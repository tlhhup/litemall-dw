package org.tlh.rt.dw.dwd.dim

import org.apache.hadoop.conf.Configuration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods.parse
import org.tlh.rt.dw.entity.GoodsSku
import org.tlh.rt.dw.utils.{DwSerializers, KafkaUtil, PhoenixUtils}


/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-30
  */
object DwdDimSkuApp extends App {

  //当应用被停止的时候，进行如下设置可以保证当前批次执行完之后再停止应用
  System.setProperty("spark.streaming.stopGracefullyOnShutdown", "true")

  //1. 获取duration
  val duration = if (args.length > 0) args(0).toInt else 5
  val widowDuration = duration * 3

  //2. kafka参数
  val topics = "ods_litemall_goods"
  val groupId = "sku"

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "kafka-master:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> groupId,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  //3. 创建spark context
  val conf = new SparkConf().setMaster("local[*]").setAppName("DwdDimSkuApp")
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

  //创建广播变量
  val brands = PhoenixUtils.queryBrands()
  val brandsBD = scc.sparkContext.broadcast(brands)
  val categories = PhoenixUtils.queryCategories()
  val categoriesBD = scc.sparkContext.broadcast(categories)


  //6. 读取Kafka中的数据
  implicit val formats: Formats = DefaultFormats ++ DwSerializers.all
  offSetDs.map(item => {

    val brand = brandsBD.value
    val category = categoriesBD.value

    val sku = parse(item.value()).extract[GoodsSku]
    sku.brand_name = brand(sku.brand_id).name
    sku.category_name = category(sku.category_id).name

    sku
  })
    .foreachRDD(rdd => {

      //7. 将数据保存到hbase中
      import org.apache.phoenix.spark._
      rdd.saveToPhoenix(
        "LITEMALL.DWD_DIM_SKU",
        Seq("ID", "NAME", "CATEGORY_ID", "BRAND_ID", "CATEGORY_NAME", "BRAND_NAME"),
        new Configuration,
        Some("hadoop-master:2181")
      )

      //8. 更新offset
      KafkaUtil.saveOffSet(topics, groupId, offsetRanges)
    })


  scc.start()
  scc.awaitTermination()

}
