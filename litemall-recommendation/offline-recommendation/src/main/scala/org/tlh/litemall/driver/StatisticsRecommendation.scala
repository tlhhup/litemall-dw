package org.tlh.litemall.driver

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * 基于统计的推荐
  * 1. 累计 销量 评分
  * 2. 最近 销量 评分
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-16
  */
object StatisticsRecommendation extends BaseDriver {

  val sale_topN = 20
  val sale="sale"
  val recently_sale="recently_sale"
  val comment_topN = 20
  val comment="comment"
  val recently_comment="recently_comment"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StatisticsRecommendation").setMaster("local[*]")
    //设置使用域名通信
    conf.set("dfs.client.use.datanode.hostname", "true")
    // 设置元数据版本
    //conf.set("spark.sql.hive.metastore.version","3.1.2")
    val spark = SparkSession.builder()
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    //1. 累计销量 topN
    var sql =
      s"""
         |select
         |	date_sub(current_date,1) as stat_date,
         |	sku_id,
         |	spu_id,
         |	order_count
         |from litemall.dwt_sku_topic
         |order by order_count desc
         |limit ${sale_topN}
      """.stripMargin
    var df = spark.sql(sql)
    saveToMongoDb(df,sale)

    //2. 累计评分 topN
    sql=
      s"""
        |select
        |	date_sub(current_date,1) as stat_date,
        |	value_id as spu_id,
        |	avg(star) as avg_star
        |from litemall.dwd_fact_comment_info
        |where type=0
        |group by value_id
        |order by avg_star desc
        |limit ${comment_topN}
      """.stripMargin
    df=spark.sql(sql)
    saveToMongoDb(df,comment)

    //3. 最近评分 topN
    sql=
      s"""
        |select
        |	date_sub(current_date,1) as stat_date,
        |	value_id as spu_id,
        |	avg(star) as avg_star
        |from litemall.dwd_fact_comment_info
        |where dt=date_sub(current_date,1) and type=0
        |group by value_id
        |order by avg_star desc
        |limit ${comment_topN}
      """.stripMargin
    df=spark.sql(sql)
    saveToMongoDb(df,recently_comment)

    //4. 最近销量
    sql=
      s"""
        |select
        |	date_sub(current_date,1) as stat_date,
        |	sku_id,
        |	order_count
        |from litemall.dws_goods_action_daycount
        |where dt=date_sub(current_date,1)
        |order by order_count desc
        |limit ${sale_topN}
      """.stripMargin
    df=spark.sql(sql)
    saveToMongoDb(df,recently_sale)


    spark.stop()
  }

}
