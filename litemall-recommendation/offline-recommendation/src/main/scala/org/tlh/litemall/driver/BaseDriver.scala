package org.tlh.litemall.driver

import org.apache.spark.sql.DataFrame
import org.tlh.litemall.MongoConf

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-17
  */
class BaseDriver {


  implicit val mongoConf = MongoConf("mongodb://storage:27017/litemall", "litemall")

  /**
    * 保存数据
    *
    * @param df
    * @param collectionName
    * @param mongoConf
    */
  def saveToMongoDb(df: DataFrame, collectionName: String)(implicit mongoConf: MongoConf): Unit = {
    df.write
      .option("uri", mongoConf.uri)
      .option("collection", collectionName)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
  }

}
