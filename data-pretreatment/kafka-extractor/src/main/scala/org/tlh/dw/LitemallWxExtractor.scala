package org.tlh.dw

import java.time.Duration
import java.util.Properties

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.tlh.dw.entity.UserGoods

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-22
  */
object LitemallWxExtractor extends App {

  import Serdes._

  val source_topic = "litemall-wx"
  val target_topic = "litemall-recommend"

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "litemall-wx-extractor")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-master:9092")
    p
  }

  val builder: StreamsBuilder = new StreamsBuilder

  // 设置输入topic
  val events: KStream[String, String] = builder.stream[String, String](source_topic)

  // 提取数据
  val results: KStream[String, String] = events.filter((_, value) => {
    // 为空处理
    if (value == null || "".equals(value)) {
      false
    } else {
      // 有效性处理
      val attrs = value.split("#CS")
      attrs.size == 2
    }
  }).mapValues(UserGoods(_))
    .filter((_, item) => {
      item.eventType match {// 保留关注的事件
        case 2 => true
        case 3 => true
        case 7 => item.valueType == 0
        case 8 => item.valueType == 0 && item.collected
        case _ => false
      }
    })
    .mapValues(_.toString)

  // 保存到另一个topic
  results.to(target_topic)

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10))
  }

}
