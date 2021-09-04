package org.tlh.rt.dw.dwd.fact

import org.apache.hadoop.conf.Configuration
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
import org.tlh.rt.dw.entity.{OrderInfo, UserOrderStatus}
import org.tlh.rt.dw.utils.{AppConf, DwSerializers, KafkaUtil, PhoenixUtils}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-28
  */
object DwdFactOrderApp extends App {

  //当应用被停止的时候，进行如下设置可以保证当前批次执行完之后再停止应用
  System.setProperty("spark.streaming.stopGracefullyOnShutdown", "true")

  //1. 获取duration
  val duration = if (args.length > 0) args(0).toInt else 5
  val widowDuration = duration * 3

  //2. kafka参数
  val topics = "ods_litemall_order"
  val groupId = "order"

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> AppConf.KAFKA_SERVERS,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> groupId,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  //3. 创建spark context
  val conf = new SparkConf().setMaster("local[*]").setAppName("DwdFactOrderApp")
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

  // 读取region数据放入广播变量
  // 读取数据
  val regionMap = PhoenixUtils.queryRegion()
  // 创建广播变量
  val regionMapBD = scc.sparkContext.broadcast(regionMap)

  //6. 读取Kafka中的数据
  implicit val formats: Formats = DefaultFormats ++ DwSerializers.all

  //6.1 处理基础数据
  val orderDs = offSetDs.map(item => parse(item.value()).extract[OrderInfo])
    .mapPartitions(rdd => {
      val orders = rdd.toList

      // 获取该分区中的所有用户
      val userIds = orders.map(_.user_id)
      if (!userIds.isEmpty) {
        val region = regionMapBD.value

        // 加载首单信息
        val userOderList = PhoenixUtils.queryUserOrder(userIds)

        // 加载用户信息
        val userMap = PhoenixUtils.queryUser(userIds)

        for (order <- orders) {
          // 设置region信息
          order.province_name = region(order.province).name
          order.province_code = region(order.province).code
          order.city_name = region(order.city).name
          order.city_code = region(order.city).code
          order.country_name = region(order.country).name
          order.country_code = region(order.country).code

          // 设置用户信息
          order.user_gender = userMap(order.user_id).gender
          order.user_age_group = userMap(order.user_id).ageGroup

          // 设置是否是首单
          order.is_first_order = if (userOderList.contains(order.user_id)) false else true
        }
      }
      orders.iterator
    })

  //6.2 处理同一批次中同一用户同时下了多个订单问题
  val fixMultiOrdersDs = orderDs.map(item => (item.user_id, item))
    .groupByKey()
    .flatMap {
      case (_, orders) => {
        val orderedOrders = orders.toList.sortWith((a, b) => a.add_time.before(b.add_time))
        for (item <- orderedOrders.zipWithIndex) {
          item._1.is_first_order = if (item._2 == 0) item._1.is_first_order else false
        }

        orderedOrders
      }
    }

  // 缓存数据
  val resultDs = fixMultiOrdersDs.cache()

  //7. 保存数据
  //7.1 将数据保存到Kafka中
  resultDs.foreachRDD(rdd => {
    rdd.foreachPartition(iter => {
      val producer = KafkaUtil.buildKafkaSender(AppConf.KAFKA_SERVERS)

      iter.foreach(item => {
        producer.send(new ProducerRecord[String, String]("dwd_fact_order", write(item)))
      })

      producer.close()
    })
  })

  // todo 将数据发送到ES中

  //7.2 更新用户订单记录
  resultDs.foreachRDD(rdd => {
    import org.apache.phoenix.spark._
    rdd.map(item => UserOrderStatus(item.user_id, true))
      .saveToPhoenix(
        "litemall.user_order_status",
        Seq("USER_ID", "IS_FIRST"),
        new Configuration,
        zkUrl = Some(AppConf.HBASE_ZK)
      )

    //8. 更新offset
    KafkaUtil.saveOffSet(topics, groupId, offsetRanges)
  })

  scc.start()
  scc.awaitTermination()

}
