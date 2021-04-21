package org.tlh.spark.sql.hbase.filter

import java.util.{Calendar, Date}

import org.apache.commons.lang3.time.DateFormatUtils


/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-21
  */
class DateFilter(val filed: String,
                 val unit: String,
                 val duration: Int) {

  private[this] val DATE_FORMAT = "yyyy-MM-dd"

  def start(): String = {
    val calendar = Calendar.getInstance()
    unit match {
      case "day" => calendar.add(Calendar.DAY_OF_YEAR, -duration)
      case "month" => calendar.add(Calendar.MONTH, -duration)
      case "year" => calendar.add(Calendar.YEAR, -duration)
    }

    DateFormatUtils.format(calendar, DATE_FORMAT)
  }

  def end(): String = {
    DateFormatUtils.format(new Date(), DATE_FORMAT)
  }

}

object DateFilter {

  def apply(condition: String): DateFilter = {
    val Array(filed, unit, duration) = condition.split("#")
    new DateFilter(filed, unit, duration.toInt)
  }

}
