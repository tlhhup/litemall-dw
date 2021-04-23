package org.tlh.profile.model.statistics

import java.util.Calendar

import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.spark.sql.{Column, DataFrame}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types.StringType
import org.tlh.profile.entity
import org.tlh.profile.entity.CommonMeta
import org.tlh.profile.model.SingleMetaModel

/**
  * 查询半年内 使用最多的优惠卷
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-22
  */
object CouponModel extends SingleMetaModel {

  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * 应用名称
    *
    * @return
    */
  override def getAppName(): String = "CouponModel"

  /**
    * 获取标签名称
    *
    * @return
    */
  override def getTagName(): String = "有券必买"

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

    //1. 过滤数据
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, -6)
    val date = DateFormatUtils.format(calendar, "yyyy-MM-dd")

    val tmp = source.select('user_id, 'coupon_id)
      .where('status === 1 and ('add_time.geq(date)))

    //2. 统计用户使用最多的优惠卷
    val temp = tmp.groupBy('user_id, 'coupon_id)
      .agg(count("*") as 'total)

    //3. 使用窗口函数得到使用最多的优惠卷
    // rank:为相同组的数据计算排名
    // row_number:从1开始的递增的唯一序号值
    val t = temp.select(
      'user_id,
      'coupon_id,
      'total,
      row_number().over(Window.partitionBy('user_id).orderBy('total desc)) as 'row_number)
      .where('row_number === 1)

    //4. 构建条件
    var condition: Column = null
    rules.foreach(item => {
      condition = if (condition == null) {
        when('coupon_id === item.rule, item.name)
      } else {
        condition.when('coupon_id === item.rule, item.name)
      }
    })

    //5. 执行计算
    val result = t.select('user_id as 'id cast (StringType), condition as 'coupon)

    result
  }
}
