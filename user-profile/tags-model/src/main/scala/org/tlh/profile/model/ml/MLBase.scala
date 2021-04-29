package org.tlh.profile.model.ml

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.ml.Model
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.sql.DataFrame
import org.tlh.profile.conf.ModelConf
import org.tlh.profile.enums.MLType.MLType
import org.tlh.profile.enums.MLType._

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-28
  */
trait MLBase {

  protected[this] def loadModel(df: DataFrame, mlType: MLType): Model[_] = {
    //0. 获取模型路径
    val simpleName = this.getClass.getSimpleName
    val modelPath = ModelConf.MODEL_DIR + simpleName.substring(0, simpleName.length - 1)
    //1. 检查模型是否存在
    val hadoopConfiguration = df.sparkSession.sparkContext.hadoopConfiguration
    val fs = FileSystem.get(hadoopConfiguration)
    //2. 判断模型是否存在
    if (fs.exists(new Path(modelPath))) {
      mlType match {
        case RFM => KMeansModel.load(modelPath)
      }
    } else {
      mlType match {
        case RFM => {
          //3. 训练并获取最佳模型
          val model = trainBestKMeansModel(df, 5)
          //4. 保存模型
          model.save(modelPath)

          model
        }
      }
    }
  }

  /**
    * 训练KMeans模型
    *
    * @param df 数据集
    * @param k  分类数
    * @return
    */
  protected[this] def trainBestKMeansModel(df: DataFrame, k: Int): KMeansModel = {
    //1. 构建模型
    val models = Array(5, 10, 15, 20).map(item => {
      val kMeans = new KMeans()
        .setK(k)
        .setMaxIter(item)
        .setSeed(31) // 随机因子
        .setFeaturesCol("features") // 输入特征列名
        .setDistanceMeasure("cosine") // 距离测量测量

      //2. 训练模型
      val kMeansModel = kMeans.fit(df)

      //3. 测试模型效果
      val predictions = kMeansModel.transform(df)

      //4. 轮廓系数
      val evaluator = new ClusteringEvaluator()
        .setFeaturesCol("features")
        .setDistanceMeasure("cosine") //设置距离计算方式

      val silhouette = evaluator.evaluate(predictions)

      // 用户划分情况   通过用户的分布情况 根据肘部法制来确定  k 值
      //      import org.apache.spark.sql.functions._
      //
      //      val user_distribution = predictions.groupBy("prediction")
      //        .agg(count("user_id"))
      //        .collect().mkString

      (silhouette, kMeansModel)
    })

    val (_, model) = models.maxBy(_._1)

    model
  }

}
