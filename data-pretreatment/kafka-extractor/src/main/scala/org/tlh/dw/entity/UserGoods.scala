package org.tlh.dw.entity

import scala.collection.mutable.Set
import scala.util.parsing.json.JSON


/**
  * 用户行为数据
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-22
  */
class UserGoods(val eventType: Int,
                val valueType: Int = 0, // 针对评论和收藏 1 主题 0 商品
                val collected: Boolean = true, // 针对收藏
                val userId: Int,
                val goodsId: Set[Int]) extends Serializable {

  override def toString = {
    val goodsStr = goodsId.mkString(",")
    s"$eventType|$userId|$goodsStr"
  }
}

object UserGoods {

  def apply(message: String): UserGoods = {
    var attrs = message.split("#CS")
    // 获取数据
    val data = attrs(1)
    attrs = data.split("\\|")
    // 事件类型和用户ID
    val eventType = attrs(0).toInt
    val userId = attrs(1).toInt
    // 提取商品列表
    var valueType = 0
    var collected = true
    var goodsIds: Set[Int] = Set()
    eventType match {
      case 2 => {
        goodsIds += attrs(2).toInt
      }
      case 3 => {
        val jsonStr = attrs(4)
        val jsonMap = JSON.parseFull(jsonStr).get.asInstanceOf[Map[String, Object]]
        goodsIds ++= jsonMap.get("goodsIds").get.asInstanceOf[Map[String, Object]].keySet.map(_.toInt)
      }
      case 7 => {
        valueType = attrs(3).toInt
        goodsIds += attrs(4).toInt
      }
      case 8 => {
        valueType = attrs(2).toInt
        goodsIds += attrs(3).toInt
        collected = (attrs(4).toInt == 1)
      }
      case _ =>
    }
    new UserGoods(eventType, valueType, collected, userId, goodsIds)
  }

  def main(args: Array[String]): Unit = {
    val message = "11#CS3|630|4947|894.00|{\"couponUserId\":4744,\"couponId\":1,\"grouponId\":0,\"goodsIds\":{\"1030005\":[44]}}"
    val result = UserGoods(message)
    println(result)
  }

}
