package org.tlh.warehouse.table.dwd.dim

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-27
  */
object GoodItemApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)

  val tableEnv = StreamTableEnvironment.create(env)

  // 定义货品表 sku
  tableEnv.executeSql(
    s"""
       |create table ods_goods_product(
       |  `id` int,
       |  `goods_id` int,
       |  `specifications` string COMMENT '商品规格值列表，采用JSON数组格式',
       |  `price` decimal(10,2) COMMENT '商品货品价格',
       |  `number` int COMMENT '商品货品数量',
       |  `url` string COMMENT '商品货品图片',
       |  `add_time` string COMMENT '创建时间',
       |  `update_time` string COMMENT '更新时间',
       |  `deleted` tinyint COMMENT '逻辑删除',
       |  PRIMARY KEY (id) NOT ENFORCED
       |)comment '商品货品表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_PRODUCTS}'
       |)
    """.stripMargin)

  // 定义商品表 spu
  tableEnv.executeSql(
    s"""
       |create table ods_goods(
       |	  `id` int,
       |  	`goods_sn` string COMMENT '商品编号',
       |  	`name` string COMMENT '商品名称',
       |  	`category_id` int COMMENT '商品所属类目ID',
       |  	`brand_id` int comment '品牌ID',
       |  	`gallery` string COMMENT '商品宣传图片列表，采用JSON数组格式',
       |  	`keywords` string COMMENT '商品关键字，采用逗号间隔',
       |  	`brief` string COMMENT '商品简介',
       |  	`is_on_sale` tinyint COMMENT '是否上架',
       |  	`sort_order` smallint comment '排序编号',
       |  	`pic_url` string COMMENT '商品页面商品图片',
       |  	`share_url` string COMMENT '商品分享海报',
       |  	`is_new` tinyint COMMENT '是否新品首发，如果设置则可以在新品首发页面展示',
       |  	`is_hot` tinyint COMMENT '是否人气推荐，如果设置则可以在人气推荐页面展示',
       |  	`unit` string COMMENT '商品单位，例如件、盒',
       |  	`counter_price` decimal(10,2) COMMENT '专柜价格',
       |  	`retail_price` decimal(10,2) COMMENT '零售价格',
       |  	`detail` string COMMENT '商品详细介绍，是富文本格式',
       |  	`add_time` string COMMENT '创建时间',
       |  	`update_time` string COMMENT '更新时间',
       |  	`deleted` tinyint COMMENT '逻辑删除',
       |     PRIMARY KEY (id) NOT ENFORCED
       |)comment '商品基本信息表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_GOODS}'
       |)
    """.stripMargin)

  // 定义品牌表
  tableEnv.executeSql(
    s"""
       |create table ods_goods_brand(
       |  `id` int,
       |  `name` string COMMENT '品牌商名称',
       |  `desc` string COMMENT '品牌商简介',
       |  `pic_url` string COMMENT '品牌商页的品牌商图片',
       |  `sort_order` tinyint,
       |  `floor_price` decimal(10,2) COMMENT '品牌商的商品低价，仅用于页面展示',
       |  `add_time` string COMMENT '创建时间',
       |  `update_time` string  COMMENT '更新时间',
       |  `deleted` tinyint COMMENT '逻辑删除',
       |  PRIMARY KEY (id) NOT ENFORCED
       |)comment '商品品牌商表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_BRAND}'
       |)
    """.stripMargin)

  // 定义类别表
  tableEnv.executeSql(
    s"""
       |create table ods_goods_category(
       |  `id` int,
       |  `name` string COMMENT '类目名称',
       |  `keywords` string COMMENT '类目关键字，以JSON数组格式',
       |  `desc` string COMMENT '类目广告语介绍',
       |  `pid` int COMMENT '父类目ID',
       |  `icon_url` string COMMENT '类目图标',
       |  `pic_url` string COMMENT '类目图片',
       |  `level` string comment '层级',
       |  `sort_order` tinyint COMMENT '排序',
       |  `add_time` string COMMENT '创建时间',
       |  `update_time` string COMMENT '更新时间',
       |  `deleted` tinyint COMMENT '逻辑删除',
       |  PRIMARY KEY (id) NOT ENFORCED
       |)comment '商品类目表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_CATEGORY}'
       |)
    """.stripMargin)

  // 定义商品纬度表 sink
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

  //处理数据
  tableEnv.executeSql(
    """
      |INSERT INTO dwd_dim_goods_info
      |select
      |    ogp.id,
      |    og.goods_sn,
      |    og.id,
      |    og.name,
      |    c1.id,
      |    c1.name,
      |    c2.id,
      |    c2.name,
      |    ogb.id,
      |    ogb.name,
      |    og.brief,
      |    og.unit,
      |    ogp.price,
      |    og.counter_price,
      |    og.retail_price,
      |    og.add_time
      |from
      |(
      |    select
      |        id,
      |        goods_id,
      |        price
      |    from ods_goods_product
      |) ogp
      |join
      |(
      |    select
      |        id,
      |        goods_sn,
      |        name,
      |        category_id as category2_id,
      |        brand_id,
      |        brief,
      |        unit,
      |        counter_price,
      |        retail_price,
      |        add_time
      |    from ods_goods
      |) og on ogp.goods_id=og.id
      |join
      |(
      |    select
      |        id,
      |        name,
      |        pid
      |    from ods_goods_category
      |    where level='L2'
      |) c2 on og.category2_id=c2.id
      |join
      |(
      |    select
      |        id,
      |        name
      |    from ods_goods_category
      |    where level='L1'
      |)c1 on c1.id=c2.pid
      |join
      |(
      |    select
      |        id,
      |        name
      |    from ods_goods_brand
      |)ogb on og.brand_id=ogb.id
    """.stripMargin)

}
