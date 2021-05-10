package org.tlh.profile.model.statistics

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 统计用户半年内的消费总金额
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-21
  */
object ConsumptionAbilityModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "ConsumptionAbilityModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "消费能力"

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

    //1. 过滤消费成功的订单
    //2. 聚合消费总金额
    val tmp = source.filter('order_status === 401)
      .groupBy('user_id)
      .agg(sum('actual_price) as 'amount)

    //3. 构建条件
    var condition: Column = null
    rules.foreach(item => {
      val attrs = item.rule.split("-")
      val start = attrs(0)
      val end = attrs(1)

      condition = if (condition == null) {
        when('amount.between(start, end), item.id.toString)
      } else {
        condition.when('amount.between(start, end), item.id.toString)
      }
    })

    //4. 执行计算
    val result = tmp.select('user_id as 'id, condition as 'consumptionAbility)

    result
  }
}
