package org.tlh.dw.entity

import java.util

import org.tlh.spark.util.JsonScalaUtil
import java.util.{List, Map}

import collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
class OrderDetail(val orderId: Int,
                  val actualPrice: Double,
                  val grouponId: Int,
                  val couponId: Int,
                  val couponUserId: Int,
                  val goodsIds: Map[String, List[Int]]
                 ) extends Serializable {


  override def toString = s"OrderDetail(orderId=$orderId, actualPrice=$actualPrice, grouponId=$grouponId, couponId=$couponId, couponUserId=$couponUserId, goodsIds=$goodsIds)"
}

object OrderDetail {

  def apply(message: String): OrderDetail = {
    // 3|userId|orderId|actualPrice|{"grouponId":0,"goodsIds":{"1097004":[120]},"couponId":2,"couponUserId":32}
    val attrs = message.split("\\|")
    val orderId = attrs(2).toInt
    val actualPrice = attrs(3).toDouble
    val temp = JsonScalaUtil.toBean(classOf[OrderDetail], attrs(4))
    new OrderDetail(orderId, actualPrice, temp.grouponId, temp.couponId, temp.couponUserId, temp.goodsIds)
  }

  def main(args: Array[String]): Unit = {
    val message = "3|630|4947|894.00|{\"couponUserId\":4744,\"couponId\":1,\"grouponId\":0,\"goodsIds\":{\"1030005\":[44]}}";
    val orderDetail = OrderDetail(message)
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
    println(buffer)
  }

}
