package org.tlh.warehouse.table.dwd.fact

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-02-10
  */
object OrderPaymentApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)

  val tableEnv = StreamTableEnvironment.create(env)

  // 定义支付表
  tableEnv.executeSql(
    s"""
       |create table dwd_fact_payment_info(
       |	`order_id` int comment '订单ID',
       |	`user_id` int COMMENT '用户表的用户ID',
       |	`order_sn` string COMMENT '订单编号',
       |	`pay_price` decimal(10,2) COMMENT '实付费用',
       |	`pay_id` string COMMENT '微信付款编号',
       |	`pay_time` timestamp(3) COMMENT '微信付款时间',
       |	`add_time` timestamp(3) COMMENT '创建时间',
       |	`province` int COMMENT '省份ID',
       |	`city` int COMMENT '城市ID',
       |	`country` int COMMENT '乡镇ID'
       |)comment '订单支付事实表'
       |WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_PAYMENT}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_payment',
       |  'format' = 'json'
       |)
    """.stripMargin)

  // 定义区域纬度表
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

  // 定义支付款表
  tableEnv.executeSql(
    s"""
      |create table dws_order_payment_wide(
      |	`order_id` int comment '订单ID',
      |	`user_id` int COMMENT '用户表的用户ID',
      |	`order_sn` string COMMENT '订单编号',
      |	`pay_price` decimal(10,2) COMMENT '实付费用',
      |	`pay_id` string COMMENT '微信付款编号',
      |	`pay_time` timestamp(3) COMMENT '微信付款时间',
      |	`add_time` timestamp(3) COMMENT '创建时间',
      |	`province` int COMMENT '省份ID',
      | `province_name` string comment '省份名称',
      | `city` int COMMENT '城市ID',
      | `city_name` string comment '城市名称',
      | `country` int COMMENT '乡镇ID',
      | `country_name` string comment '乡镇名称',
      | PRIMARY KEY(order_id) NOT ENFORCED
      |)comment '订单支付事实表'
      |WITH (
      |  'connector' = 'upsert-kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_PAYMENT}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'dwd_db_payment',
      |  'key.format' = 'json',
      |  'value.format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dws_order_payment_wide
      |select
      |  p.order_id,
      |  p.user_id,
      |  p.order_sn,
      |  p.pay_price,
      |  p.pay_id,
      |  p.pay_time,
      |  p.add_time,
      |  p.province,
      |  r.province_name,
      |  p.city,
      |  r.city_name,
      |  p.country,
      |  r.country_name
      |from dwd_fact_payment_info p
      |left join dwd_dim_region_info r
      |on p.country=r.country_id
    """.stripMargin)

}
