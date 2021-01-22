package org.tlh.dw

import java.time.Duration
import java.util.Properties

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-01-22
  */
object LitemallWxExtractor extends App {

  import Serdes._

  val source_topic = "litemall-wx"
  val target_topic = "litemall-recommend"
  val filter_events = Array(2, 3, 7, 8)

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
  val results: KStream[String, String] = events.filter((key, value) => {
    // 为空处理
    if (value == null || "".equals(value)) {
      false
    } else {
      // 有效性处理
      var attrs = value.split("#CS")
      if (attrs.size != 2) {
        false
      } else {
        // 提取事件类型
        val data = attrs(1)
        attrs = data.split("\\|")
        val eventType = attrs(0).toInt
        filter_events.contains(eventType)
      }
    }
  })

  // 保存到另一个topic
  results.to(target_topic)

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10))
  }

}
