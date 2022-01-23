package org.tlh.warehouse.ods.log

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
  tableEnv.createTemporaryFunction("get_json_object", classOf[ParseJsonObject])
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
      |    get_json_object(line,'mid'),
      |    CAST(get_json_object(line,'uid') as int),
      |    get_json_object(line,'g'),
      |    get_json_object(line,'vc'),
      |    get_json_object(line,'vn'),
      |    get_json_object(line,'l'),
      |    get_json_object(line,'sr'),
      |    get_json_object(line,'os'),
      |    get_json_object(line,'ar'),
      |    get_json_object(line,'md'),
      |    get_json_object(line,'ba'),
      |    get_json_object(line,'sv'),
      |    get_json_object(line,'hw'),
      |    get_json_object(line,'t'),
      |    get_json_object(line,'action'),
      |    get_json_object(line,'loadingTime'),
      |    get_json_object(line,'detail'),
      |    get_json_object(line,'extend1')
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

  tableEnv.executeSql(
    """
      |select * from dwd_event_base_log where event_type='ad'
    """.stripMargin)
    .print()

}
