package org.tlh.dw.process

import java.util.List

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.{OrderDetail, OriginalData}
import org.tlh.spark.util.JedisUtil
import org.tlh.dw.entity._

import collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object OrderSubmitProcess extends AbstractProcess {

  def process(rdd: RDD[OriginalData]): Unit = {
    val temp = rdd.map(item => (item.userId, OrderDetail(item.ext)))
    //1. 计算订单每个用户的订单数和金额
    val userRdd = temp
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
    //3. 商品订单
    temp.flatMap(item => {
      val orderDetail = item._2
      val goodsIds = orderDetail.goodsIds.asScala
      val buffer = new ArrayBuffer[(String, Int)]()

      for (goodsId <- goodsIds.keys) {
        for (productId <- goodsIds.get(goodsId)) {
          productId match {
            case x:List[Int] => {
              for (t <- x.asScala)
                buffer.append((goodsId + "_" + t, 1))
            }
          }
        }
      }
      buffer
    }).groupByKey()
      .mapValues(_.size)
      .foreach(item => {
        JedisUtil.zSetIncBy(GOODS_ORDER, item._1, item._2)
      })
  }

}
