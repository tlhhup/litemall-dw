package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.{OrderDetail, OriginalData}
import org.tlh.spark.util.JedisUtil
import org.tlh.dw.entity._

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object OrderSubmitProcess {

  def process(rdd: RDD[OriginalData]): Unit = {
    //1. 计算订单每个用户的订单数和金额
    val userRdd = rdd.map(item => (item.userId, OrderDetail(item.ext)))
      .groupByKey()
      .mapValues(orders => {
        val count = orders.size
        val amount = orders.map(_.actualPrice).sum
        (count, amount)
      })
    //2. 存储数据
    userRdd.foreach(item => {
      val userId = item._1
      val count = item._2._1
      val amount = item._2._2
      //2.1 记录用户
      JedisUtil.inc(USER_ORDER_COUNT + userId, count)
      JedisUtil.incrByFloat(USER_ORDER_AMOUNT + userId, amount)
      //2.2 记录订单汇总
      JedisUtil.inc(ORDER_COUNT, count)
      JedisUtil.incrByFloat(ORDER_AMOUNT, amount)
    })
  }

}
