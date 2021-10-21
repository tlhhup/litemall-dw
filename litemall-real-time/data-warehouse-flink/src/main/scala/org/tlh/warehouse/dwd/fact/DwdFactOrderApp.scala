package org.tlh.warehouse.dwd.fact

import java.beans.Transient
import java.time.Duration
import java.util.Properties

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
import org.json4s.jackson.Serialization.{read, write}
import org.slf4j.{Logger, LoggerFactory}
import org.tlh.warehouse.entity.{Order, OrderWide, Region}
import org.tlh.warehouse.util.{AppConfig, JedisUtils}
import redis.clients.jedis.Jedis

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-19
  */
object DwdFactOrderApp extends App {

  val topic = Seq("ods_litemall_order")
  val out_topic = "dwd_fact_litemall_order"

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

  // 创建Kafka信息
  val source = KafkaSource.builder[String]()
    .setBootstrapServers(AppConfig.KAFKA_SERVERS)
    .setTopics(topic: _*)
    .setGroupId("litemall_dwd_fact_order")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()

  // 添加数据源
  val stream = env.fromSource(source,
    WatermarkStrategy.noWatermarks(),
    "Kafka Source")
    .name("kafka source")
    .uid("source")

  // 转化数据
  val orderDs = stream.map(item => Order(item))
    .assignTimestampsAndWatermarks(
      WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2))
        .withTimestampAssigner(new SerializableTimestampAssigner[Order] {
          override def extractTimestamp(element: Order, recordTimestamp: Long): Long = element.add_time.getTime
        })
    )

  // 将数据拉宽
  val orderWideDs = orderDs
    .map(new OrderWideMap)
    .name("process_region")
    .uid("region")

  // 将数据保存到Kafka
  val properties = new Properties
  properties.setProperty("bootstrap.servers", AppConfig.KAFKA_SERVERS)
  properties.setProperty("transaction.timeout.ms", s"${60 * 5 * 1000}")
  val kafkaSink = new FlinkKafkaProducer[String](
    out_topic,
    new SimpleStringSchema(),
    properties,
    null,
    Semantic.EXACTLY_ONCE,
    FlinkKafkaProducer.DEFAULT_KAFKA_PRODUCERS_POOL_SIZE
  )
  implicit val formats: Formats = Serialization.formats(NoTypeHints)
  orderWideDs
    .map(item => write(item))
    .name("convert_to_str")
    .uid("to_str")
    .addSink(kafkaSink)
    .name("kakfa_sink")
    .uid("sink_kafka")

  // 将数据保存到clickhouse

  env.execute("DwdFactOrderApp")
}

class OrderWideMap extends RichMapFunction[Order, OrderWide] {

  private val logger: Logger = LoggerFactory.getLogger(classOf[OrderWideMap])

  @Transient private[this] var jedis: Jedis = _

  override def open(parameters: Configuration): Unit = {
    logger.info("init redis client")
    jedis = JedisUtils.getResource()
  }

  override def close(): Unit = {
    logger.info("release redis")
    JedisUtils.release(jedis)
  }

  override def map(order: Order): OrderWide = {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    // 基础数据
    val orderWide = OrderWide(
      order.id,
      order.user_id,
      order.order_sn,
      order.order_status,
      order.goods_price,
      order.freight_price,
      order.coupon_price,
      order.integral_price,
      order.groupon_price,
      order.order_price,
      order.actual_price,
      order.add_time,
      order.province, order.city, order.country)
    // 处理redis中的维度数据
    val key = "litemall:dwd:dim:litemall_region"
    // 读取省份信息
    var json = jedis.hget(key, order.province.toString)
    if (StringUtils.isNotBlank(json)) {
      orderWide.province_name = read[Region](json).name
    }
    // 城市名称
    json = jedis.hget(key, order.city.toString)
    if (StringUtils.isNotBlank(json)) {
      orderWide.city_name = read[Region](json).name
    }
    // 乡镇信息
    json = jedis.hget(key, order.country.toString)
    if (StringUtils.isNotBlank(json)) {
      orderWide.country_name = read[Region](json).name
    }

    orderWide
  }
}