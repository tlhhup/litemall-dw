package org.tlh.profile.model.ml

import org.apache.spark.ml.clustering.KMeansModel
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.{Column, DataFrame}
import org.apache.spark.sql.types.{DecimalType, LongType, StringType}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.enums.MLType
import org.tlh.profile.model.SingleMetaModel

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-28
  */
object RFMModel extends SingleMetaModel with MLBase {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "RFMModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "用户价值"

  /**
    * 业务逻辑处理
    *
    * @param rules
    * @param commonMeta
    * @param source
    * @return
    */
  override def processDetail(rules: Array[entity.Tag], commonMeta: CommonMeta, source: DataFrame): DataFrame = {
    import source.sparkSession.implicits._
    import org.apache.spark.sql.functions._

    //1. 计算用户的RFM值
    val rColumn = datediff(current_date(), max('end_time)) as 'r_column // 计算最近一次消费时间距离现在的天数
    val fColumn = count('order_sn) as 'f_column // 计算订单数
    val mColumn = sum('actual_price.cast(DecimalType.USER_DEFAULT)) as 'm_column // 计算消费金额

    val t = source.groupBy('user_id)
      .agg(rColumn, fColumn, mColumn)

    //2. 对RFM值进行打分
    //R: 1-3天=5分，4-6天=4分，7-9天=3分，10-15天=2分，大于16天=1分
    //F: ≥200=5分，150-199=4分，100-149=3分，50-99=2分，1-49=1分
    //M: ≥20w=5分，10-19w=4分，5-9w=3分，1-4w=2分，<1w=1分
    val rScore = when('r_column.between(1, 3), 5)
      .when('r_column.between(4, 6), 4)
      .when('r_column.between(7, 9), 3)
      .when('r_column.between(10, 15), 2)
      .otherwise(1) as 'r_score

    val fScore = when('f_column.gt(200), 5)
      .when('f_column.between(150, 199), 4)
      .when('f_column.between(100, 149), 3)
      .when('f_column.between(50, 99), 2)
      .otherwise(1) as 'f_score

    val w = 1000
    val mScore = when('m_column.gt(20 * w), 5)
      .when('m_column.between(10 * w, 19 * w), 4)
      .when('m_column.between(5 * w, 9 * w), 3)
      .when('m_column.between(1 * w, 4 * w), 2)
      .otherwise(1) as 'm_score

    val rfm_score = t.select('user_id cast (LongType), rScore, fScore, mScore)

    //3. 使用KMeans算法进行聚类运算
    //3.1 将数据转换为向量,将制定的列转换为特征列
    val featureDf = new VectorAssembler()
      .setInputCols(Array("r_score", "f_score", "m_score")) //指定的列
      .setOutputCol("features") //输出的特征列
      .transform(rfm_score)

    //3.2 获取最优的模型
    val kMeansModel = loadModel(featureDf, MLType.RFM).asInstanceOf[KMeansModel]

    //3.3 预测模型
    val predictDf = kMeansModel.transform(featureDf)

    //3.4 获取质心
    val clusterIndexers = kMeansModel
      .clusterCenters
      .zipWithIndex
    //3.4.1 计算质心的RFM和
    val ci = clusterIndexers.map {
      case (v, i) => {
        (v.toArray.sum, i)
      }
    }
      .sortWith(_._1 > _._1)
      .zipWithIndex
    //3.4.2 将质心和 rule 进行关联  质心和最大-->最有价值
    val ruleMap = rules.map(item => {
      item.rule -> (item.id, item.name)
    }).toMap

    val final_rules = ci.map(item => {
      (item._1._2, ruleMap(item._2.toString))
    })

    //4. 构建条件
    var condition: Column = null
    final_rules.foreach(item => {
      condition = if (condition == null) {
        when('prediction === item._1, item._2._2)
      } else {
        condition.when('prediction === item._1, item._2._2)
      }
    })

    //5. 标签计算
    val result = predictDf.select('user_id as 'id cast (StringType), condition as 'rfm)

    result
  }

}
