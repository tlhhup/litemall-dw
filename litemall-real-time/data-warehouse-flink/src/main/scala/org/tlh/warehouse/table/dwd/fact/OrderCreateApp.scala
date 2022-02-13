package org.tlh.warehouse.table.dwd.fact

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-02-13
  */
object OrderCreateApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)
  val tableEnv = StreamTableEnvironment.create(env)


  // 订单表
  tableEnv.executeSql(
    s"""
       |create table dwd_fact_order_info(
       |	`id` int,
       |	`user_id` int COMMENT '用户表的用户ID',
       |	`order_sn` string COMMENT '订单编号',
       |	`order_status` smallint COMMENT '订单状态',
       |	`goods_price` decimal(10,2) COMMENT '商品总费用',
       |	`freight_price` decimal(10,2) COMMENT '配送费用',
       |	`coupon_price` decimal(10,2) COMMENT '优惠券减免',
       |	`integral_price` decimal(10,2) COMMENT '用户积分减免',
       |	`groupon_id` int comment '团购ID',
       |	`groupon_price` decimal(10,2) COMMENT '团购优惠价减免',
       |	`order_price` decimal(10,2) COMMENT '订单费用， = goods_price + freight_price - coupon_price',
       |	`actual_price` decimal(10,2) COMMENT '实付费用， = order_price - integral_price',
       |	`add_time` timestamp(3) COMMENT '创建时间',
       |	`province` int COMMENT '省份ID',
       |	`city` int COMMENT '城市ID',
       |	`country` int COMMENT '乡镇ID'
       |)comment '订单事实表'
       |WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_ORDER}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_order',
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

  // 订单宽表
  tableEnv.executeSql(
    s"""
       |create table dws_order_wide(
       |	`id` int,
       |	`user_id` int COMMENT '用户表的用户ID',
       |	`order_sn` string COMMENT '订单编号',
       |	`order_status` smallint COMMENT '订单状态',
       |	`goods_price` decimal(10,2) COMMENT '商品总费用',
       |	`freight_price` decimal(10,2) COMMENT '配送费用',
       |	`coupon_price` decimal(10,2) COMMENT '优惠券减免',
       |	`integral_price` decimal(10,2) COMMENT '用户积分减免',
       |	`groupon_id` int comment '团购ID',
       |	`groupon_price` decimal(10,2) COMMENT '团购优惠价减免',
       |	`order_price` decimal(10,2) COMMENT '订单费用， = goods_price + freight_price - coupon_price',
       |	`actual_price` decimal(10,2) COMMENT '实付费用， = order_price - integral_price',
       |	`add_time` timestamp(3) COMMENT '创建时间',
       |	`province` int COMMENT '省份ID',
       |  `province_name` string COMMENT '省份名称',
       |	`city` int COMMENT '城市ID',
       |  `city_name` string COMMENT '城市名称',
       |	`country` int COMMENT '乡镇ID',
       |  `country_name` string COMMENT '乡镇名称',
       |   PRIMARY KEY(id) NOT ENFORCED
       |)comment '订单事实表'
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_ORDER}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dws_db_order',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dws_order_wide
      |select
      | o.id,
      | o.user_id,
      | o.order_sn,
      | o.order_status,
      | o.goods_price,
      | o.freight_price,
      | o.coupon_price,
      | o.integral_price,
      | o.groupon_id,
      | o.groupon_price,
      | o.order_price,
      | o.actual_price,
      | o.add_time,
      | o.province,
      | r.province_name,
      | o.city,
      | r.city_name,
      | o.country,
      | r.country_name
      |from dwd_fact_order_info o
      |left join dwd_dim_region_info r
      |on o.country=r.country_id
    """.stripMargin)
}
