package org.tlh.warehouse.table.ods.db

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-25
  */
object OdsOrderApp extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.enableCheckpointing(3 * 1000)
  env.setParallelism(3)

  val tableEnv = StreamTableEnvironment.create(env)

  // 定义订单数据源
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
       |  `pay_time` timestamp(3) COMMENT '微信付款时间',
       |  `ship_sn` string COMMENT '发货编号',
       |  `ship_channel` string COMMENT '发货快递公司',
       |  `ship_time` timestamp(3) COMMENT '发货开始时间',
       |  `refund_amount` decimal(10,2) COMMENT '实际退款金额，（有可能退款金额小于实际支付金额）',
       |  `refund_type` string COMMENT '退款方式',
       |  `refund_content` string COMMENT '退款备注',
       |  `refund_time` timestamp(3) COMMENT '退款时间',
       |  `confirm_time` timestamp(3) COMMENT '用户确认收货时间',
       |  `comments` smallint COMMENT '待评价订单商品数量',
       |  `end_time` timestamp(3) COMMENT '订单关闭时间',
       |  `add_time` timestamp(3) COMMENT '创建时间',
       |  `update_time` timestamp(3) COMMENT '更新时间',
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
       |	`country` int COMMENT '乡镇ID',
       |  PRIMARY KEY (order_id) NOT ENFORCED
       |)comment '订单支付事实表'
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_PAYMENT}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_payment',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  // 定义退款表
  tableEnv.executeSql(
    s"""
       |create table dwd_fact_refund_info(
       |	`order_id` int comment '订单ID',
       |	`user_id` int COMMENT '用户表的用户ID',
       |	`order_sn` string COMMENT '订单编号',
       |	`refund_amount` decimal(10,2) COMMENT '实际退款金额，（有可能退款金额小于实际支付金额）',
       |	`refund_type` string COMMENT '退款方式',
       |	`refund_content` string COMMENT '退款备注',
       |	`refund_time` timestamp(3) COMMENT '申请退款时间',
       |	`confirm_time` timestamp(3) COMMENT '确认退款时间',
       |	`province` int COMMENT '省份ID',
       |	`city` int COMMENT '城市ID',
       |	`country` int COMMENT '乡镇ID',
       |  PRIMARY KEY (order_id) NOT ENFORCED
       |)comment '订单退款事实表'
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_REFUND}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_refund',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  // 处理支付事务性事实表
  tableEnv.executeSql(
    s"""
       |INSERT INTO dwd_fact_payment_info
       |select
       |    id,
       |    user_id,
       |    order_sn,
       |    actual_price,
       |    pay_id,
       |    pay_time,
       |    add_time,
       |    province,
       |    city,
       |    country
       |from ods_order
       |where order_status=201
    """.stripMargin)

  // 处理退款事务性事实表
  tableEnv.executeSql(
    """
      |INSERT INTO dwd_fact_refund_info
      |select
      |    id,
      |    user_id,
      |    order_sn,
      |    refund_amount,
      |    refund_type,
      |    refund_content,
      |    refund_time,
      |    confirm_time,
      |    province,
      |    city,
      |    country
      |from ods_order
      |where order_status=203
    """.stripMargin)

  // 定义订单表
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
       |	`country` int COMMENT '乡镇ID',
       |   PRIMARY KEY (id) NOT ENFORCED
       |)comment '订单事实表'
       |WITH (
       |  'connector' = 'upsert-kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_DB_ORDER}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_order',
       |  'key.format' = 'json',
       |  'value.format' = 'json'
       |)
    """.stripMargin)

  // 处理团购数据
  tableEnv.executeSql(
    s"""
       |create table ods_groupon(
       |  `id` int,
       |  `order_id` int COMMENT '关联的订单ID',
       |  `groupon_id` int COMMENT '如果是开团用户，则groupon_id是0；如果是参团用户，则groupon_id是团购活动ID',
       |  `rules_id` int COMMENT '团购规则ID，关联litemall_groupon_rules表ID字段',
       |  `user_id` int COMMENT '用户ID',
       |  `share_url` string COMMENT '团购分享图片地址',
       |  `creator_user_id` int COMMENT '开团用户ID',
       |  `creator_user_time` timestamp(3) COMMENT '开团时间',
       |  `status` smallint COMMENT '团购活动状态，开团未支付则0，开团中则1，开团失败则2',
       |  `add_time` timestamp(3) COMMENT '创建时间',
       |  `update_time` timestamp(3) COMMENT '更新时间',
       |  `deleted` tinyint COMMENT '逻辑删除',
       |  PRIMARY KEY (id) NOT ENFORCED
       |)comment '团购活动表'
       |WITH (
       | 'connector' = 'mysql-cdc',
       | 'hostname' = '${AppConfig.MYSQL_HOST}',
       | 'port' = '${AppConfig.MYSQL_PORT}',
       | 'username' = '${AppConfig.MYSQL_USERNAME}',
       | 'password' = '${AppConfig.MYSQL_PASSWORD}',
       | 'database-name' = '${AppConfig.MYSQL_CDC_DB}',
       | 'table-name' = '${AppConfig.MYSQL_CDC_ODS_GROUPON}'
       |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_fact_order_info
      |select
      |    oo.id,
      |    oo.user_id,
      |    oo.order_sn,
      |    oo.order_status,
      |    oo.goods_price,
      |    oo.freight_price,
      |    oo.coupon_price,
      |    oo.integral_price,
      |    og.id as groupon_id,
      |    oo.groupon_price,
      |    oo.order_price,
      |    oo.actual_price,
      |    oo.add_time,
      |    oo.province,
      |    oo.city,
      |    oo.country
      |from ods_order oo
      |left join ods_groupon og
      |on og.order_id=oo.id
      |where oo.order_status=101
    """.stripMargin)

}
