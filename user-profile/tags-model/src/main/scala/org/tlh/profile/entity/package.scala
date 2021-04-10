package org.tlh.profile

import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

import scala.collection.mutable

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-09
  */
package object entity {

  /**
    * 标签数据
    *
    * @param id
    * @param name
    * @param rule
    */
  case class Tag(id: Long, name: String, rule: String)

  /**
    * hBase元数据
    *
    * @param table
    * @param rowkey
    * @param columns
    */
  case class HBaseCatalog(table: HBaseTable,
                          rowkey: String,
                          columns: mutable.Map[String, HBaseColumn]) {

    private[this] implicit val formats = DefaultFormats

    def toJson(): String = {
      write(this)
    }

  }

  case class HBaseTable(namespace: String,
                        name: String)

  case class HBaseColumn(cf: String,
                         col: String,
                         `type`: String)

}
