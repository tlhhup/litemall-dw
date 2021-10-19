package org.tlh.warehouse.entity

import org.junit.Test
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read, write}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-18
  */
class MaxWellEntityTest {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  @Test
  def parseEntity() = {
    val json =
      """
        |{
        |    "database": "test",
        |    "table": "maxwell",
        |    "type": "insert",
        |    "ts": 1449786310,
        |    "xid": 940752,
        |    "commit": true,
        |    "data": { "id":1, "daemon": "Stanislaw Lem" }
        |  }
      """.stripMargin

    val maxWellEntity = read[MaxWellEntity](json)

    println(maxWellEntity)

    // 解析data
    val data = maxWellEntity.data
    println(write(data))
  }

}
