package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.{GOODS_COLLECT, OriginalData}
import org.tlh.spark.util.JedisUtil

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object CollectProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    //1. 计算收藏
    rdd.map(item => {
      //8|userId|type|valueId1/0
      val attrs = item.ext.split("\\|")
      val valueType = attrs(2).toInt
      val goodsId = attrs(3).toInt
      val collect = attrs(4).toInt
      (valueType + "_" + collect, (goodsId, 1))
    })
      .filter(_._1 == "0_1")
      .map(_._2)
      .groupByKey()
      .mapValues(_.size)
      .foreach(item => {
        JedisUtil.zSetIncBy(GOODS_COLLECT, item._1.toString, item._2)
      })
  }

}
