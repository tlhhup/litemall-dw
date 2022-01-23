package org.tlh.warehouse.util

import com.typesafe.config.ConfigFactory

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-17
  */
object AppConfig {

  private[this] val config = ConfigFactory.load("application.conf")

  val PHOENIX_DRIVER = config.getString("phoenix.driver")
  val PHOENIX_URL = config.getString("phoenix.url")
  val HBASE_ZK = config.getString("hbase.zk")

  val KAFKA_SERVERS = config.getString("kafka.servers")
  val kafka_input_topic = config.getString("kafka.input.topic")
  val KAFKA_INPUT_FRONT_TOPIC = config.getString("kafka.input.front.topic")
  val KAFKA_OUTPUT_ODS_LOG_START = config.getString("kafka.output.ods.log.start")
  val KAFKA_OUTPUT_ODS_LOG_EVENT = config.getString("kafka.output.ods.log.event")

  val CLICKHOUSE_DRIVER = config.getString("clickhouse.driver")
  val CLICKHOUSE_URL = config.getString("clickhouse.url")
  val CLICKHOUSE_USER = config.getString("clickhouse.user")

  val redis_master = config.getString("redis.master")
  val redis_sentinels = config.getString("redis.sentinels")
  val redis_pool_max = config.getInt("redis.pool.max")
  val redis_pool_idle_max = config.getInt("redis.pool.idle.max")
  val redis_pool_idle_min = config.getInt("redis.pool.idle.min")
  val redis_db_index = config.getInt("redis.db.index")

  val flink_ck_dir = config.getString("flink.ck.dir")
  val flink_ck_user = config.getString("flink.ck.user")

}
