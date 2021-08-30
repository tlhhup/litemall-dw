package org.tlh.rt.dw.dws

import org.apache.commons.lang3.StringUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, Formats}
import org.tlh.rt.dw.entity.{OrderDetail, OrderInfo, OrderWide}
import org.tlh.rt.dw.utils.{AppConf, DwSerializers, KafkaUtil}
import org.tlh.spark.util.JedisUtil

import collection.mutable

/**
  *
  * 双流join
  *
  * @author 离歌笑
  * @desc
  * @date 2021-07-30
  */
object DwsOrderDetailCapitationApp extends App {

  //当应用被停止的时候，进行如下设置可以保证当前批次执行完之后再停止应用
  System.setProperty("spark.streaming.stopGracefullyOnShutdown", "true")

  //1. 获取duration
  val duration = if (args.length > 0) args(0).toInt else 5
  val widowDuration = duration * 10

  //2. 创建spark context
  val conf = new SparkConf().setMaster("local[*]").setAppName("DwdDimSkuApp")
  val scc = new StreamingContext(conf, Seconds(duration))

  //3. 读取Kafka数据
  //3.1 读取订单事实表数据
  val orderTopics = "dwd_fact_order"
  val orderGroupId = "order"

  var kafkaParams = Map[String, Object](
    "bootstrap.servers" -> AppConf.KAFKA_SERVERS,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> orderGroupId,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  var orderDs: InputDStream[ConsumerRecord[String, String]] = null
  val orderOffSets = KafkaUtil.readOffSet(orderTopics, orderGroupId)
  if (orderOffSets != null) {
    orderDs = KafkaUtils.createDirectStream[String, String](
      scc,
      PreferConsistent,
      Subscribe[String, String](Array(orderTopics), kafkaParams, orderOffSets)
    )
  } else {
    orderDs = KafkaUtils.createDirectStream[String, String](
      scc,
      PreferConsistent,
      Subscribe[String, String](Array(orderTopics), kafkaParams)
    )
  }

  var orderOffsetRanges: Array[OffsetRange] = Array.empty[OffsetRange]

  val orderOffSetDs: DStream[ConsumerRecord[String, String]] = orderDs.transform(rdd => {
    orderOffsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    rdd
  })

  //3.2 读取订单详情事实表数据
  val orderDetailTopics = "dwd_fact_order_detail"
  val orderDetailGroupId = "order_detail"

  kafkaParams = Map[String, Object](
    "bootstrap.servers" -> AppConf.KAFKA_SERVERS,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> orderDetailGroupId,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  var orderDetailDs: InputDStream[ConsumerRecord[String, String]] = null
  val orderDetailOffSets = KafkaUtil.readOffSet(orderDetailTopics, orderDetailGroupId)
  if (orderDetailOffSets != null) {
    orderDetailDs = KafkaUtils.createDirectStream[String, String](
      scc,
      PreferConsistent,
      Subscribe[String, String](Array(orderDetailTopics), kafkaParams, orderDetailOffSets)
    )
  } else {
    orderDetailDs = KafkaUtils.createDirectStream[String, String](
      scc,
      PreferConsistent,
      Subscribe[String, String](Array(orderDetailTopics), kafkaParams)
    )
  }

  var orderDetailOffsetRanges: Array[OffsetRange] = Array.empty[OffsetRange]

  val orderDetailOffSetDs: DStream[ConsumerRecord[String, String]] = orderDetailDs.transform(rdd => {
    orderDetailOffsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    rdd
  })

  //4. 转化数据
  implicit val formats: Formats = DefaultFormats ++ DwSerializers.all

  val orderInfoDs = orderOffSetDs.map(item => parse(item.value()).extract[OrderInfo])

  val orderDetailInfoDs = orderDetailOffSetDs.map(item => parse(item.value()).extract[OrderDetail])

  //5. 开窗
  val orderWindow = orderInfoDs
    .window(Seconds(widowDuration), Seconds(duration))
    .map(item => (item.id, item))

  val orderDetailWindow = orderDetailInfoDs
    .window(Seconds(widowDuration), Seconds(duration))
    .map(item => (item.order_id.toLong, item))

  //6. join
  val orderWideDs = orderWindow.join(orderDetailWindow)
    .mapPartitions(iter => {
      // 去重
      val jedis = JedisUtil.getJedis

      val orderWides = new mutable.ArrayBuffer[OrderWide]()

      for ((orderId, (order, orderDetail)) <- iter) {
        // 构建key
        val key = s"order_joined:$orderId"
        // 添加数据
        val ifNotExists = jedis.sadd(key, orderDetail.id.toString)
        // 设置过期时间, 只有命中该订单就重新设置过期时间
        jedis.expire(key, 600)
        // 如果没有处理过
        if (ifNotExists == 1L) {
          orderWides.append(OrderWide(order, orderDetail))
        }
      }

      jedis.close()

      orderWides.toIterator
    })

  //7. 处理均摊问题
  val resultDs: DStream[OrderWide] = orderWideDs
    .mapPartitions(iter => {
      val jedis = JedisUtil.getJedis
      // 订单详情款表
      val orderWides = iter.toList
      // actual_price = order_price - integral_price
      // order_price = goods_price + freight_price - coupon_price
      for (orderWide <- orderWides) {
        // 获取该订单的历史总和
        val key = s"order_his_total:${orderWide.orderId}"
        val historySum = if (StringUtils.isNotBlank(jedis.get(key))) jedis.get(key).toDouble else 0D

        // 获取该订单的历史均摊
        val capitationKey = s"order_capitation_total:${orderWide.orderId}"
        val capitationSum = if (StringUtils.isNotBlank(jedis.get(capitationKey))) jedis.get(capitationKey).toDouble else 0D

        // 计算当前详情商品价格历史总和是否等于 goods_price
        // 当前商品的总价
        val itemTotalAmount = orderWide.price * orderWide.number
        // 最后一笔订单
        if (itemTotalAmount + historySum == orderWide.goods_price) {
          orderWide.capitation_price = orderWide.actual_price - capitationSum
        } else {
          orderWide.capitation_price = orderWide.actual_price * (itemTotalAmount / orderWide.goods_price)
        }

        // 更新redis数据
        // 更新历史总和
        jedis.setex(key, 600, (itemTotalAmount + historySum).toString)
        jedis.setex(capitationKey, 600, (capitationSum + orderWide.capitation_price).toString)
      }

      jedis.close()

      orderWides.toIterator
    })

  val resultDSCache = resultDs.cache()

  // 将数据存储到clickhouse 便于olap(ad-hoc 即席查询)
  resultDSCache.foreachRDD(rdd => {

    val spark = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
    import spark.implicits._
    rdd.toDF()
      .write
      .mode(SaveMode.Append)
      .format("jdbc")
      .option("url",AppConf.CLICKHOUSE_URL)
      .option("driver",AppConf.CLICKHOUSE_DRIVER)
      .option("dbtable", "litemall.dws_order_wide_all")
      .option("user", AppConf.CLICKHOUSE_USER)
      .save()
  })


  // 8. 将数据存储到kafka
  resultDSCache.foreachRDD(rdd => {
    rdd.foreachPartition(iter => {
      val producer = KafkaUtil.buildKafkaSender(AppConf.KAFKA_SERVERS)

      iter.foreach(item => {
        producer.send(new ProducerRecord[String, String]("dws_order_wide", write(item)))
      })

      producer.close()
    })

    // 更新offset
    KafkaUtil.saveOffSet(orderTopics, orderGroupId, orderOffsetRanges)
    KafkaUtil.saveOffSet(orderDetailTopics, orderDetailGroupId, orderDetailOffsetRanges)
  })


  scc.start()
  scc.awaitTermination()
}
