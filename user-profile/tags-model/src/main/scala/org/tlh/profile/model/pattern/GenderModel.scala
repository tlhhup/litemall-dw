package org.tlh.profile.model.pattern

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 性别模型
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-09
  */
object GenderModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "GenderModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "性别"

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

    //1. 构建condition  ==> case when
    var condition: Column = null
    rules.foreach(item => {
      condition = if (condition == null) {
        when('gender === item.rule, item.name)
      } else {
        condition.when('gender === item.rule, item.name)
      }
    })

    //2. 别名
    condition = condition.as("gender")

    //3. 执行计算查询
    val result = source.select('id, condition)

    result
  }
}
