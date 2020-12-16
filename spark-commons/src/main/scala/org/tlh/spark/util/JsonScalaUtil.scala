package org.tlh.spark.util

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
object JsonScalaUtil {

  private[this] val objectMapper = {
    val mapper = new ObjectMapper()
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
    mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
    mapper.registerModule(DefaultScalaModule)
    mapper
  }

  def toJson(data: AnyRef): String = {
    this.objectMapper.writeValueAsString(data)
  }

  def toBean[T](clazz: Class[T], json: String): T = {
    this.objectMapper.readValue(json, clazz)
  }

}
