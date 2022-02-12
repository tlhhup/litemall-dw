package org.tlh.warehouse.table.dws

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-02-10
  */
object RegionItemTopic extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)
  val tableEnv = StreamTableEnvironment.create(env)

  // 支付宽表
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
       |  `province_name` string comment '省份名称',
       |  `city` int COMMENT '城市ID',
       |  `city_name` string comment '城市名称',
       |  `country` int COMMENT '乡镇ID',
       |  `country_name` string comment '乡镇名称',
       |  WATERMARK FOR pay_time AS pay_time - INTERVAL '5' SECOND
       |)comment '订单支付事实表'
       |WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_PAYMENT}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_payment',
       |  'format' = 'json'
       |)
    """.stripMargin)

  // 退款宽表
  tableEnv.executeSql(
    s"""
       |create table dws_order_refund_wide(
       |	`order_id` int comment '订单ID',
       |	`user_id` int COMMENT '用户表的用户ID',
       |	`order_sn` string COMMENT '订单编号',
       |	`refund_amount` decimal(10,2) COMMENT '实际退款金额，（有可能退款金额小于实际支付金额）',
       |	`refund_type` string COMMENT '退款方式',
       |	`refund_content` string COMMENT '退款备注',
       |	`refund_time` timestamp(3) COMMENT '申请退款时间',
       |	`confirm_time` timestamp(3) COMMENT '确认退款时间',
       |	`province` int COMMENT '省份ID',
       |  `province_name` string comment '省份名称',
       |  `city` int COMMENT '城市ID',
       |  `city_name` string comment '城市名称',
       |  `country` int COMMENT '乡镇ID',
       |  `country_name` string comment '乡镇名称',
       |  WATERMARK FOR refund_time AS refund_time - INTERVAL '5' SECOND
       |)comment '订单退款事实表'
       |WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWS_DB_REFUND}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'dwd_db_refund',
       |  'format' = 'json'
       |)
    """.stripMargin)

  /**
    * 省 市 乡镇 支付数 支付金额 支付用户数 退款数 退款金额 退款用户数
    */
  tableEnv.executeSql(
    """
      |select
      | TUMBLE_START(add_time, INTERVAL '10' SECOND),
      | TUMBLE_END(add_time, INTERVAL '10' SECOND),
      | province,
      | city,
      | country,
      | sum(pay_count) as pay_total,
      | sum(pay_price) as pay_amount,
      | count(distinct if(pay_user>0,pay_user,cast(null as int))) as pay_user_count,
      | sum(refund_count) as refund_total,
      | sum(refund_amount) as refund_amount,
      | count(distinct if(refund_user>0,refund_user,cast(null as int))) as refund_user_count
      |from
      |(
      | select
      |   province,
      |   city,
      |   country,
      |   1 as pay_count,
      |   pay_price,
      |   user_id as pay_user,
      |   0 as refund_count,
      |   0 as refund_amount,
      |   0 as refund_user,
      |   pay_time as add_time
      | from dws_order_payment_wide
      |
      | union all
      |
      | select
      |   province,
      |   city,
      |   country,
      |   0 as pay_count,
      |   0 as pay_price,
      |   0 as pay_user,
      |   1 as refund_count,
      |   refund_amount,
      |   user_id as refund_user,
      |   refund_time as add_time
      | from dws_order_refund_wide
      |) tmp
      |group by
      | TUMBLE(add_time, INTERVAL '10' SECOND),
      | province,city,country
    """.stripMargin)
    .print()

}
