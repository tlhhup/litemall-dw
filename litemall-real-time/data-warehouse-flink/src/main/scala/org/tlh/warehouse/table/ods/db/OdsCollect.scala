package org.tlh.warehouse.table.ods.db

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-02-12
  */
object OdsCollect extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)
  env.enableCheckpointing(3 * 1000)
  val tableEnv = StreamTableEnvironment.create(env)

  // 创建表
  tableEnv.executeSql(
    s"""
       |create table ods_collect(
       |  `id` int,
       |  `user_id` int COMMENT '用户表的用户ID',
       |  `value_id` int COMMENT '如果type=0，则是商品ID；如果type=1，则是专题ID',
       |  `type` tinyint COMMENT '收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID',
       |  `add_time` timestamp(3) COMMENT '创建时间',
       |  `update_time` timestamp(3) COMMENT '更新时间',
       |  `deleted` tinyint COMMENT '逻辑删除',
       |  PRIMARY KEY(id) NOT ENFORCED
       |)comment '收藏表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_COLLECT}'
       |)
    """.stripMargin)

  tableEnv.executeSql(
    s"""
       |create table dwd_fact_collect_info(
       |	`id` int,
       |	`user_id` int COMMENT '用户表的用户ID',
       |	`value_id` int COMMENT '如果type=0，则是商品ID；如果type=1，则是专题ID',
       |	`type` tinyint COMMENT '收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID',
       |	`collect_time` timestamp(3) COMMENT '收藏时间',
       | PRIMARY KEY(id) NOT ENFORCED
       |)comment '收藏事实表'
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_COLLECT}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_collect',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dwd_fact_collect_info
      |select
      | id,
      | user_id,
      | value_id,
      | type,
      | add_time
      |from ods_collect
      |where deleted=0
    """.stripMargin)

}
