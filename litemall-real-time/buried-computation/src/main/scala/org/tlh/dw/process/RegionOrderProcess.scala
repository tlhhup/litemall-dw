package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.{OriginalData, RegionOrder}
import org.tlh.spark.util.{DateUtil, JedisUtil}
import org.tlh.dw.entity.REGION_ORDER_COUNT

/**
  * 区域(城市级别)订单统计
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-15
  */
object RegionOrderProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    rdd.map(item => RegionOrder(item.ext))
      .map(item => (item.city, item))
      .groupByKey()
      .mapValues(_.size)
      .foreach {
        case (city, size) => {
          JedisUtil.zSetIncBy(DateUtil.todayFormat() + REGION_ORDER_COUNT, city + "", size)
        }
      }
  }


}
