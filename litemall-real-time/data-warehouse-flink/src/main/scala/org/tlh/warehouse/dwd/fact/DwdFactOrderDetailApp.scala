package org.tlh.warehouse.dwd.fact

import java.beans.Transient
import java.time.Duration
import java.util.{Optional, Properties}

import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read
import org.slf4j.{Logger, LoggerFactory}
import org.tlh.warehouse.entity.{GoodsBrand, GoodsCategory, GoodsItem, OrderDetail, OrderDetailWide}
import org.tlh.warehouse.util.{AppConfig, JedisUtils}
import redis.clients.jedis.Jedis

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-21
  */
object DwdFactOrderDetailApp extends App {

  val topic = Seq("ods_litemall_order_goods")
  val out_topic = "dwd_fact_order_detail"

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
  // 设置ck间的最小间隙 该值应该长于ck的时间
  env.getCheckpointConfig.setMinPauseBetweenCheckpoints(100 * 1000)
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

  // 创建Kafka信息
  val source = KafkaSource.builder[String]()
    .setBootstrapServers(AppConfig.KAFKA_SERVERS)
    .setTopics(topic: _*)
    .setGroupId("litemall_dwd_fact_order_detail")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()

  // 添加数据源
  val stream = env.fromSource(source,
    WatermarkStrategy.noWatermarks(),
    "Kafka Source")
    .name("kafka source")
    .uid("source")

  // 转化数据设置水位线
  val orderDetailDs = stream.map(item => OrderDetail(item))
    .assignTimestampsAndWatermarks(
      WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2))
        .withTimestampAssigner(new SerializableTimestampAssigner[OrderDetail] {
          override def extractTimestamp(element: OrderDetail, recordTimestamp: Long): Long = element.add_time.getTime
        })
    )

  // 拉宽数据
  val orderDetailWideDs = orderDetailDs
    .map(new OrderDetailWideMap)
    .name("fill_goods")
    .uid("fill_goods")

  // 数据保存到Kafka
  val properties = new Properties
  properties.setProperty("bootstrap.servers", AppConfig.KAFKA_SERVERS)
  properties.setProperty("transaction.timeout.ms", s"${60 * 5 * 1000}")
  val kafkaSink = new FlinkKafkaProducer[String](
    out_topic,
    new SimpleStringSchema(),
    properties,
    null,
    Semantic.EXACTLY_ONCE,
    3
  )

  orderDetailWideDs
    .map(item => item.toJson())
    .name("convert_to_str_order_detail")
    .uid("to_str_order_detail")
    .addSink(kafkaSink)
    .name("kakfa_sink_order_detail")
    .uid("sink_kafka_order_detail")

  // todo 数据保存到clickhouse

  env.execute("DwdFactOrderDetailApp")
}

class OrderDetailWideMap extends RichMapFunction[OrderDetail, OrderDetailWide] {

  private val logger: Logger = LoggerFactory.getLogger(classOf[OrderDetailWideMap])

  @Transient private[this] var jedis: Jedis = _

  override def open(parameters: Configuration): Unit = {
    logger.info("init redis client")
    jedis = JedisUtils.getResource()
  }

  override def close(): Unit = {
    logger.info("release redis")
    JedisUtils.release(jedis)
  }

  override def map(element: OrderDetail): OrderDetailWide = {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)

    // 处理基础数据
    val orderDetailWide = OrderDetailWide(element)
    // 填充商品信息
    val goods_id = element.goods_id
    var key = "litemall:dwd:dim:litemall_goods"
    var json = jedis.hget(key, goods_id.toString)
    if (StringUtils.isNotBlank(json)) {
      val goodsItem = read[GoodsItem](json)
      if (goodsItem != null) {
        // 商品品牌
        key = "litemall:dwd:dim:litemall_brand"
        json = jedis.hget(key, goodsItem.brand_id.toString)
        if (StringUtils.isNotBlank(json)) {
          Optional.of(read[GoodsBrand](json)).ifPresent(item => {
            orderDetailWide.brand_name = item.name
          })
        }
        // 商品分类
        key = "litemall:dwd:dim:litemall_category"
        json = jedis.hget(key, goodsItem.category_id.toString)
        if (StringUtils.isNotBlank(json)) {
          Optional.of(read[GoodsCategory](json)).ifPresent(item => {
            // 设置一级分类
            orderDetailWide.first_category_id = item.id
            orderDetailWide.first_category_name = item.name

            // 设置二级分类
            json = jedis.hget(key, item.pid.toString)
            if (StringUtils.isNotBlank(json)) {
              Optional.of(read[GoodsCategory](json)).ifPresent(item => {
                orderDetailWide.second_category_id = item.id
                orderDetailWide.second_category_name = item.name
              })
            }
          })
        }
      }
    }

    orderDetailWide
  }
}
