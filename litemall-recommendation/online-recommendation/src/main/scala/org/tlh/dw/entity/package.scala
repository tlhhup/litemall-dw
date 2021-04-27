package org.tlh.dw

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-27
  */
package object entity {

}


// 定义标准推荐对象
case class Recommendation(productId: Int, score: Double)

case class ProductRecs(productId: Int, recs: Seq[Recommendation])

case class UserGoods(eventType: Int, userId: Int, productIds: Seq[Int])