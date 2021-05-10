package org.tlh.profile.model.statistics

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 统计用户最近一次消费距现在的天数
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-21
  */
object ConsumptionModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "ConsumptionModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "消费周期"

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

    //1. 计算消费时间距离现在的天数
    val diff = datediff(current_date(), max('end_time)) as 'diff
    val temp = source
      .groupBy('user_id)
      .agg(diff)

    //2. 构建匹配
    var condition: Column = null
    rules.foreach(item => {
      val attrs = item.rule.split("-")
      val start = attrs(0)
      val end = attrs(1)

      condition = if (condition == null) {
        when('diff.between(start, end), item.id.toString)
      } else {
        condition.when('diff.between(start, end), item.id.toString)
      }
    })

    //3. 执行计算
    val result = temp.select('user_id as 'id, condition as 'consumption)
      .where('consumption isNotNull)

    result
  }
}
