package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity._
import org.tlh.spark.util.{DateUtil, JedisUtil}

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object OrderConfirmProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    //1. 计算收货金额
    rdd.foreach(item => {
      // 5|userId|orderId|actualPrice
      val attrs = item.ext.split("\\|")
      val amount = attrs(3).toDouble
      JedisUtil.inc(DateUtil.todayFormat() + CONFIRM_COUNT, 1)
      JedisUtil.incrByFloat(DateUtil.todayFormat() + CONFIRM_AMOUNT, amount)
    })
  }

}
