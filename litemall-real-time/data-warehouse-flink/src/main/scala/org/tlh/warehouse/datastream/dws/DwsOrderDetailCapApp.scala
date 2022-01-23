package org.tlh.warehouse.datastream.dws

import java.beans.Transient
import java.time.Duration

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor, StateTtlConfig}
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector
import org.tlh.warehouse.entity.{OrderDetailCap, OrderDetailWide, OrderWide}
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-23
  */
object DwsOrderDetailCapApp extends App {

  val out_topic = "dws_order_detail_cap"

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)

  // 设置hdfs用户
  System.setProperty("HADOOP_USER_NAME", AppConfig.flink_ck_user)

  // 设置状态后端 增量ck
  val stateBackend = new EmbeddedRocksDBStateBackend(true)
  env.setStateBackend(stateBackend)
  // 配置checkpoint
  env.enableCheckpointing(60 * 1000)
  // 设置存储目录
  env.getCheckpointConfig.setCheckpointStorage(AppConfig.flink_ck_dir + out_topic)
  // 设置同时进行的ck数
  env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
  // 设置多长时间丢弃ck
  env.getCheckpointConfig.setCheckpointTimeout(10 * 60 * 1000)
  // 设置ck间的最小间隙
  env.getCheckpointConfig.setMinPauseBetweenCheckpoints(10 * 1000)
  // 设置ck模式
  env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
  // 设置容错数
  env.getCheckpointConfig.setTolerableCheckpointFailureNumber(2)
  // job取消后保留ck
  env.getCheckpointConfig.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
  // 开始实验特征
  env.getCheckpointConfig.enableUnalignedCheckpoints()
  // 快照压缩
  env.getConfig.setUseSnapshotCompression(true)

  // 获取订单信息
  var source = KafkaSource.builder[String]()
    .setBootstrapServers(AppConfig.KAFKA_SERVERS)
    .setTopics("dwd_fact_order_info")
    .setGroupId("litemall_dws_fact_order")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()

  val orderStream = env.fromSource(source,
    WatermarkStrategy.noWatermarks(),
    "Kafka_Source_order")
    .name("kafka_order_source_order")
    .uid("source_order")

  // 转化数据
  val orderWideDs = orderStream.map(item => OrderWide(item))
    .assignTimestampsAndWatermarks(
      WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2))
        .withTimestampAssigner(new SerializableTimestampAssigner[OrderWide] {
          override def extractTimestamp(element: OrderWide, recordTimestamp: Long): Long = element.add_time.getTime
        })
    )


  // 获取订单详情数据
  source = KafkaSource.builder[String]()
    .setBootstrapServers(AppConfig.KAFKA_SERVERS)
    .setTopics("dwd_fact_order_detail")
    .setGroupId("litemall_dws_fact_order_detail")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()

  val orderDetailStream = env.fromSource(source,
    WatermarkStrategy.noWatermarks(),
    "Kafka_Source_order_detail")
    .name("kafka_order_source_order_detail")
    .uid("source_order_detail")

  // 转化数据
  val orderDetailDs = orderDetailStream.map(item => OrderDetailWide(item))
    .assignTimestampsAndWatermarks(
      WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2))
        .withTimestampAssigner(new SerializableTimestampAssigner[OrderDetailWide] {
          override def extractTimestamp(element: OrderDetailWide, recordTimestamp: Long): Long = element.add_time.getTime
        })
    )

  // 将两个流进行join 执行的是interval join
  orderWideDs
    .keyBy(_.id)
    .intervalJoin(orderDetailDs.keyBy(_.order_id))
    .between(Time.seconds(-60), Time.seconds(60))
    .process(new OrderDetailIntervalJoinProcess)
    .name("interval_join")
    .uid("order_detail_cap")
    .print()


  env.execute("DwsOrderDetailCapApp")
}

class OrderDetailIntervalJoinProcess extends ProcessJoinFunction[OrderWide, OrderDetailWide, OrderDetailCap] {

  // 记录历史累加
  @Transient var orderSumState: MapState[Int, Double] = _
  // 记录历史均摊
  @Transient var orderCapSumState: MapState[Int, Double] = _

  override def open(parameters: Configuration): Unit = {
    import org.apache.flink.api.common.time.Time
    // 设置状态的ttl
    val ttlConfig = StateTtlConfig
      .newBuilder(Time.seconds(60)) //设置过期时间
      .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
      .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
      .build

    // 设置订单历史累加
    val orderSumStateDesc = new MapStateDescriptor[Int, Double]("order_sum", classOf[Int], classOf[Double])
    orderSumStateDesc.enableTimeToLive(ttlConfig)
    orderSumState = getRuntimeContext.getMapState(orderSumStateDesc)

    // 设置订单均摊累加
    val orderCapSumStateDesc = new MapStateDescriptor[Int, Double]("order_cap_sum", classOf[Int], classOf[Double])
    orderCapSumStateDesc.enableTimeToLive(ttlConfig)
    orderCapSumState = getRuntimeContext.getMapState(orderCapSumStateDesc)
  }

  override def processElement(left: OrderWide,
                              right: OrderDetailWide,
                              ctx: ProcessJoinFunction[OrderWide, OrderDetailWide, OrderDetailCap]#Context,
                              out: Collector[OrderDetailCap]): Unit = {
    // 构建基础数据
    val orderWide = OrderDetailCap(right)

    // 处理均摊数据
    // 获取订单历史累加
    val order_his_sum = orderSumState.get(left.id)
    // 当前商品金额
    val current_order_sum = right.price * right.number

    // 获取订单历史累加
    val order_cap_sum_his = orderCapSumState.get(left.id)
    var current_order_cap: Double = 0
    // 判断是否是最后一个订单详情  商品总金额=历史商品总金额+当前详情的金额
    if (left.goods_price == order_his_sum + current_order_sum) {
      // 最后一个订单详情
      current_order_cap = left.actual_price - order_cap_sum_his
    } else {
      current_order_cap = left.actual_price * (current_order_sum / left.goods_price)
    }

    orderWide.capitation_price = current_order_cap

    // 更新状态
    orderSumState.put(left.id, (order_his_sum + current_order_sum))
    orderCapSumState.put(left.id, (order_cap_sum_his + current_order_cap))

    // 返回数据
    out.collect(orderWide)
  }
}
