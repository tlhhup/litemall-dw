package org.tlh.spark.util

import java.text.SimpleDateFormat
import java.util.Date

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-05
  */
object DateUtil {

  def todayFormat(): String = {
    val format = new SimpleDateFormat("yyyy-MM-dd")
    format.format(new Date())
  }

}
