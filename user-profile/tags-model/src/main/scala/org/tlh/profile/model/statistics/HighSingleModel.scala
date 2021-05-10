package org.tlh.profile.model.statistics

import org.apache.spark.sql.types.DecimalType
import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 单比最高
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-22
  */
object HighSingleModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "HighSingleModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "单笔最高"

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

    //1. 计算单比最高
    val singleMax = max('order_price) as 'orderMax
    val tmp = source
      .select('user_id, 'order_price.cast(DecimalType.USER_DEFAULT)) // 转换数据类型(hBase中为字符串)
      .groupBy('user_id)
      .agg(singleMax)

    //2. 构建匹配条件
    var condition: Column = null
    rules.foreach(item => {
      val Array(start, end) = item.rule.split("-")
      condition = if (condition == null) {
        when('orderMax.between(start, end), item.id.toString)
      } else {
        condition.when('orderMax.between(start, end), item.id.toString)
      }
    })

    //3. 执行计算
    val result = tmp.select('user_id as 'id, condition as 'singleOrderMax)
      .filter('singleOrderMax isNotNull)

    result
  }
}
