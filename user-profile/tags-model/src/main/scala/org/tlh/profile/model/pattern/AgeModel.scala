package org.tlh.profile.model.pattern

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-20
  */
object AgeModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "AgeModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "年龄段"


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

    //1. 自定义函数 格式化birthday
    val formatBirthday = udf((birthday: String) => birthday.replace("-", ""))
    source.sparkSession.udf.register("formatBirthday", formatBirthday)

    //2. 构建condition
    var condition: Column = null
    rules.foreach(item => {
      //2.1 拆分数据
      val attrs = item.rule.split("-")
      val start = attrs(0)
      val end = attrs(1)
      //2.2 构建 case when
      condition = if (condition == null) {
        when(formatBirthday('birthday).between(start, end), item.id.toString)
      } else {
        condition.when(formatBirthday('birthday).between(start, end), item.id.toString)
      }
    })

    //3. 执行匹配计算
    val result = source.select('id, condition as 'age)

    result
  }
}
