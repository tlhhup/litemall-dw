package org.tlh.profile.model.ml

import org.apache.spark.sql.{Column, DataFrame}
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 优惠明感度模型
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-29
  */
object PSMModel extends SingleMetaModel with MLBase {

  private[this] val RULE_REG = "^([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[1-9]\\d*)?([~|>|<|=]*)([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[1-9]\\d*|0)$".r

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "PSMModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "促销敏感度"

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

    //1. 处理优惠金额 = 优惠券减免+用户积分减免+团购优惠价减免
    val temp = source.select(
      'user_id,
      'order_price,
      'actual_price,
      'coupon_price.plus('integral_price).plus('groupon_price) as 'discount_price)

    //2. 计算优惠订单数、订单总数、平均优惠金额、平均每单应收、优惠总金额、应收总金额
    val tdon = sum(when('discount_price.gt(0), 1).otherwise(0)) as 'tdon
    val ton = count("*") as 'ton
    val ada = avg('discount_price) as 'ada
    val ara = avg('order_price) as 'ara
    val tda = sum('discount_price) as 'tda
    val tra = sum('order_price) as 'tra

    //3. 计算 优惠订单占比 平均优惠金额占比 优惠金额占比
    val tdonr = when(ton === 0, 0).otherwise(tdon.divide(ton)) as 'tdonr
    val adar = when(ara === 0, 0).otherwise(ada.divide(ara)) as 'adar
    val tdar = when(tra === 0, 0).otherwise(tda.divide(tra)) as 'tdar

    //4. 计算psm
    val psm = tdonr.plus(adar).plus(tdar) as 'psmScore
    val t = temp.groupBy('user_id)
      .agg(tdonr, adar, tdar, psm)
      .select('user_id as 'id, 'psmScore)

    //5. 构建条件
    var condition: Column = null
    rules.foreach(item => {
      //5.1 解析规则
      val rule = item.rule

      //5.2 创建或添加条件
      condition = if (condition == null) {
        when(buildWhen(rule), item.name)
      } else {
        condition.when(buildWhen(rule), item.name)
      }
    })

    //6. 计算打标签
    val result = t.select('id, condition as 'psm)
      .filter('psm isNotNull)

    result
  }

  // 解析规则
  private[this] def buildWhen(rule: String): Column = {
    import org.apache.spark.sql.functions._

    val RULE_REG(start, symbol, end) = rule

    if (start == null) {
      symbol match {
        case ">" => col("psmScore").gt(end)
        case ">=" => col("psmScore").geq(end)
        case "<" => col("psmScore").lt(end)
        case "<=" => col("psmScore").leq(end)
        case _ => col("psmScore") === end
      }
    } else {
      col("psmScore").between(start, end)
    }
  }

}
