package org.tlh.rt.dw.utils

import java.util
import java.util.{Map, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.TopicPartition
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010.OffsetRange
import org.tlh.spark.util.JedisUtil
import redis.clients.jedis.Jedis

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-26
  */
object KafkaUtil extends Logging {

  /**
    * 读取偏移量
    *
    * @param topic
    * @param consumerGroup
    * @return
    */
  def readOffSet(topic: String, consumerGroup: String): collection.Map[TopicPartition, Long] = {
    val jedis: Jedis = JedisUtil.getJedis
    try {
      //1. 读取偏移量
      val key = s"offset:$topic:$consumerGroup"
      val offSets: Map[String, String] = jedis.hgetAll(key)

      //2. 转化数据
      import scala.collection.JavaConverters._

      offSets.asScala.map {
        case (partition, offset) => {
          logInfo(s"read offset $topic:$consumerGroup  partition:$partition offset:$offset")
          (new TopicPartition(topic, partition.toInt), offset.toLong)
        }
      }.toMap
    } finally {
      jedis.close()
    }
  }

  /**
    * 保存偏移量
    *
    * @param topic
    * @param consumerGroup
    * @param offSets
    * @return
    */
  def saveOffSet(topic: String, consumerGroup: String, offSets: Array[OffsetRange]): Unit = {
    val jedis: Jedis = JedisUtil.getJedis
    try {
      //1. 读取偏移量
      val key = s"offset:$topic:$consumerGroup"

      //2. 处理数据
      val offSetMap = new util.HashMap[String, String]()
      for (offset <- offSets) {
        logInfo(s"save offset $topic:$consumerGroup  partition:${offset.partition} offset:${offset.untilOffset}")
        offSetMap.put(offset.partition.toString, offset.untilOffset.toString)
      }

      //3. 保存数据
      jedis.hmset(key, offSetMap)
    } finally {
      jedis.close()
    }
  }


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
