package org.tlh.warehouse.datastream.dwd.dim

import java.beans.Transient
import java.time.Duration

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.slf4j.{Logger, LoggerFactory}
import org.tlh.warehouse.entity.{MaxWellEntity, MaxWellEntitySchema}
import org.tlh.warehouse.util.{AppConfig, JedisUtils}
import redis.clients.jedis.Jedis

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-18
  */
object TransformDim2Redis extends App {

  val dim_tables = Seq(
    "litemall_goods",
    "litemall_brand",
    "litemall_category",
    "litemall_region")

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // 创建Kafka信息
  val source = KafkaSource.builder[MaxWellEntity]()
    .setBootstrapServers(AppConfig.KAFKA_SERVERS)
    .setTopics(AppConfig.kafka_input_topic)
    .setGroupId("litemall_dwd_dim")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new MaxWellEntitySchema())
    .build()

  // 添加数据源
  val stream = env.fromSource(source,
    WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2))
      .withTimestampAssigner(new SerializableTimestampAssigner[MaxWellEntity] {
        override def extractTimestamp(element: MaxWellEntity, recordTimestamp: Long): Long = element.ts * 1000
      }),
    "Kafka Source")
    .name("kafka source")
    .uid("source")

  // 过滤维度表
  stream.filter(item => dim_tables.contains(item.table) && item.data.nonEmpty)
    .uid("filter")
    .name("filter dim table")
    .addSink(new DimData2RedisSink)
    .uid("sink")
    .name("save to redis")

  env.execute("transform dim")
}

class DimData2RedisSink extends RichSinkFunction[MaxWellEntity] {

  val logger: Logger = LoggerFactory.getLogger(classOf[DimData2RedisSink])

  @Transient private[this] var jedis: Jedis = _

  override def open(parameters: Configuration): Unit = {
    jedis = JedisUtils.getResource()
    logger.info("create jedis success")
  }

  override def close(): Unit = {
    JedisUtils.release(jedis)
    logger.info("release jedis")
  }

  override def invoke(element: MaxWellEntity, context: SinkFunction.Context): Unit = {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    // 获取表名
    val table = element.table
    logger.info(s"insert table:$table")
    val key = s"litemall:dwd:dim:$table"
    // 获取ID
    val id = element.data("id").toString
    // 匹配事件
    element.`type` match {
      case e if ("insert".equals(e) || "update".equals(e) || "bootstrap-insert".equals(e)) => jedis.hset(key, id, write(element.data))
      case "delete" => jedis.hdel(key, id)
      case _ =>
    }
  }
}