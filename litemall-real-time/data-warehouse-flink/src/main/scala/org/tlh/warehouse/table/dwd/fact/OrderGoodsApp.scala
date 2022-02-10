package org.tlh.warehouse.table.dwd.fact

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-28
  */
object OrderGoodsApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.enableCheckpointing(3 * 1000)
  env.setParallelism(3)
  val tableEnv = StreamTableEnvironment.create(env)

  // 定义商品纬度表
  tableEnv.executeSql(
    s"""
       |create table dwd_dim_goods_info(
       |	  `sku_id` int COMMENT '商品货品表的货品ID',
       |  	`goods_sn` string COMMENT '商品编号',
       |  	`spu_id` int COMMENT '商品表的ID',
       |  	`name` string COMMENT '商品名称',
       |  	`category_id` int COMMENT '商品所属一级类目ID',
       |  	`category_name` string comment '商品所属一级类目名称',
       |  	`category2_id` int COMMENT '商品所属二级类目ID',
       |  	`category2_name` string comment '商品所属二级类目名称',
       |  	`brand_id` int comment '品牌ID',
       |  	`brand_name` string comment '品牌名称',
       |  	`brief` string COMMENT '商品简介',
       |  	`unit` string COMMENT '商品单位，例如件、盒',
       |  	`product_price` decimal(10,2) comment '商品货品表的价格',
       |  	`counter_price` decimal(10,2) COMMENT '专柜价格',
       |  	`retail_price` decimal(10,2) COMMENT '零售价格',
       |  	`add_time` string COMMENT '创建时间',
       |    PRIMARY KEY (sku_id) NOT ENFORCED
       |)comment '商品维度表'
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_GOODS}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_goods',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
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

  // 定义订单详情表
  tableEnv.executeSql(
    s"""
       |create table dwd_fact_order_goods_info(
       |	`id` int,
       |	`order_id` int COMMENT '订单表的订单ID',
       |	`goods_id` int COMMENT '商品表的商品ID  spu_id',
       |	`goods_name` string COMMENT '商品名称',
       |	`goods_sn` string COMMENT '商品编号',
       |	`product_id` int COMMENT '商品货品表的货品ID sku_id',
       |	`number` smallint COMMENT '商品货品的购买数量',
       |	`price` decimal(10,2)  COMMENT '商品货品的售价',
       |	`add_time` timestamp(3) COMMENT '创建时间',
       |	`user_id` int comment '用户id',
       |  `province` int COMMENT '省份ID',
       |  `city` int COMMENT '城市ID',
       |  `country` int COMMENT '乡镇ID',
       |  PRIMARY KEY(id) NOT ENFORCED
       |)comment '订单详情事实表'
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_ORDER_GOODS}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_order_goods',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  // 创建视图进行关联
  tableEnv.executeSql(
    """
      |create view order_goods_wide_view
      |as
      |(
      |	select
      |		og.id,
      |		og.order_id,
      |		og.goods_id as spu_id,
      |		o.sku_id,
      |		o.goods_sn,
      |		og.goods_name,
      |		og.number,
      |		og.price,
      |   og.user_id,
      |		o.category_id,
      |		o.category_name,
      |		o.category2_id,
      |		o.category2_name,
      |		o.brand_id,
      |		o.brand_name,
      |		o.product_price,
      |		o.counter_price,
      |		o.retail_price,
      |   og.province,
      |   r.province_name,
      |   og.city,
      |   r.city_name,
      |   og.country,
      |   r.country_name,
      |   og.add_time
      |	from dwd_fact_order_goods_info og
      |	left join dwd_dim_goods_info o
      |	on og.goods_id=o.spu_id
      | left join dwd_dim_region_info r
      | on og.country=r.country_id
      |)
    """.stripMargin)

  tableEnv.executeSql(
    s"""
       |create table dws_order_goods_wide(
       |  `id` int,
       |	`order_id` int COMMENT '订单表的订单ID',
       |	`goods_id` int COMMENT '商品表的商品ID  spu_id',
       |  `sku_id` int COMMENT '商品货品表的货品ID',
       |	`goods_sn` string COMMENT '商品编号',
       |	`goods_name` string COMMENT '商品名称',
       |	`number` smallint COMMENT '商品货品的购买数量',
       |	`price` decimal(10,2)  COMMENT '商品货品的售价',
       |	`user_id` int comment '用户id',
       |  `category_id` int COMMENT '商品所属一级类目ID',
       |  `category_name` string comment '商品所属一级类目名称',
       |  `category2_id` int COMMENT '商品所属二级类目ID',
       |  `category2_name` string comment '商品所属二级类目名称',
       |  `brand_id` int comment '品牌ID',
       |  `brand_name` string comment '品牌名称',
       |  `product_price` decimal(10,2) comment '商品货品表的价格',
       |  `counter_price` decimal(10,2) COMMENT '专柜价格',
       |  `retail_price` decimal(10,2) COMMENT '零售价格',
       |  `province` int COMMENT '省份ID',
       |  `province_name` string comment '省份名称',
       |  `city` int COMMENT '城市ID',
       |  `city_name` string comment '城市名称',
       |  `country` int COMMENT '乡镇ID',
       |  `country_name` string comment '乡镇名称',
       |	`add_time` timestamp(3) COMMENT '创建时间',
       |   PRIMARY KEY(id) NOT ENFORCED
       |)
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_GOODS_DETAIL}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_goods',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dws_order_goods_wide
      |select
      | *
      |from order_goods_wide_view
    """.stripMargin)


}
