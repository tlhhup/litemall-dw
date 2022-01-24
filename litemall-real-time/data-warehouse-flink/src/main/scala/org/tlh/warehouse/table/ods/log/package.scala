package org.tlh.warehouse.table.ods

import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.annotation.{DataTypeHint, FunctionHint}
import org.apache.flink.table.functions.{ScalarFunction, TableFunction}
import org.apache.flink.types.Row
import org.json4s.{DefaultFormats, JObject}
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.tlh.warehouse.util.LogValidateUtil


/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-23
  */
package object log {

  class IsStartLog extends ScalarFunction {

    def eval(line: String): Boolean = {
      var isValid = false
      if (StringUtils.isNotBlank(line) && line.contains("start")) {
        isValid = LogValidateUtil.isStartLog(line)
      }
      isValid
    }
  }

  class IsEventLog extends ScalarFunction {

    def eval(line: String): Boolean = {
      var isValid = false
      if (StringUtils.isNotBlank(line) && !line.contains("start")) {
        isValid = LogValidateUtil.isEventLog(line)
      }
      isValid
    }
  }

  class GetJsonObject extends ScalarFunction {

    def eval(line: String, path: String): String = {
      if (StringUtils.isEmpty(line) || StringUtils.isEmpty(path) || path.charAt(0) != '$') {
        return ""
      } else {
        import org.json4s.{Formats, NoTypeHints}
        import org.json4s.jackson.JsonMethods.parse

        implicit val formats: Formats = Serialization.formats(NoTypeHints)
        var item = parse(line)

        // 拆分表达式
        val pathExpr = path.split("\\.")
        // 迭代获取最里层元素
        for (pathExprStart <- 1 to pathExpr.length-1) {
          item = item \ pathExpr(pathExprStart)
        }

        item.extract[String]
      }
    }

  }

  class ParseEventJsonObject extends ScalarFunction {

    def eval(line: String, path: String): String = {
      if (StringUtils.isEmpty(line)) {
        return ""
      }
      if (StringUtils.isEmpty(path)) {
        return line
      } else {
        val attrs = line.split("\\|")
        //1. 解析时间
        if ("st".eq(path)) {
          return attrs(0)
        } else {
          import org.json4s.{Formats, NoTypeHints}
          import org.json4s.jackson.JsonMethods.parse

          implicit val formats: Formats = Serialization.formats(NoTypeHints)
          val item = parse(attrs(1))

          // 2. 解析事件数据
          var result = ""
          if ("et".eq(path)) {
            result = write((item \ "et"))
          } else {
            // 3. 解析cm数据
            result = (item \ "cm" \ path).extract[String]
          }

          return result
        }
      }
    }

  }

  @FunctionHint(output = new DataTypeHint("ROW<event_type STRING, event_json STRING>"))
  class ExtractEventType extends TableFunction[Row] {

    def eval(event: String): Unit = {
      if (StringUtils.isNotBlank(event)) {
        import org.json4s.{Formats}
        import org.json4s.jackson.JsonMethods.parse

        implicit val formats: Formats = DefaultFormats
        val events = parse(event).extract[List[JObject]]
        for (item <- events) {

          // 解析event_type
          val eventType = (item \ "en").extract[String]

          collect(Row.of(eventType, write(item)))
        }
      }
    }

  }

}
