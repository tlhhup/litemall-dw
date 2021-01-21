package org.tlh.litemall

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-17
  */
package object entity {

}

case class Rating(userId: Int, itemId: Int, rating: Float)

case class MongoConf(uri: String, db: String)

// 定义标准推荐对象
case class Recommendation(productId: Int, score: Double)

// 定义用户的推荐列表
case class UserRecs(userId: Int, recs: Seq[Recommendation])

case class ProductRecs(productId: Int, recs: Seq[Recommendation])
