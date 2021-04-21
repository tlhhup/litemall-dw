package org.tlh.spark.sql.hbase

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-20
  */
object ReadConfig {

  val FILED_SEPARATOR = ","

  // 设置的参数
  val ZK_HOSTS = "zkHosts"
  val ZK_PORT = "zkPort"
  val HBASE_TABLE = "hBaseTable"
  val HBASE_FAMILY = "family"
  val SELECT_FIELDS = "selectFields"
  val ROW_KEY = "rowKey"
  val WHERE_FIELD_NAMES = "whereFieldNames"


  // hBase 配置信息
  val HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum"
  val HBASE_ZOOKEEPER_QUORUM_default = "localhost"
  val HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT = "hbase.zookeeper.property.clientPort"
  val HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT_default = "2181"
  val INPUT_TABLE = "hbase.mapreduce.inputtable"
  val SCAN = "hbase.mapreduce.scan"
  val OUTPUT_TABLE = "hbase.mapred.outputtable"

}
