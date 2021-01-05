package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity._
import org.tlh.spark.util.{DateUtil, JedisUtil}

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object PaymentProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    //1. 计算用户支付次数和金额
    val payRdd = rdd.map(item => {
      val userId = item.userId
      //4|userId|orderId|actualPrice|payId
      val attrs = item.ext.split("\\|")
      val actualPrice = attrs(3).toDouble
      (userId, (1, actualPrice))
    })
      .groupByKey()
      .mapValues(payments => {
        val count = payments.size
        val amount = payments.map(_._2).sum
        (count, amount)
      })
    //2. 存储
    payRdd.foreach(item => {
      val userId = item._1
      val count = item._2._1
      val amount = item._2._2
      //2.1 记录用户
      //      JedisUtil.inc(USER_PAY_COUNT + userId, count)
      //      JedisUtil.incrByFloat(USER_PAY_AMOUNT + userId, amount)
      //2.2 记录支付汇总
      JedisUtil.inc(DateUtil.todayFormat() + PAY_COUNT, count)
      JedisUtil.incrByFloat(DateUtil.todayFormat() + PAY_AMOUNT, amount)
    })
  }

}
