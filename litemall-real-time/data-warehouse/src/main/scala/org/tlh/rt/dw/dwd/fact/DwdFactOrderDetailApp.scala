package org.tlh.rt.dw.dwd.fact

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization.write
import org.tlh.rt.dw.entity.OrderDetail
import org.tlh.rt.dw.utils.{AppConf, DwSerializers, KafkaUtil, PhoenixUtils}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-30
  */
object DwdFactOrderDetailApp extends App {

  //当应用被停止的时候，进行如下设置可以保证当前批次执行完之后再停止应用
  System.setProperty("spark.streaming.stopGracefullyOnShutdown", "true")

  //1. 获取duration
  val duration = if (args.length > 0) args(0).toInt else 5
  val widowDuration = duration * 3

  //2. kafka参数
  val topics = "ods_litemall_order_goods"
  val groupId = "order_detail"

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> AppConf.KAFKA_SERVERS,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> groupId,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  //3. 创建spark context
  val conf = new SparkConf().setMaster("local[*]").setAppName("DwdFactOrderDetailApp")
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
  implicit val formats: Formats = DefaultFormats ++ DwSerializers.all
  val orderDs = offSetDs.map(item => parse(item.value()).extract[OrderDetail])
    .mapPartitions(rdd => {
      val orders = rdd.toList

      // 获取该分区中的所有sku
      val skuIds = orders.map(_.goods_id)
      if (!skuIds.isEmpty) {
        val skus = PhoenixUtils.querySku(skuIds)
        for (order <- orders) {
          order.brand_id = skus(order.goods_id).brand_id
          order.brand_name = skus(order.goods_id).brand_name
          order.category_id= skus(order.goods_id).category_id
          order.category_name = skus(order.goods_id).category_name
        }
      }
      orders.iterator
    })

  //7.  将数据保存到Kafka中
  orderDs.foreachRDD(rdd => {
    rdd.foreachPartition(iter => {
      val producer = KafkaUtil.buildKafkaSender(AppConf.KAFKA_SERVERS)

      iter.foreach(item => {
        producer.send(new ProducerRecord[String, String]("dwd_fact_order_detail", write(item)))
      })

      producer.close()
    })
    //8. 更新offset
    KafkaUtil.saveOffSet(topics, groupId, offsetRanges)
  })

  scc.start()
  scc.awaitTermination()

}
