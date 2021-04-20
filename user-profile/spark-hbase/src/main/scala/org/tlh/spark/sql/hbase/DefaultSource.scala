package org.tlh.spark.sql.hbase

import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider, DataSourceRegister, RelationProvider}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

/**
  * 数据源注册，提供给spark采用SPI加载 <br>
  *
  * 1. 提供读取的Relation <br>
  * 2. 注册format别名
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-20
  */
class DefaultSource extends DataSourceRegister with RelationProvider with CreatableRelationProvider {

  override def shortName(): String = "hbase"

  /**
    * 提供查询数据的relation
    *
    * @param sqlContext
    * @param parameters
    * @return
    */
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    //1. 获取查询的列 构建schema
    val schema = parameters.get(ReadConfig.SELECT_FIELDS) match {
      case Some(x) => {
        StructType(
          x.split(ReadConfig.FILED_SEPARATOR).map(fieldName => StructField(fieldName, StringType, nullable = true))
        )
      }
      case None => throw new IllegalArgumentException("selectFields must not been none!")
    }
    //2. 构建查询的Relation
    new HBaseRelation(sqlContext, schema, parameters)
  }

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    //1. 构建添加的Relation
    val hBaseRelation = new HBaseRelation(sqlContext, data.schema, parameters)
    //2. 保存数据
    hBaseRelation.insert(data, true)

    hBaseRelation
  }
}
