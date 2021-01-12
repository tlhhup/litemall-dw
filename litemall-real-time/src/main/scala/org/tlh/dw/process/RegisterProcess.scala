package org.tlh.dw.process
import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.OriginalData
import org.tlh.spark.util.{DateUtil, JedisUtil}
import org.tlh.dw.entity.USER_REGISTER_COUNT

/**
  * 用户注册
  * @author 离歌笑
  * @desc
  * @date 2021-01-12
  */
object RegisterProcess extends AbstractProcess {

  override def process(rdd: RDD[OriginalData]): Unit = {
    JedisUtil.inc(DateUtil.todayFormat() + USER_REGISTER_COUNT, rdd.count())
  }

}
