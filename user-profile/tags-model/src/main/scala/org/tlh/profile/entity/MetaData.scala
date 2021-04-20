package org.tlh.profile.entity

import org.tlh.profile.enums.MetaDataTypeEnum.MetaDataTypeEnum

/**
  * 标签元数据
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-09
  */
case class MetaData(in_type: Int,
                    driver: String,
                    url: String,
                    user: String,
                    password: String,
                    db_table: String,
                    query_sql: String,
                    in_path: String,
                    sperator: String,
                    out_path: String,
                    zk_hosts: String,
                    zk_port: Int,
                    hbase_namespace: String,
                    hbase_table: String,
                    row_key: String,
                    family: String,
                    select_field_names: String,
                    where_field_names: String,
                    where_field_values: String,
                    out_fields: String
                   ) {

  def metaDataType(): MetaDataTypeEnum = {
    import org.tlh.profile.enums.MetaDataTypeEnum._

    this.in_type match {
      case 1 => RDBMS
      case 2 => HDFS
      case 3 => HBASE
      case 4 => HIVE
    }
  }

  def toRDBMSMeta(): RDBMSMetaData = {
    val commonMeta = CommonMeta(select_field_names, where_field_names, where_field_values, out_fields)
    RDBMSMetaData(commonMeta, driver, url, user, password, db_table, query_sql)
  }

  def toHDFSMeta(): HDFSMetaData = {
    val commonMeta = CommonMeta(select_field_names, where_field_names, where_field_values, out_fields)
    HDFSMetaData(commonMeta, in_path, sperator, out_path)
  }

  def toHBaseMeta(): HBASEMetaData = {
    val commonMeta = CommonMeta(select_field_names, where_field_names, where_field_values, out_fields)
    HBASEMetaData(commonMeta, zk_hosts, zk_port, hbase_namespace, hbase_table, row_key, family)
  }

  def toHiveMeta(): HiveMetaData = {
    val commonMeta = CommonMeta(select_field_names, where_field_names, where_field_values, out_fields)
    HiveMetaData(commonMeta, db_table)
  }

}


case class CommonMeta(selectFieldNames: String,
                      whereFieldNames: String,
                      whereFieldValues: String,
                      outFields: String)

case class RDBMSMetaData(commonMeta: CommonMeta,
                         driver: String,
                         url: String,
                         user: String,
                         password: String,
                         dbTable: String,
                         querySql: String)

case class HDFSMetaData(commonMeta: CommonMeta,
                        inPath: String,
                        separator: String,
                        outPath: String)

case class HBASEMetaData(commonMeta: CommonMeta,
                         zkHosts: String,
                         zkPort: Int,
                         namespace: String,
                         table: String,
                         rowKey: String,
                         family: String)

case class HiveMetaData(commonMeta: CommonMeta,
                        dbTable: String)