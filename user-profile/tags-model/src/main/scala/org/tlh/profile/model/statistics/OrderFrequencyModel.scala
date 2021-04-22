package org.tlh.profile.model.statistics

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 购买频率
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-22
  */
object OrderFrequencyModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "OrderFrequencyModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "购买频率"

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

    //1.统计购买次数
    val tmp = source.groupBy('user_id)
      .agg(count("*") as 'orderFrequency)


    //2.构建条件
    var condition: Column = null
    rules.foreach(item => {
      val Array(start, end) = item.rule.split("-")
      condition = if (condition == null) {
        when('orderFrequency.between(start, end), item.name)
      } else {
        condition.when('orderFrequency.between(start, end), item.name)
      }
    })

    //3.执行计算
    val result = tmp.select('user_id as 'id, condition as 'orderFrequency)

    result
  }
}
