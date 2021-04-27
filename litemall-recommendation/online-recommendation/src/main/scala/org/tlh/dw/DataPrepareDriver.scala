package org.tlh.dw

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * 用户交互过的数据计算，用于在线推荐中候选商品过滤
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-28
  */
object DataPrepareDriver {

  val mongoConf = Map(
    "url" -> "mongodb://storage:27017/litemall",
    "db" -> "litemall")

  val USER_COMMENT_HISTORY = "user_comment_history"
  val USER_ORDER_HISTORY = "user_order_history"
  val USER_COLLECT_HISTORY = "user_collect_history"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StatisticsRecommendation").setMaster("local[*]")
    //设置使用域名通信
    conf.set("dfs.client.use.datanode.hostname", "true")
    val spark = SparkSession.builder()
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    //1. 统计评论
    var sql =
      s"""
         |select
         |	user_id,
         |	collect_set(value_id) as goods_id
         |from litemall.dwd_fact_comment_info
         |where type=0
         |group by user_id
        """.stripMargin
    var df = spark.sql(sql)
    saveToMongoDb(df, USER_COMMENT_HISTORY)

    // 统计收藏的商品
    sql =
      s"""
         |select
         |	user_id,
         |	collect_set(value_id) as goods_id
         |from litemall.dwd_fact_collect_info
         |where type=0 and is_cancel=false
         |group by user_id
      """.stripMargin
    df = spark.sql(sql)
    saveToMongoDb(df, USER_COLLECT_HISTORY)

    // 统计下单的商品
    sql =
      s"""
         |select
         |	user_id,
         |	collect_set(goods_id) as goods_id
         |from litemall.dwd_fact_order_goods_info
         |group by user_id
      """.stripMargin
    df = spark.sql(sql)
    saveToMongoDb(df, USER_ORDER_HISTORY)

    spark.stop()
  }

  def saveToMongoDb(df: DataFrame, collect: String): Unit = {
    df.write
      .option("uri", mongoConf("url"))
      .option("collection", collect)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
  }

}
