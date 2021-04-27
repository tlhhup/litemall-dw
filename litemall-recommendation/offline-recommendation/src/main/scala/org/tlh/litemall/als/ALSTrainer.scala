package org.tlh.litemall.als

import org.apache.spark.SparkConf
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.param.{ParamMap, Params}
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.{Row, SparkSession}
import org.tlh.litemall.Rating

/**
  * 对als模型进行训练，并保存模型，供给离线推荐使用
  * userCF 基于用户的协同过滤
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-18
  */
object ALSTrainer {

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

    //1. 读取之前的评分数据,将数据按日期升序排序,保证切分的数据具有时间维度，更好的进行预测判断
    val sql =
      """
        |select
        |	user_id,
        |	value_id as spu_id,
        |	star
        |from litemall.dwd_fact_comment_info
        |where dt=date_sub(current_date,1) and type=0
        |order by add_time
      """.stripMargin
    val commentDf = spark.sql(sql)
      .map(row => Rating(row.getInt(0), row.getInt(1), row.getShort(2).toFloat))
      .toDF("userId", "itemId", "rating")

    val Array(training, test) = commentDf.randomSplit(Array(0.8, 0.2))

    //2. 模型选择，超参数优化
    //2.1 构建模型
    val als = new ALS()
      .setUserCol("userId")
      .setItemCol("itemId")
      .setRatingCol("rating")
      .setColdStartStrategy("drop")

    //2.2 参数
    val paramGrid = new ParamGridBuilder()
      .addGrid(als.rank, Array(5, 10, 20))
      .addGrid(als.regParam, Array(1, 0.1, 0.01))
      .addGrid(als.maxIter, Array(5, 10, 15))
      .build()

    //3. 交叉检验
    val evaluator = new RegressionEvaluator()
      .setMetricName("rmse")
      .setLabelCol("rating")
      .setPredictionCol("prediction")

    val cv = new CrossValidator()
      .setEstimator(als)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(2) // 设置交叉检验的数据集分割数
      .setParallelism(2) // 并行

    //3.1 训练模型
    val cvModel = cv.fit(training)

    //3.2 使用最好的模型，测试模型
    cvModel.transform(test)
      .select("prediction")
      .as[Float]
      .collect()
      .foreach { prediction => println(s"(prediction=$prediction") }

    // 模型的参数
    val adjustParams = Array(als.rank, als.maxIter, als.regParam)
    // 鉴别器参数集合
    val avgMetrics = cvModel.avgMetrics
    val paramMaps: Array[ParamMap] = cvModel.getEstimatorParamMaps
    for (i <- 0 until paramGrid.length) {
      println(s"@@@ ModelGrid[$i]:")
      println("@@@ Params:")
      for (param <- adjustParams) {
        println(s"@@@  ${param.name} : ${paramMaps(i).apply(param)}")
      }
      println(s"@@@ [${i}]'s metric=${avgMetrics(i)}")
    }

    // 保存模型
    val bestModel:ALS = (ALS) (cvModel.bestModel)
    bestModel.save("model/user-cf")

    spark.close()
  }


}
