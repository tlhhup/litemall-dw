package org.tlh.profile.model.statistics

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * per customer transaction 客单价 每一个顾客平均购买商品的金额，也即是平均交易金额
  * <br>
  * 客单价=销售额÷成交顾客数
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-22
  */
object PCTModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "PCTModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "客单价"

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

    //1.计算销售额  成交顾客数
    val orderAmount = sum(when('order_status === 401, 'order_price).otherwise(0)) as 'orderAmount
    val competeOrder = sum(when('order_status === 401, 1).otherwise(0)) as 'competeOrder
    //2.计算客单价
    val pct = orderAmount.divide(competeOrder) as 'pct
    val temp = source.select(pct)

    //3.构建条件
    var condition: Column = null
    rules.foreach(item => {
      val Array(start, end) = item.rule.split("-")
      condition = if (condition == null) {
        when('pct.between(start, end), item.id.toString)
      } else {
        condition.when('pct.between(start, end), item.id.toString)
      }
    })

    //4.执行计算
    val tmp = temp.select(condition as 'pct)

    val result = source.crossJoin(tmp).select('user_id as 'id, 'pct)

    result
  }
}
