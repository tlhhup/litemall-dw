package org.tlh.rt.dw.utils

import java.util.Date

import org.apache.commons.lang3.time.{DateFormatUtils, DateUtils}
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-27
  */
object DwSerializers {

  def all = List(
    JDateSerializer
  )

}

case object JDateSerializer extends CustomSerializer[Date](format => ( {
  case JString(s) => {
    try {
      DateUtils.parseDate(s, "yyyy-MM-dd HH:mm:ss")
    } catch {
      case _ => DateUtils.parseDate(s, "yyyy-MM-dd")
    }
  }
  case JNull => null
}, {
  case date: Date => {
    try {
      JString(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"))
    } catch {
      case _ => JString(DateFormatUtils.format(date, "yyyy-MM-dd"))
    }
  }
}
))

