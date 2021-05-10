package org.tlh.profile.model.statistics

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 退货率
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-22
  */
object RefundModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "RefundModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "退货率"

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

    //1. 计算订单数  退单数
    val orderCount = count("*") as 'orderCount
    val refundCount = sum(when('order_status === 202, 1).otherwise(0)) as 'refundCount
    //2. 计算退货率
    val refund = refundCount.divide(orderCount).multiply(100) as 'refund

    val temp = source.groupBy('user_id)
      .agg(refund)

    //3. 构建条件
    var condition: Column = null
    rules.foreach(item => {
      val Array(start, end) = item.rule.split("-")
      condition = if (condition == null) {
        when('refund.between(start, end), item.id.toString)
      } else {
        condition.when('refund.between(start, end), item.id.toString)
      }
    })

    //4. 执行计算
    val result = temp.select('user_id as 'id,condition as 'refundRate)

    result
  }
}
