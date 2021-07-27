package org.tlh.rt.dw.ods

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.json4s.{Formats, NoTypeHints}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.tlh.rt.dw.utils.KafkaUtil

/**
  * 导入历史数据
  *
  * @author 离歌笑
  * @desc
  * @date 2021-07-27
  */
object MaxWellOdsDispatcher extends App {

  //当应用被停止的时候，进行如下设置可以保证当前批次执行完之后再停止应用
  System.setProperty("spark.streaming.stopGracefullyOnShutdown", "true")

  //1. 获取duration
  val duration = if (args.length > 0) args(0).toInt else 5
  val widowDuration = duration * 3

  //2. kafka参数
  val topics = "maxwell-litemall-db"
  val groupId = "maxwell"

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "kafka-master:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> groupId,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  //3. 创建spark context
  val conf = new SparkConf().setMaster("local[*]").setAppName("MaxWellOdsDispatcher")
  val scc = new StreamingContext(conf, Seconds(duration))

  //5. 读取redis中的offset
  var stream: InputDStream[ConsumerRecord[String, String]] = null
  val offSets = KafkaUtil.readOffSet(topics, groupId)
  if (offSets != null) {
    stream = KafkaUtils.createDirectStream[String, String](
      scc,
      PreferConsistent,
      Subscribe[String, String](Array(topics), kafkaParams, offSets)
    )
  } else {
    stream = KafkaUtils.createDirectStream[String, String](
      scc,
      PreferConsistent,
      Subscribe[String, String](Array(topics), kafkaParams)
    )
  }

  var offsetRanges: Array[OffsetRange] = Array.empty[OffsetRange]

  val offSetDs: DStream[ConsumerRecord[String, String]] = stream.transform(rdd => {
    offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    rdd
  })

  //6. 读取Kafka中的数据
  implicit val formats: Formats = Serialization.formats(NoTypeHints)
  offSetDs.map(item => parse(item.value()))
    .foreachRDD(rdd => {
      rdd.foreachPartition(iter => {

        // 创建生产者
        val sender = KafkaUtil.buildKafkaSender("kafka-master:9092")

        for (item <- iter) {
          // 处理类型
          (item \ "type").extract[String] match {
            case "bootstrap-insert" => {
              // 解析表名
              val table = "ods_" + (item \ "table").extract[String]
              // 解析数据
              val data = write((item \ "data").extract[Map[String, Any]])

              //7. 处理数据，转发到独立的ods表中
              sender.send(new ProducerRecord[String, String](table, data))
            }
            case _ =>
          }
        }

        // 释放资源
        sender.close()
      })
      //8. 更新offset
      KafkaUtil.saveOffSet(topics, groupId, offsetRanges)
    })


  scc.start()
  scc.awaitTermination()

}
