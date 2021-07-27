package org.tlh.dw.entity

import org.tlh.spark.util.JsonScalaUtil
import java.util.{List, Map}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-15
  */
class RegionOrder(
                   val orderId: Int,
                   val actualPrice: Double,
                   val grouponId: Int,
                   val couponId: Int,
                   val couponUserId: Int,
                   val goodsIds: Map[String, List[Int]],
                   val province: Int,
                   val city: Int,
                   val country: Int
                 ) extends Serializable {

}

object RegionOrder {

  def apply(message: String): RegionOrder = {
    // 3|19|327|598.00|{"grouponId":0,"goodsIds":{"1115028":[161]}}|15|182|1711
    val attrs = message.split("\\|")
    val orderId = attrs(2).toInt
    val actualPrice = attrs(3).toDouble
    val temp = JsonScalaUtil.toBean(classOf[OrderDetail], attrs(4))
    val province = attrs(5).toInt
    val city = attrs(6).toInt
    val country = attrs(7).toInt
    new RegionOrder(orderId, actualPrice, temp.grouponId, temp.couponId, temp.couponUserId, temp.goodsIds,province,city,country)
  }

}
