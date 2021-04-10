package org.tlh.profile.model.pattern

import org.apache.spark.sql.DataFrame
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
    * @param model
    * @param rules
    * @param commonMeta
    * @param source
    * @return
    */
  override def processDetail(model: entity.Tag, rules: Array[entity.Tag], commonMeta: CommonMeta, source: DataFrame): DataFrame = {

    null
  }
}
