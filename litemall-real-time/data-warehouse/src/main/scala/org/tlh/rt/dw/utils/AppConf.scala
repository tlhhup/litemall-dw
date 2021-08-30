package org.tlh.rt.dw.utils

import com.typesafe.config.ConfigFactory

/**
  * @author 离歌笑
  * @desc
  * @date 2021-08-30
  */
object AppConf {

  private[this] val config = ConfigFactory.load("application.conf")

  val PHOENIX_DRIVER = config.getString("phoenix.driver")
  val PHOENIX_URL = config.getString("phoenix.url")
  val HBASE_ZK = config.getString("hbase.zk")

  val KAFKA_SERVERS = config.getString("kafka.servers")

  val CLICKHOUSE_DRIVER = config.getString("clickhouse.driver")
  val CLICKHOUSE_URL = config.getString("clickhouse.url")
  val CLICKHOUSE_USER = config.getString("clickhouse.user")

}
