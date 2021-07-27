package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.OriginalData
import org.tlh.dw.entity._
import org.tlh.spark.util.{DateUtil, JedisUtil}

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object AddCartProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    //1. 计算加购
    rdd.map(item => {
      //2|userId|goodsId|productId|number
      val attrs = item.ext.split("\\|")
      val goodsId = attrs(2).toInt
      val productId = attrs(3).toInt
      (goodsId + "_" + productId, 1)
    })
      .groupByKey()
      .mapValues(_.size)
      .foreach(item => {
        JedisUtil.zSetIncBy(DateUtil.todayFormat() + GOODS_CART, item._1, item._2)
      })
  }

}
