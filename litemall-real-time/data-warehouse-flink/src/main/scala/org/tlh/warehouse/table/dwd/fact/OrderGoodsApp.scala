package org.tlh.warehouse.table.dwd.fact

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-27
  */
object OrderGoodsApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.enableCheckpointing(3 * 1000)
  env.setParallelism(3)
  val tableEnv = StreamTableEnvironment.create(env)

  // 定义订单
  tableEnv.executeSql(
    s"""
       |create table ods_order(
       |  `id` int,
       |  `user_id` int COMMENT '用户表的用户ID',
       |  `order_sn` string COMMENT '订单编号',
       |  `order_status` smallint COMMENT '订单状态',
       |  `aftersale_status` smallint COMMENT '售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消',
       |  `consignee` string COMMENT '收货人名称',
       |  `mobile` string COMMENT '收货人手机号',
       |  `address` string COMMENT '收货具体地址',
       |  `message` string COMMENT '用户订单留言',
       |  `goods_price` decimal(10,2) COMMENT '商品总费用',
       |  `freight_price` decimal(10,2) COMMENT '配送费用',
       |  `coupon_price` decimal(10,2) COMMENT '优惠券减免',
       |  `integral_price` decimal(10,2) COMMENT '用户积分减免',
       |  `groupon_price` decimal(10,2) COMMENT '团购优惠价减免',
       |  `order_price` decimal(10,2) COMMENT '订单费用， = goods_price + freight_price - coupon_price',
       |  `actual_price` decimal(10,2) COMMENT '实付费用， = order_price - integral_price',
       |  `pay_id` string COMMENT '微信付款编号',
       |  `pay_time` string COMMENT '微信付款时间',
       |  `ship_sn` string COMMENT '发货编号',
       |  `ship_channel` string COMMENT '发货快递公司',
       |  `ship_time` string COMMENT '发货开始时间',
       |  `refund_amount` decimal(10,2) COMMENT '实际退款金额，（有可能退款金额小于实际支付金额）',
       |  `refund_type` string COMMENT '退款方式',
       |  `refund_content` string COMMENT '退款备注',
       |  `refund_time` string COMMENT '退款时间',
       |  `confirm_time` string COMMENT '用户确认收货时间',
       |  `comments` smallint COMMENT '待评价订单商品数量',
       |  `end_time` string COMMENT '订单关闭时间',
       |  `add_time` string COMMENT '创建时间',
       |  `update_time` string COMMENT '更新时间',
       |  `deleted` tinyint COMMENT '逻辑删除',
       |  `province` int COMMENT '省份ID',
       |  `city` int COMMENT '城市ID',
       |  `country` int COMMENT '乡镇ID',
       |  PRIMARY KEY(id) NOT ENFORCED
       |)comment '订单表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_ORDER}'
       |)
    """.stripMargin)

  // 订单详情
  tableEnv.executeSql(
    s"""
       |create table ods_order_goods(
       |  `id` int,
       |  `order_id` int COMMENT '订单表的订单ID',
       |  `goods_id` int COMMENT '商品表的商品ID',
       |  `goods_name` string COMMENT '商品名称',
       |  `goods_sn` string COMMENT '商品编号',
       |  `product_id` int COMMENT '商品货品表的货品ID',
       |  `number` smallint COMMENT '商品货品的购买数量',
       |  `price` decimal(10,2)  COMMENT '商品货品的售价',
       |  `specifications` string COMMENT '商品货品的规格列表',
       |  `pic_url` string COMMENT '商品货品图片或者商品图片',
       |  `comment` int COMMENT '订单商品评论，如果是-1，则超期不能评价；如果是0，则可以评价；如果其他值，则是comment表里面的评论ID。',
       |  `add_time` string COMMENT '创建时间',
       |  `update_time` string COMMENT '更新时间',
       |  `deleted` tinyint COMMENT '逻辑删除',
       |  PRIMARY KEY(id) NOT ENFORCED
       |)comment '订单商品表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_ORDER_GOODS}'
       |)
    """.stripMargin)

  // 订单详情事实表
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
       |	`add_time` string COMMENT '创建时间',
       |	`user_id` int comment '用户id',
       | `province` int COMMENT '省份ID',
       | `city` int COMMENT '城市ID',
       | `country` int COMMENT '乡镇ID',
       | PRIMARY KEY(id) NOT ENFORCED
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

  // 处理数据
  tableEnv.executeSql(
    """
      |INSERT INTO dwd_fact_order_goods_info
      |select
      |    oog.id,
      |    oog.order_id,
      |    oog.goods_id,
      |    oog.goods_name,
      |    oog.goods_sn,
      |    oog.product_id,
      |    oog.number,
      |    oog.price,
      |    oog.add_time,
      |    oo.user_id,
      |    oo.province,
      |    oo.city,
      |    oo.country
      |from ods_order_goods oog
      |join ods_order oo
      |on oog.order_id=oo.id
    """.stripMargin)
}
