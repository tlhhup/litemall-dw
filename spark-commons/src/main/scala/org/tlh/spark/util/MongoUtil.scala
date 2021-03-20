package org.tlh.spark.util

import com.typesafe.config.ConfigFactory
import org.mongodb.scala._

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-27
  */
object MongoUtil {

  // 加载配置文件
  val config = ConfigFactory.load("mongo")

  val client: MongoClient = {
    // 1.获取url
    val url = config.getString("mongo.url")
    MongoClient(url)
  }

  def getMongoClient(): MongoClient = client


  def getDBOrDefault(db: String = null): MongoDatabase = {
    val actualDb = Option(db) match {
      case Some(value) => value
      case None => config.getString("mongo.db")
    }

    client.getDatabase(actualDb)
  }
}
