package org.tlh.warehouse.table.dwd.fact

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-02-14
  */
object GoodsDisplayApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)
  val tableEnv = StreamTableEnvironment.create(env)


  // 展示
  tableEnv.executeSql(
    s"""
       |create table dwd_event_display_log(
       |	`mid` string comment '设备id',
       |	`uid` string comment '用户id',
       |	`mail` string comment '邮箱',
       |	`version_code` string comment '程序版本号',
       |	`version_name` string comment '程序版本名',
       |	`language` string comment '系统语言',
       |	`source` string comment '应用从那个渠道来的',
       |	`os` string comment '系统版本',
       |	`area` string comment '区域',
       |	`model` string comment '手机型号',
       |	`brand` string comment '手机品牌',
       |	`sdk_version` string,
       |	`hw` string comment '屏幕宽高',
       |	`app_time` string comment '时间戳',
       |	`goods_id` int comment '商品id',
       |	`action` string,
       |	`extend1` string,
       |	`place` string,
       |	`category` string comment '商品分类'
       |)comment '展示事件表'
       |WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_LOG_DISPLAY}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'event_display',
       |  'format' = 'json'
       |)
    """.stripMargin)

  // 商品纬度表
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
       |create table dws_goods_display_wide(
       |	`spu_id` int COMMENT '商品表的商品ID  spu_id',
       |  `sku_id` int COMMENT '商品货品表的货品ID',
       |  `mid` string comment '设备id',
       |	`goods_sn` string COMMENT '商品编号',
       |	`goods_name` string COMMENT '商品名称',
       |	`user_id` int comment '用户id',
       |  `category_id` int COMMENT '商品所属一级类目ID',
       |  `category_name` string comment '商品所属一级类目名称',
       |  `category2_id` int COMMENT '商品所属二级类目ID',
       |  `category2_name` string comment '商品所属二级类目名称',
       |  `brand_id` int comment '品牌ID',
       |  `brand_name` string comment '品牌名称',
       |	`display_time` timestamp(3) COMMENT '展示时间',
       |   PRIMARY KEY(spu_id) NOT ENFORCED
       |)
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_LOG_GOODS_DISPLAY}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dws_log_display',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dws_goods_display_wide
      |select
      |  d.goods_id,
      |  g.sku_id,
      |  d.mid,
      |  g.goods_sn,
      |  g.name,
      |  cast(d.uid as int),
      |  g.category_id,
      |  g.category_name,
      |  g.category2_id,
      |  g.category2_name,
      |  g.brand_id,
      |  g.brand_name,
      |  TO_TIMESTAMP_LTZ(cast(d.app_time as bigint),3)
      |from dwd_event_display_log d
      |left join dwd_dim_goods_info g
      |on d.goods_id=g.spu_id
    """.stripMargin)

}
