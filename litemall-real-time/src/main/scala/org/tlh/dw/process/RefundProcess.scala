package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity._
import org.tlh.spark.util.JedisUtil

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object RefundProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    //1. 计算用户退款次数和金额
    val payRdd = rdd.map(item => {
      val userId = item.userId
      //6|userId|orderId|actualPrice
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
      JedisUtil.inc(USER_REFUND_COUNT + userId, count)
      JedisUtil.incrByFloat(USER_REFUND_AMOUNT + userId, amount)
      //2.2 记录退款汇总
      JedisUtil.inc(REFUND_COUNT, count)
      JedisUtil.incrByFloat(REFUND_AMOUNT, amount)
    })
  }

}
