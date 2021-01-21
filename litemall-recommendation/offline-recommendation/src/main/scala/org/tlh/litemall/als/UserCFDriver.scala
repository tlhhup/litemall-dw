package org.tlh.litemall.als

import org.apache.spark.SparkConf
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.{Row, SparkSession}
import org.tlh.litemall.{ProductRecs, Recommendation, UserRecs}
import org.tlh.litemall.driver.BaseDriver
import org.jblas.DoubleMatrix

import scala.collection.mutable


/**
  * 基于用户评分数据推荐:协同过滤<br>
  * 1. 通过每天用户评分数据<br>
  * 2. 训练模型，得到用户推荐列表，取 topN<br>
  * 3. 得到商品的特征矩阵，计算商品的相识度<br>
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-17
  */
object UserCFDriver extends BaseDriver {

  val top_n = 10
  val SIM_THRESHOLD = 0.4
  val user_spu = "user_recommends"
  val PRODUCT_SIM = "product_sims"

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

    //1. 读取前一天的评分数据
    val sql =
      """
        |select
        |	user_id,
        |	value_id as spu_id,
        |	star
        |from litemall.dwd_fact_comment_info
        |where dt=date_sub(current_date,1) and type=0
      """.stripMargin
    val training = spark.sql(sql)

    //2. 构建模型
    val rank = 10
    val als = new ALS()
      .setMaxIter(5) // 设置迭代次数
      .setRegParam(0.01) // 设置正则化系数
      .setRank(rank) //设置特征值个数
      .setUserCol("user_id")
      .setItemCol("spu_id")
      .setRatingCol("star")
    //2.1训练模型
    val model = als.fit(training)

    // Generate top n recommendations for each user
    val userRecs = model.recommendForAllUsers(top_n)
    val temp = userRecs.as[(Int, Array[(Int, Float)])]
      .map {
        case (userId, recs) => UserRecs(userId, recs.toList.sortWith(_._2 > _._2).map(item => Recommendation(item._1, item._2)))
      }.toDF()
    saveToMongoDb(temp, user_spu)

    //3. 获取商品特征矩阵
    val productFeatures = model.itemFactors.as[(Int, Array[Double])]
      .rdd
      .map(item => (item._1, new DoubleMatrix(item._2)))

    //4. 计算余弦相似度
    val product_recs = productFeatures.cartesian(productFeatures)
      .filter(item => item._1._1 != item._2._1)
      .map {
        //计算余弦相似度
        case (p1, p2) => {
          val sim = conSinSim(p1._2, p2._2)
          (p1._1, (p2._1, sim))
        }
      }
      .filter(_._2._2 > SIM_THRESHOLD)
      .groupByKey()
      .map {
        case (productId, recs) => {
          ProductRecs(productId, recs.toList.sortWith(_._2 > _._2).map(item => Recommendation(item._1, item._2)))
        }
      }
      .toDF()
    saveToMongoDb(product_recs, PRODUCT_SIM)

    spark.close()
  }

  def conSinSim(p1: DoubleMatrix, p2: DoubleMatrix): Double = {
    p1.dot(p2) / (p1.norm2() * p2.norm2())
  }

}
