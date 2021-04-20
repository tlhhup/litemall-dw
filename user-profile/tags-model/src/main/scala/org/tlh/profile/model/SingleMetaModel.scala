package org.tlh.profile.model

import org.apache.spark.sql.DataFrame
import org.tlh.profile.entity
import org.tlh.profile.entity.{CommonMeta, Tag}

/**
  * 单一数据源
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-09
  */
trait SingleMetaModel extends BaseModel {

  override def processDetail(rules: Array[entity.Tag], sources: Array[(CommonMeta, DataFrame)]): DataFrame = {
    //1. 解析第一个数据源
    val (commonMeta, source) = sources(0)
    this.processDetail(rules, commonMeta, source)
  }

  /**
    * 业务逻辑处理
    *
    * @param rules
    * @param commonMeta
    * @param source
    * @return
    */
  def processDetail(rules: Array[Tag], commonMeta: CommonMeta, source: DataFrame): DataFrame

}
