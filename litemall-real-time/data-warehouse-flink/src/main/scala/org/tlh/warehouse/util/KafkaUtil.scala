package org.tlh.warehouse.util

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-19
  */
object KafkaUtil {

  /**
    * 创建生产者
    *
    * @param servers
    * @return
    */
  def buildKafkaSender(servers: String): KafkaProducer[String, String] = {
    val prop = new Properties
    // 指定请求的kafka集群列表
    prop.put("bootstrap.servers", servers) // 指定响应方式
    prop.put("acks", "all")
    // 指定key的序列化方式, key是用于存放数据对应的offset
    prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // 指定value的序列化方式
    prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // 配置超时时间
    prop.put("request.timeout.ms", "60000")

    // 得到生产者的实例
    new KafkaProducer[String, String](prop)
  }

}
