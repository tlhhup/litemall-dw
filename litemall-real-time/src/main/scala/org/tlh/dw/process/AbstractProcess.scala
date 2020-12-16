package org.tlh.dw.process

import org.apache.spark.rdd.RDD
import org.tlh.dw.entity.OriginalData

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
trait AbstractProcess {

  def process(rdd: RDD[OriginalData]): Unit

}
