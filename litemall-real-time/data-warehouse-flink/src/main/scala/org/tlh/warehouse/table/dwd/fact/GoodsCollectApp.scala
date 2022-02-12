package org.tlh.warehouse.table.dwd.fact

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-02-12
  */
object GoodsCollectApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)
  val tableEnv = StreamTableEnvironment.create(env)

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

  // 商品纬度表 todo 该数据中hive中获取 使用look up join
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

  tableEnv.executeSql(
    s"""
       |create table dws_goods_collect_wide(
       |	`spu_id` int COMMENT '商品表的商品ID  spu_id',
       |  `sku_id` int COMMENT '商品货品表的货品ID',
       |	`goods_sn` string COMMENT '商品编号',
       |	`goods_name` string COMMENT '商品名称',
       |	`user_id` int comment '用户id',
       |  `category_id` int COMMENT '商品所属一级类目ID',
       |  `category_name` string comment '商品所属一级类目名称',
       |  `category2_id` int COMMENT '商品所属二级类目ID',
       |  `category2_name` string comment '商品所属二级类目名称',
       |  `brand_id` int comment '品牌ID',
       |  `brand_name` string comment '品牌名称',
       |	`collect_time` timestamp(3) COMMENT '收藏时间',
       |   PRIMARY KEY(spu_id) NOT ENFORCED
       |)
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_GOODS_COLLECT}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dws_db_collect',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dws_goods_collect_wide
      |select
      |  c.value_id,
      |  g.sku_id,
      |  g.goods_sn,
      |  g.name,
      |  c.user_id,
      |  g.category_id,
      |  g.category_name,
      |  g.category2_id,
      |  g.category2_name,
      |  g.brand_id,
      |  g.brand_name,
      |  c.collect_time
      |from
      |(
      | select
      |   *
      | from dwd_fact_collect_info
      | where type=0
      |)c
      |left join dwd_dim_goods_info g
      |on c.value_id=g.spu_id
    """.stripMargin)

}
