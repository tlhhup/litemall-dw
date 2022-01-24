package org.tlh.warehouse.table.ods.log

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.tlh.warehouse.util.AppConfig

/**
  * @author 离歌笑
  * @desc
  * @date 2022-01-23
  */
object FrontLog extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(3)

  val tableEnv = StreamTableEnvironment.create(env)

  // 注册表
  tableEnv.executeSql(
    s"""
       |CREATE TABLE frontLog (
       |  `line` String
       |) WITH (
       |  'connector' = 'kafka',
       |  'topic' = '${AppConfig.KAFKA_INPUT_FRONT_TOPIC}',
       |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
       |  'properties.group.id' = 'frontLog',
       |  'scan.startup.mode' = 'earliest-offset',
       |  'format' = 'raw'
       |)
    """.stripMargin)

  // 注册函数
  tableEnv.createTemporaryFunction("isStartLog", classOf[IsStartLog])
  tableEnv.createTemporaryFunction("isEventLog", classOf[IsEventLog])
  tableEnv.createTemporaryFunction("get_json_object", classOf[GetJsonObject])
  tableEnv.createTemporaryFunction("parse_json_object", classOf[ParseEventJsonObject])
  tableEnv.createTemporaryFunction("extract_event_type", classOf[ExtractEventType])

  // 启动日志
  tableEnv.executeSql(
    s"""
      |create table dwd_start_log(
      |	`mid` string comment '设备id',
      |	`uid` int comment '用户id',
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
      |	`action` string comment '状态',
      |	`loadingTime` string comment '加载时长',
      |	`detail` string comment '失败码',
      |	`extend1` string comment '失败的message'
      |) comment '启动日志表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_ODS_LOG_START}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'start_log',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dwd_start_log
      |select
      |    get_json_object(line,'$.mid'),
      |    CAST(get_json_object(line,'$.uid') as int),
      |    get_json_object(line,'$.g'),
      |    get_json_object(line,'$.vc'),
      |    get_json_object(line,'$.vn'),
      |    get_json_object(line,'$.l'),
      |    get_json_object(line,'$.sr'),
      |    get_json_object(line,'$.os'),
      |    get_json_object(line,'$.ar'),
      |    get_json_object(line,'$.md'),
      |    get_json_object(line,'$.ba'),
      |    get_json_object(line,'$.sv'),
      |    get_json_object(line,'$.hw'),
      |    get_json_object(line,'$.t'),
      |    get_json_object(line,'$.action'),
      |    get_json_object(line,'$.loadingTime'),
      |    get_json_object(line,'$.detail'),
      |    get_json_object(line,'$.extend1')
      |from frontLog
      |where isStartLog(line)
    """.stripMargin)

  // 处理事件日志
  // 定义基础事件表
  tableEnv.executeSql(
    s"""
      |create table dwd_event_base_log(
      |  `mid` string comment '设备id',
      |  `uid` string comment '用户id',
      |  `mail` string comment '邮箱',
      |  `version_code` string comment '程序版本号',
      |  `version_name` string comment '程序版本名',
      |  `language` string comment '系统语言',
      |  `source` string comment '应用从那个渠道来的',
      |  `os` string comment '系统版本',
      |  `area` string comment '区域',
      |  `model` string comment '手机型号',
      |  `brand` string comment '手机品牌',
      |  `sdk_version` string,
      |  `hw` string comment '屏幕宽高',
      |  `app_time` string comment '时间戳',
      |  `event_type` string comment '事件类型',
      |  `event_json` string comment '事件原始数据'
      |)comment '事件日志基础表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_ODS_LOG_EVENT}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'event_log',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |insert into dwd_event_base_log
      |select
      |    parse_json_object(line,'mid'),
      |    parse_json_object(line,'uid'),
      |    parse_json_object(line,'g'),
      |    parse_json_object(line,'vc'),
      |    parse_json_object(line,'vn'),
      |    parse_json_object(line,'l'),
      |    parse_json_object(line,'sr'),
      |    parse_json_object(line,'os'),
      |    parse_json_object(line,'ar'),
      |    parse_json_object(line,'md'),
      |    parse_json_object(line,'ba'),
      |    parse_json_object(line,'sv'),
      |    parse_json_object(line,'hw'),
      |    parse_json_object(line,'st'),
      |    event_type,
      |    event_json
      |from frontLog, LATERAL TABLE(extract_event_type(parse_json_object(line,'et')))
      |where isEventLog(line)
    """.stripMargin)

  // 广告
  tableEnv.executeSql(
    s"""
      |create table dwd_event_ad_log(
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
      |	`activity_id` string comment '活动id',
      |	`display_mills` string comment '展示时长',
      |	`entry` string comment '入口',
      |	`item_id` string,
      |	`action` string,
      |	`content_type` string
      |)comment '广告事件表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_LOG_AD}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'event_ad',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_event_ad_log
      |select
      |    mid,
      |    uid,
      |    mail,
      |    version_code,
      |    version_name,
      |    `language`,
      |    source,
      |    os,
      |    area,
      |    model,
      |    brand,
      |    sdk_version,
      |    hw,
      |    get_json_object(event_json,'$.ett'),
      |    get_json_object(event_json,'$.kv.activityId'),
      |    get_json_object(event_json,'$.kv.displayMills'),
      |    get_json_object(event_json,'$.kv.entry'),
      |    get_json_object(event_json,'$.kv.itemId'),
      |    get_json_object(event_json,'$.kv.action'),
      |    get_json_object(event_json,'$.kv.contentType')
      |from dwd_event_base_log
      |where event_type='ad'
    """.stripMargin)

  // 添加购物车
  tableEnv.executeSql(
    s"""
      |create table dwd_event_addCar_log(
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
      |	`add_time` string comment '加入购物车时间',
      |	`goods_id` int comment 'spu id',
      |	`sku_id` int comment 'sku id',
      |	`num` int comment '商品数量',
      |	`user_id` int comment '用户id'
      |)comment '加购事件表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_LOG_ADD_CAR}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'event_add_car',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_event_addCar_log
      |select
      |    mid,
      |    uid,
      |    mail,
      |    version_code,
      |    version_name,
      |    `language`,
      |    source,
      |    os,
      |    area,
      |    model,
      |    brand,
      |    sdk_version,
      |    hw,
      |    app_time,
      |    get_json_object(event_json,'$.kv.addTime'),
      |    cast(get_json_object(event_json,'$.kv.goodsId') as int),
      |    cast(get_json_object(event_json,'$.kv.skuId') as int),
      |    cast(get_json_object(event_json,'$.kv.num') as int),
      |    cast(get_json_object(event_json,'$.kv.userId') as int)
      |from dwd_event_base_log
      |where event_type='addCar'
    """.stripMargin)

  // 评论
  tableEnv.executeSql(
    s"""
      |create table dwd_event_comment_log(
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
      |	`value_id` int comment '数据id',
      |	`add_time` string comment '评论时间',
      |	`star` int comment '评分',
      |	`type` int comment '数据类型 1 主题 0 商品',
      |	`user_id` int comment '用户id',
      |	`content` string comment '评论内容'
      |)comment '评论事件表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_LOG_COMMENT}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'event_comment',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_event_comment_log
      |select
      |    mid,
      |    uid,
      |    mail,
      |    version_code,
      |    version_name,
      |    `language`,
      |    source,
      |    os,
      |    area,
      |    model,
      |    brand,
      |    sdk_version,
      |    hw,
      |    app_time,
      |    cast(get_json_object(event_json,'$.kv.valueId') as INT),
      |    get_json_object(event_json,'$.kv.addTime'),
      |    cast(get_json_object(event_json,'$.kv.star') as INT),
      |    cast(get_json_object(event_json,'$.kv.type') as int),
      |    cast(get_json_object(event_json,'$.kv.userId') as int),
      |    get_json_object(event_json,'$.kv.content')
      |from dwd_event_base_log
      |where event_type='comment'
    """.stripMargin)

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

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_event_display_log
      |select
      |    mid,
      |    uid,
      |    mail,
      |    version_code,
      |    version_name,
      |    `language`,
      |    source,
      |    os,
      |    area,
      |    model,
      |    brand,
      |    sdk_version,
      |    hw,
      |    app_time,
      |    CAST(get_json_object(event_json,'$.kv.goodsId') as INT),
      |    get_json_object(event_json,'$.kv.action'),
      |    get_json_object(event_json,'$.kv.extend1'),
      |    get_json_object(event_json,'$.kv.place'),
      |    get_json_object(event_json,'$.kv.category')
      |from dwd_event_base_log
      |where event_type='display'
    """.stripMargin)

  // 收藏
  tableEnv.executeSql(
    s"""
      |create table dwd_event_favorites_log(
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
      |	`add_time` string comment '收藏时间',
      |	`goods_id` int comment '商品id',
      |	`user_id` int comment '用户id'
      |)comment '收藏事件表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_LOG_FAVORITES}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'event_favorites',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_event_favorites_log
      |    select
      |    mid,
      |    uid,
      |    mail,
      |    version_code,
      |    version_name,
      |    `language`,
      |    source,
      |    os,
      |    area,
      |    model,
      |    brand,
      |    sdk_version,
      |    hw,
      |    app_time,
      |    get_json_object(event_json,'$.kv.addTime'),
      |    cast(get_json_object(event_json,'$.kv.courseId') as int),
      |    cast(get_json_object(event_json,'$.kv.userId') as int)
      |from dwd_event_base_log
      |where event_type='favorites'
    """.stripMargin)

  // 加载
  tableEnv.executeSql(
    s"""
      |create table dwd_event_loading_log(
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
      |	`loading_time` string comment '耗时',
      |	`extend2` string,
      |	`loadingWay` string,
      |	`action` string,
      |	`extend1` string,
      |	`type` string,
      |	`type1` string
      |)comment '加载事件表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_LOG_LOADING}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'event_loading',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_event_loading_log
      |    select
      |    mid,
      |    uid,
      |    mail,
      |    version_code,
      |    version_name,
      |    `language`,
      |    source,
      |    os,
      |    area,
      |    model,
      |    brand,
      |    sdk_version,
      |    hw,
      |    app_time,
      |    get_json_object(event_json,'$.kv.loadingTime'),
      |    get_json_object(event_json,'$.kv.extend2'),
      |    get_json_object(event_json,'$.kv.loadingWay'),
      |    get_json_object(event_json,'$.kv.action'),
      |    get_json_object(event_json,'$.kv.extend1'),
      |    get_json_object(event_json,'$.kv.type'),
      |    get_json_object(event_json,'$.kv.type1')
      |from dwd_event_base_log
      |where event_type='loading'
    """.stripMargin)

  // 点赞
  tableEnv.executeSql(
    s"""
      |create table dwd_event_praise_log(
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
      |	`add_time` string comment '点赞时间',
      |	`target_id` int comment '数据id',
      |	`type` int comment '数据类型',
      |	`user_id` int comment '用户id'
      |)comment '点赞事件表'
      |WITH (
      |  'connector' = 'kafka',
      |  'topic' = '${AppConfig.KAFKA_OUTPUT_DWD_LOG_PRAISE}',
      |  'properties.bootstrap.servers' = '${AppConfig.KAFKA_SERVERS}',
      |  'properties.group.id' = 'event_praise',
      |  'format' = 'json'
      |)
    """.stripMargin)

  tableEnv.executeSql(
    """
      |INSERT INTO dwd_event_praise_log
      |select
      |    mid,
      |    uid,
      |    mail,
      |    version_code,
      |    version_name,
      |    `language`,
      |    source,
      |    os,
      |    area,
      |    model,
      |    brand,
      |    sdk_version,
      |    hw,
      |    app_time,
      |    get_json_object(event_json,'$.kv.addTime'),
      |    cast(get_json_object(event_json,'$.kv.targetId') as int),
      |    cast(get_json_object(event_json,'$.kv.type') as int),
      |    CAST(get_json_object(event_json,'$.kv.userId') as INT)
      |from dwd_event_base_log
      |where event_type='praise'
    """.stripMargin)

}
