package org.tlh.dw

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.tlh.dw.entity.OriginalData
import org.tlh.dw.process.OrderSubmitProcess

/**
  * 实时数仓计算
  *
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object DwRealTimeDriver {

  def main(args: Array[String]): Unit = {
    //1. 获取duration
    val duration = if (args.length > 0) args(0).toInt else 5
    //2. kafka参数
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kafka-master:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "litemall_real_time",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    //3. 创建spark context
    val conf = new SparkConf().setMaster("local[*]").setAppName("DwRealTimeDriver")
    val ssc = new StreamingContext(conf, Seconds(duration))
    //4. 订阅Kafka主题
    val topics = Array("litemall-wx")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    //5. 处理
    val oDs = stream.filter(record => record.value().split("#CS").length == 2)
      .map(record => OriginalData(record.value()))

    //登录
    val loginDs = oDs.filter(item => item.eventType == 1)
    loginDs.print()

    //加购
    val cartDs = oDs.filter(item => item.eventType == 2)
    cartDs.print()
    //下单
    val orderDs = oDs.filter(item => item.eventType == 3)
    orderDs.foreachRDD(rdd => {
      OrderSubmitProcess.process(rdd)
    })

    //支付
    val paymentDs = oDs.filter(item => item.eventType == 4)

    //确认收货
    val confirmDs = oDs.filter(item => item.eventType == 5)

    //退款
    val refundDs = oDs.filter(item => item.eventType == 6)

    //评论
    val commentDs = oDs.filter(item => item.eventType == 7)

    //收藏
    val collectDs = oDs.filter(item => item.eventType == 8)
    collectDs.print()

    //6. 启动
    ssc.start()
    ssc.awaitTermination()
  }

}
