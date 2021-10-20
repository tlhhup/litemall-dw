package org.tlh.warehouse.ods

import java.beans.Transient
import java.time.Duration

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.slf4j.{Logger, LoggerFactory}
import org.tlh.warehouse.entity.{MaxWellEntity, MaxWellEntitySchema}
import org.tlh.warehouse.util.{AppConfig, KafkaUtil}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-19
  */
object MaxWellDispatcher extends App {

  val attention_tables = Seq(
    "litemall_cart",
    "litemall_collect",
    "litemall_comment",
    "litemall_order",
    "litemall_order_goods")

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // 创建Kafka信息
  val source = KafkaSource.builder[MaxWellEntity]()
    .setBootstrapServers(AppConfig.KAFKA_SERVERS)
    .setTopics(AppConfig.kafka_input_topic)
    .setGroupId("litemall_maxwell_dispatcher")
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

  // 过滤表 及校验数据合法性
  stream.filter(item => attention_tables.contains(item.table) && item.data.nonEmpty)
    .uid("filter")
    .name("filter table")
    .addSink(new KafkaSink) // 将数据转存到kafka
    .uid("sink")
    .name("save to kafka")

  env.execute("export to kafka")

}

class KafkaSink extends RichSinkFunction[MaxWellEntity] {

  private[this] val logger: Logger = LoggerFactory.getLogger(classOf[KafkaSink])

  @Transient private[this] var producer: KafkaProducer[String, String] = _

  override def open(parameters: Configuration): Unit = {
    logger.info("init kafka producer")
    producer = KafkaUtil.buildKafkaSender(AppConfig.KAFKA_SERVERS)
  }

  override def close(): Unit = {
    if (producer != null) {
      producer.close()
      logger.info("release kafka producer")
    }
  }

  override def invoke(element: MaxWellEntity, context: SinkFunction.Context): Unit = {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    // 获取表名
    val topic_name = s"ods_${element.table}"
    logger.info("send data to {}", topic_name)
    element.`type` match {
      case "insert" => {
        // 将数据发送到Kafka中
        producer.send(new ProducerRecord[String, String](topic_name, write(element.data)))
      }
      case _ =>
    }
  }
}
