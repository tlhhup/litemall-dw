package org.tlh.warehouse.table.dwd.dim

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-28
  */
object RegionApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.enableCheckpointing(3 * 1000)
  env.setParallelism(3)

  val tableEnv=StreamTableEnvironment.create(env)

  // 创建region source
  tableEnv.executeSql(
    s"""
      |create table ods_region(
      |  `id` int,
      |  `pid` int COMMENT '行政区域父ID，例如区县的pid指向市，市的pid指向省，省的pid则是0',
      |  `name` string COMMENT '行政区域名称',
      |  `type` tinyint COMMENT '行政区域类型，如如1则是省， 如果是2则是市，如果是3则是区县',
      |  `code` int COMMENT '行政区域编码',
      |  PRIMARY KEY(id) NOT ENFORCED
      |)comment '行政区域表'
      |WITH (
      | 'connector' = 'mysql-cdc',
      | 'hostname' = '${AppConfig.MYSQL_HOST}',
      | 'port' = '${AppConfig.MYSQL_PORT}',
      | 'username' = '${AppConfig.MYSQL_USERNAME}',
      | 'password' = '${AppConfig.MYSQL_PASSWORD}',
      | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
      | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_REGION}'
      |)
    """.stripMargin)

  // 创建试图
  tableEnv.executeSql(
    """
      |create view region_view(
      |  country_id,
      |  country_name,
      |  city_id,
      |  city_name,
      |  province_id,
      |  province_name
      |)comment '区域视图'
      |as
      |(
      |  select
      |    co.country_id,
      |    co.country_name,
      |    c.city_id,
      |    c.city_name,
      |    p.province_id,
      |    p.province_name
      |  from
      |  (
      |    select
      |      id as country_id,
      |      name as country_name,
      |      pid
      |    from ods_region
      |    where type=3
      |  )co inner join
      |  (
      |    select
      |      id as city_id,
      |      name as city_name,
      |      pid
      |    from ods_region
      |    where type=2
      |  )c on co.pid=c.city_id
      |  inner join
      |  (
      |    select
      |      id as province_id,
      |      name as province_name
      |    from ods_region
      |    where type=1
      |  )p on c.pid=p.province_id
      |)
    """.stripMargin)

  // 定义sink
  tableEnv.executeSql(
    s"""
      |create table dwd_dim_region_info(
      |  country_id int,
      |  country_name string,
      |  city_id int,
      |  city_name string,
      |  province_id int,
      |  province_name string,
      |  PRIMARY KEY(country_id) NOT ENFORCED
      |)comment '行政区域表'
      |WITH (
      |  'connector' = 'upsert-kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_REGION}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'dwd_db_region',
      |  'key.format' = 'json',
      |  'value.format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dwd_dim_region_info
      |select
      | *
      |from region_view
    """.stripMargin)

}
