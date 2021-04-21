package org.tlh.spark.sql.hbase

import java.util.Base64

import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.hbase.{CompareOperator, HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ColumnFamilyDescriptorBuilder, Connection, ConnectionFactory, Put, Result, Scan, TableDescriptorBuilder}
import org.apache.hadoop.hbase.filter.{FilterList, SingleColumnValueFilter}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.sources.{BaseRelation, InsertableRelation, TableScan}
import org.apache.spark.sql.types.StructType
import org.tlh.spark.sql.hbase.ReadConfig._
import org.tlh.spark.sql.hbase.filter.DateFilter

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-20
  */
class HBaseRelation(sc: SQLContext,
                    userSchema: StructType,
                    params: Map[String, String]
                   ) extends BaseRelation with TableScan with InsertableRelation {

  override def sqlContext: SQLContext = sc

  // DF的数据结构  df=schema+RDD[Row]
  override def schema: StructType = userSchema

  /**
    * 去读表中的数据，将查询转换为hBase的scan操作
    *
    * @return
    */
  override def buildScan(): RDD[Row] = {
    //1. 读取配置信息
    val quorum = params.getOrElse(ZK_HOSTS, HBASE_ZOOKEEPER_QUORUM_default)
    val port = params.getOrElse(ZK_PORT, HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT_default)
    val table = params(HBASE_TABLE)
    val cf = params(HBASE_FAMILY)
    val fields = params(SELECT_FIELDS)

    //2. 构建hBase的基础配置信息
    val conf = HBaseConfiguration.create()
    conf.set(HBASE_ZOOKEEPER_QUORUM, quorum)
    conf.set(HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT, port)

    //3. 设置查询scan对象
    //3.1 构建scan，查询的列
    val scan = new Scan()
    val cfBytes = Bytes.toBytes(cf)
    fields.split(FILED_SEPARATOR).foreach(filed => {
      scan.addColumn(cfBytes, Bytes.toBytes(filed))
    })

    //3.2 构建时间过滤 字段#单位(day,month,year)#duration
    val filterCondition = params(WHERE_FIELD_NAMES)
    if (StringUtils.isNotBlank(filterCondition)) {
      val filters = new FilterList()

      filterCondition.split(FILED_SEPARATOR).foreach(condition => {
        val dateFilter = DateFilter(condition)

        //3.2.1 设置过滤器
        // 起始值
        val startFilter = new SingleColumnValueFilter(cfBytes,
          Bytes.toBytes(dateFilter.filed),
          CompareOperator.GREATER,
          Bytes.toBytes(dateFilter.start()))
        filters.addFilter(startFilter)
        // 终止值
        val endFilter = new SingleColumnValueFilter(cfBytes,
          Bytes.toBytes(dateFilter.filed),
          CompareOperator.LESS,
          Bytes.toBytes(dateFilter.end()))
        filters.addFilter(endFilter)

        //3.2.1 设置过滤的列(必须添加)
        scan.addColumn(cfBytes, Bytes.toBytes(dateFilter.filed))
      })

      scan.setFilter(filters)
    }

    //3.3 设置到conf中
    conf.set(INPUT_TABLE, table)
    conf.set(SCAN, Bytes.toString(Base64.getEncoder.encode(ProtobufUtil.toScan(scan).toByteArray)))

    //4. 查询数据
    val rdd = sc.sparkContext
      .newAPIHadoopRDD(
        conf,
        classOf[TableInputFormat],
        classOf[ImmutableBytesWritable],
        classOf[Result]
      )

    //5. 将rdd转换为RDD[Row]
    val rows = rdd.map {
      case (_, result) => {
        val values = fields.split(FILED_SEPARATOR).map(field => {
          val bytes = result.getValue(cfBytes, Bytes.toBytes(field))
          Bytes.toString(bytes)
        })

        Row.fromSeq(values)
      }
    }

    rows
  }

  /**
    * 保存数据，将df转换为put操作
    *
    * @param data
    * @param overwrite
    */
  override def insert(data: DataFrame, overwrite: Boolean): Unit = {
    //1. 读取配置信息
    val quorum = params.getOrElse(ZK_HOSTS, HBASE_ZOOKEEPER_QUORUM_default)
    val port = params.getOrElse(ZK_PORT, HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT_default)
    val table = params(HBASE_TABLE)
    val cf = params(HBASE_FAMILY)
    val rowKeyColumn = params(ROW_KEY)

    //2. 构建hBase的基础配置信息
    val conf = HBaseConfiguration.create()
    conf.set(HBASE_ZOOKEEPER_QUORUM, quorum)
    conf.set(HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT, port)
    // 设置输出的表
    conf.set(OUTPUT_TABLE, table)

    //3. 检查表是否存在
    var connection: Connection = null
    try {
      connection = ConnectionFactory.createConnection(conf)
      val admin = connection.getAdmin
      val tableName = TableName.valueOf(table)
      if (!admin.tableExists(tableName)) {
        val tableDescriptor = TableDescriptorBuilder.newBuilder(tableName)
          .setColumnFamily(ColumnFamilyDescriptorBuilder.of(cf))
          .build()
        admin.createTable(tableDescriptor)
      }
    } finally {
      if (connection != null) {
        connection.close()
      }
    }

    //4. 将数据转换为put操作
    val cfBytes = Bytes.toBytes(cf)
    val columns = data.columns
    val rdd = data.rdd.map(row => {
      //设置rowkey
      val rowKey = row.getAs[String](rowKeyColumn)
      val rowKeyBytes = Bytes.toBytes(rowKey)
      val put = new Put(rowKeyBytes)
      //设置列
      columns.foreach(field => {
        val value = row.getAs[String](field)
        put.addColumn(cfBytes, Bytes.toBytes(field), Bytes.toBytes(value))
      })

      (new ImmutableBytesWritable(rowKeyBytes), put)
    })

    //5. 写出数据
    rdd.saveAsNewAPIHadoopFile(
      s"datas/hbase/output-${System.nanoTime()}", //设置job的输出路径
      classOf[ImmutableBytesWritable],
      classOf[Put],
      classOf[TableOutputFormat[ImmutableBytesWritable]],
      conf
    )
  }
}
