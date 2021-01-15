package org.tlh.dw

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.tlh.dw.entity.OriginalData
import org.tlh.dw.process._

/**
  * 实时数仓计算
  *
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object DwRealTimeDriver {

  def main(args: Array[String]): Unit = {

    //当应用被停止的时候，进行如下设置可以保证当前批次执行完之后再停止应用
    System.setProperty("spark.streaming.stopGracefullyOnShutdown", "true")

    //1. 获取duration
    val duration = if (args.length > 0) args(0).toInt else 5
    val widowDuration = duration * 3
    val slideDuration = duration * 2
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
    // val conf = new SparkConf().setMaster("local[*]").setAppName("DwRealTimeDriver")
    val conf = new SparkConf().setAppName("DwRealTimeDriver")
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
    oDs.foreachRDD(rdd => {
      LoginProcess.process(rdd)
    })

    //加购
    val cartDs = oDs.filter(item => item.eventType == 2)
    cartDs.foreachRDD(rdd => {
      AddCartProcess.process(rdd)
    })

    //下单
    val orderDs = oDs.filter(item => item.eventType == 3)
    orderDs.foreachRDD(rdd => {
      OrderSubmitProcess.process(rdd)
    })

    // 区域订单
    orderDs.foreachRDD(rdd=>{
      RegionOrderProcess.process(rdd)
    })

    // 订单处理速度
    orderDs.window(Seconds(widowDuration), Seconds(slideDuration))
      .foreachRDD(rdd => {
        OrderSubmitProcess.speedProcess(rdd.count(),widowDuration)
      })

    //支付
    val paymentDs = oDs.filter(item => item.eventType == 4)
    paymentDs.foreachRDD(rdd => {
      PaymentProcess.process(rdd)
    })

    //确认收货
    val confirmDs = oDs.filter(item => item.eventType == 5)
    confirmDs.foreachRDD(rdd => {
      OrderConfirmProcess.process(rdd)
    })

    //退款
    val refundDs = oDs.filter(item => item.eventType == 6)
    refundDs.foreachRDD(rdd => {
      RefundProcess.process(rdd)
    })

    //评论
    val commentDs = oDs.filter(item => item.eventType == 7)
    commentDs.foreachRDD(rdd => {
      CommentProcess.process(rdd)
    })

    //收藏
    val collectDs = oDs.filter(item => item.eventType == 8)
    collectDs.foreachRDD(rdd => {
      CollectProcess.process(rdd)
    })

    //注册
    val registerDs = oDs.filter(item => item.eventType == 9)
    registerDs.foreachRDD(rdd => {
      RegisterProcess.process(rdd)
    })

    //6. 启动
    ssc.start()
    ssc.awaitTermination()
  }

}
