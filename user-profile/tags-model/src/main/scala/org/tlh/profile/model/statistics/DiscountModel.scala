package org.tlh.profile.model.statistics

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-23
  */
object DiscountModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "DiscountModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "省钱小能手"

  /**
    * 业务逻辑处理
    *
    * @param rules
    * @param commonMeta
    * @param source
    * @return
    */
  override def processDetail(rules: Array[entity.Tag], commonMeta: CommonMeta, source: DataFrame): DataFrame = {
    import source.sparkSession.implicits._
    import org.apache.spark.sql.functions._

    //1. 计算优惠力度
    val temp = source.select('user_id,
      'coupon_price.plus('groupon_price).divide('goods_price) as 'discountRate)

    //2. 获取最大的打折力度
    val tmp = temp.groupBy('user_id)
      .agg(max('discountRate) as 'discountRate)

    //3. 构建条件
    var condition: Column = null
    rules.foreach(item => {
      val Array(start, end) = item.rule.split("-")
      condition = if (condition == null) {
        when('discountRate.between(start, end), item.id.toString)
      } else {
        condition.when('discountRate.between(start, end), item.id.toString)
      }
    })

    //4. 执行计算
    val result = tmp.select('user_id as 'id, condition as 'discountRate)
      .filter('discountRate isNotNull)

    result
  }
}
