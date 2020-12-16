package org.tlh.dw.entity

import java.util.Date

import org.apache.commons.lang3.time.DateUtils


/**
  * 基础数据
  *
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
class OriginalData(val time: Date,
                   val eventType: Int,
                   val userId: Int,
                   val ext: String) extends Serializable {

  override def toString = s"OriginalData(time=$time, eventType=$eventType, userId=$userId, ext=$ext)"
}

object OriginalData {

  def apply(message: String): OriginalData = {
    var attrs = message.split("#CS")
    val time = DateUtils.parseDate(attrs(0), "yyyy-MM-dd HH:mm:ss.SSS");
    val data = attrs(1)
    attrs = data.split("\\|")
    val eventType = attrs(0).toInt
    val userId = attrs(1).toInt
    new OriginalData(time, eventType, userId, data)
  }

}
