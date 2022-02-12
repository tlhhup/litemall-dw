package org.tlh.warehouse.table.dws

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-30
  */
object GoodsItemTopic extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)
  val tableEnv = StreamTableEnvironment.create(env)

  // 商品订单详情
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
       |   WATERMARK FOR add_time AS add_time - INTERVAL '5' SECOND
       |)
       |WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_GOODS_DETAIL}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_goods',
       |  'format' = 'json',
       |  'scan.startup.mode' = 'earliest-offset'
       |)
    """.stripMargin)

  // 商品收藏
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
       |   WATERMARK FOR collect_time AS collect_time - INTERVAL '5' SECOND
       |)
       |WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_GOODS_COLLECT}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dws_db_collect',
       |  'format' = 'json',
       |  'scan.startup.mode' = 'earliest-offset'
       |)
    """.stripMargin)

  /**
    * 商品ID 下单次数 下单数量 收藏次数
    */

  // 定义窗口函数
  tableEnv.executeSql(
    """
      |select
      | TUMBLE_START(add_time, INTERVAL '10' SECOND),
      | TUMBLE_END(add_time, INTERVAL '10' SECOND),
      | spu_id,
      | sum(if(order_id>0,1,0)),
      | sum(number),
      | sum(collect_number)
      |from
      |(
      |select
      | goods_id as spu_id,
      | order_id,
      | sku_id,
      | number,
      | 0 as collect_number,
      | add_time
      |from dws_order_goods_wide
      |
      |union all
      |
      | select
      |   spu_id,
      |   0 as order_id,
      |   sku_id,
      |   0 as number,
      |   1 as collect_number,
      |   collect_time as add_time
      | from dws_goods_collect_wide
      |) tmp
      |group by
      | TUMBLE(add_time, INTERVAL '10' SECOND),
      | spu_id
    """.stripMargin)
    .print()

}
