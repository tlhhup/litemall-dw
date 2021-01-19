package org.tlh.litemall.itemcf

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.tlh.litemall.driver.BaseDriver
import org.tlh.litemall.{ProductRecs, Recommendation}

/**
  * 通过用户的购买行为，来计算商品的相似度
  * 基于物品的协同过滤
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-19
  */
object ItemCFDriver extends BaseDriver {

  val MAX_RECOMMENDATION = 10
  val SIM_THRESHOLD = 0.4
  val ITEM_CF_PRODUCT_SIM = "item_cf_product_sim"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("CommentDriver").setMaster("local[*]")
    //设置使用域名通信
    conf.set("dfs.client.use.datanode.hostname", "true")
    // 设置元数据版本
    //conf.set("spark.sql.hive.metastore.version","3.1.2")
    val spark = SparkSession.builder()
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    //1. 加载前一天的订单明细表(user_id,spu_id)
    val sql =
      """
        |select
        |	user_id,
        |	goods_id
        |from litemall.dwd_fact_order_goods_info
        |where dt=date_sub(current_date,1)
        |group by user_id,goods_id
      """.stripMargin
    val upDf = spark.sql(sql).toDF("user_id", "spu_id")

    //2. 计算每个商品的购买用户数(spu_id,user_count)
    val spCountDf = upDf.groupBy("spu_id").agg("user_id" -> "count")

    //3. join得到(user_id,spu_id,user_count)
    val usWithCountDf = upDf.join(spCountDf, "spu_id")

    //4. self join 得到(user_id,spu_id_1,user_count_1,spu_id_2,user_count_2)
    val uspu_spuDf = usWithCountDf.join(usWithCountDf, "user_id")
      .toDF("user_id", "spu_id_1", "user_count_1", "spu_id_2", "user_count_2")

    uspu_spuDf.createOrReplaceTempView("user_spu_temp")
    //5. group spu得到(spu_id_1,user_count_1,spu_id_2,user_count_2,count_join)
    // 使用窗口函数，来保留每个商品的用户
    val cooccurrenceDF = spark.sql(
      """
        |select
        | spu_id_1,
        | first(user_count_1) as count1,
        | spu_id_2,
        | first(user_count_2) as count2,
        | count(1) as ccount
        |from user_spu_temp
        |group by spu_id_1,spu_id_2
      """.stripMargin)

    //6. 计算相识度
    val simDf = cooccurrenceDF.map {
      row => {
        val sim = getCooccurrenceSim(row.getAs[Long]("ccount"), row.getAs[Long]("count1"), row.getAs[Long]("count2"))
        (row.getAs[Int]("spu_id_1"), (row.getAs[Int]("spu_id_2"), sim))
      }
    }.rdd
      .filter(_._2._2 > SIM_THRESHOLD)
      .groupByKey()
      .map {
        case (spu_id, recs) => {
          ProductRecs(spu_id,
            recs.toList
              .filter(_._1 != spu_id)
              .sortWith(_._2 > _._2)
              .take(MAX_RECOMMENDATION)
              .map(item => Recommendation(item._1, item._2))
          )
        }
      }
      .filter(_.recs.size > 0)
      .toDF()

    //7. 保存到mongo
    saveToMongoDb(simDf, ITEM_CF_PRODUCT_SIM)

    spark.close()
  }

  def getCooccurrenceSim(coo: Long, count1: Long, count2: Long): Double = {
    coo / math.sqrt(count1 * count2)
  }

}
