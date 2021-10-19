package org.tlh.warehouse.entity

import java.nio.charset.StandardCharsets

import org.apache.flink.api.common.serialization.{DeserializationSchema, SerializationSchema}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read, write}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-18
  */
case class MaxWellEntity(database: String,
                         table: String,
                         `type`: String,
                         ts: Long,
                         data: Map[String, Any])

class MaxWellEntitySchema extends DeserializationSchema[MaxWellEntity] with SerializationSchema[MaxWellEntity] {

  override def deserialize(message: Array[Byte]): MaxWellEntity = {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    val json = new String(message, StandardCharsets.UTF_8)
    read[MaxWellEntity](json)
  }

  override def isEndOfStream(nextElement: MaxWellEntity): Boolean = false

  override def serialize(element: MaxWellEntity): Array[Byte] = {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    write[MaxWellEntity](element).getBytes(StandardCharsets.UTF_8)
  }

  override def getProducedType: TypeInformation[MaxWellEntity] = TypeInformation.of(classOf[MaxWellEntity])
}