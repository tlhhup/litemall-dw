package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.{GOODS_COMMENT_BAD, GOODS_COMMENT_GOOD, OriginalData}
import org.tlh.spark.util.{DateUtil, JedisUtil}

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object CommentProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    //1. 计算评论
    val goodsRdd = rdd.map(item => {
      //7|userId|orderId|type|valueId|star
      val attrs = item.ext.split("\\|")
      val valueType = attrs(3).toInt
      val goodsId = attrs(4).toInt
      val star = attrs(5).toInt
      (valueType, (goodsId, star))
    })
      .filter(_._1 == 0)
      .map(_._2)
    //2. 好评
    goodsRdd.filter(_._2 >= 4)
      .groupByKey()
      .mapValues(_.size)
      .foreach(item => {
        JedisUtil.zSetIncBy(DateUtil.todayFormat() + GOODS_COMMENT_GOOD, item._1.toString, item._2)
      })
    //3. 差评
    goodsRdd.filter(_._2 <= 2)
      .groupByKey()
      .mapValues(_.size)
      .foreach(item => {
        JedisUtil.zSetIncBy(DateUtil.todayFormat() + GOODS_COMMENT_BAD, item._1.toString, item._2)
      })
  }

}
