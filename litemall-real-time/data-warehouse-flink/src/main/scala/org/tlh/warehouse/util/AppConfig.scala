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
  val KAFKA_OUTPUT_DWD_LOG_AD = config.getString("kafka.output.dwd.log.ad")
  val KAFKA_OUTPUT_DWD_LOG_ADD_CAR = config.getString("kafka.output.dwd.log.addCar")
  val KAFKA_OUTPUT_DWD_LOG_COMMENT = config.getString("kafka.output.dwd.log.comment")
  val KAFKA_OUTPUT_DWD_LOG_DISPLAY = config.getString("kafka.output.dwd.log.display")
  val KAFKA_OUTPUT_DWD_LOG_FAVORITES = config.getString("kafka.output.dwd.log.favorites")
  val KAFKA_OUTPUT_DWD_LOG_LOADING = config.getString("kafka.output.dwd.log.loading")
  val KAFKA_OUTPUT_DWD_LOG_PRAISE = config.getString("kafka.output.dwd.log.praise")
  val KAFKA_OUTPUT_DWD_DB_PAYMENT = config.getString("kafka.output.dwd.db.payment")
  val KAFKA_OUTPUT_DWD_DB_REFUND = config.getString("kafka.output.dwd.db.refund")
  val KAFKA_OUTPUT_DWD_DB_ORDER = config.getString("kafka.output.dwd.db.order")
  val KAFKA_OUTPUT_DWD_DB_GOODS = config.getString("kafka.output.dwd.db.goods")
  val KAFKA_OUTPUT_DWD_DB_ORDER_GOODS = config.getString("kafka.output.dwd.db.orderGoods")
  val KAFKA_OUTPUT_DWD_DB_REGION = config.getString("kafka.output.dwd.db.region")
  val KAFKA_OUTPUT_DWD_DB_COLLECT = config.getString("kafka.output.dwd.db.collect")
  val KAFKA_OUTPUT_DWS_DB_GOODS_DETAIL = config.getString("kafka.output.dws.db.goodsDetail")
  val KAFKA_OUTPUT_DWS_DB_PAYMENT = config.getString("kafka.output.dws.db.payment")
  val KAFKA_OUTPUT_DWS_DB_REFUND = config.getString("kafka.output.dws.db.refund")
  val KAFKA_OUTPUT_DWS_DB_GOODS_COLLECT = config.getString("kafka.output.dws.db.collect")
  val KAFKA_OUTPUT_DWS_DB_ORDER = config.getString("kafka.output.dws.db.order")
  val KAFKA_OUTPUT_DWS_LOG_GOODS_DISPLAY = config.getString("kafka.output.dws.log.goodsDisplay")

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

  val MYSQL_HOST = config.getString("mysql.host")
  val MYSQL_PORT = config.getInt("mysql.port")
  val MYSQL_USERNAME = config.getString("mysql.username")
  val MYSQL_PASSWORD = config.getString("mysql.password")
  val MYSQL_CDC_DB = config.getString("mysql.cdc.db")
  val MYSQL_CDC_ODS_ORDER = config.getString("mysql.cdc.ods.order")
  val MYSQL_CDC_ODS_GROUPON = config.getString("mysql.cdc.ods.groupon")
  val MYSQL_CDC_ODS_PRODUCTS = config.getString("mysql.cdc.ods.products")
  val MYSQL_CDC_ODS_GOODS = config.getString("mysql.cdc.ods.goods")
  val MYSQL_CDC_ODS_BRAND = config.getString("mysql.cdc.ods.brand")
  val MYSQL_CDC_ODS_CATEGORY = config.getString("mysql.cdc.ods.category")
  val MYSQL_CDC_ODS_ORDER_GOODS = config.getString("mysql.cdc.ods.orderGoods")
  val MYSQL_CDC_ODS_REGION = config.getString("mysql.cdc.ods.region")
  val MYSQL_CDC_ODS_COLLECT = config.getString("mysql.cdc.ods.collect")

}
