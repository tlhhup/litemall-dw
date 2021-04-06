package org.tlh.driver

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.hbase.client.{Admin, ClusterConnection, ColumnFamilyDescriptorBuilder, ConnectionFactory, HRegionLocator, TableDescriptorBuilder}
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue, NamespaceDescriptor, NamespaceNotFoundException, TableName}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{HFileOutputFormat2, TableOutputFormat}
import org.apache.hadoop.hbase.tool.LoadIncrementalHFiles
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.sql.SparkSession

/**
  * 将hive中的数据转换成HFile,并使用bulkLoad 加载到hbase中
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-04
  */
object HBaseBulkLoad {

  val config = ConfigFactory.load()
  private val namaspace: String = config.getString("namaspace")
  private val family: String = config.getString("family")
  private val hFileDir: String = config.getString("hfileDir")

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      throw new IllegalArgumentException("请输入，sourceDB sourceTable filedId")
    }
    // 解析参数
    val sourceDB = args(0)
    val sourceTable = args(1)
    val filedId = args(2)

    val conf = HBaseConfiguration.create
    conf.set(TableOutputFormat.OUTPUT_TABLE, sourceTable)
    conf.set("hbase.mapreduce.hfileoutputformat.table.name", sourceTable)

    val job = Job.getInstance(conf)
    job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setMapOutputValueClass(classOf[KeyValue])

    val hFilePath = s"$hFileDir/$sourceTable"
    // 写出HFile
    hive2HFile(sourceDB, sourceTable, filedId, conf, hFilePath)

    // 使用bulkLoad加载HFile到HBase中
    bulkLoad(job, hFilePath, sourceTable)
  }

  implicit val ord = new Ordering[(ImmutableBytesWritable, Array[Byte])] {
    override def compare(x: (ImmutableBytesWritable, Array[Byte]), y: (ImmutableBytesWritable, Array[Byte])): Int = {
      if (x._1.compareTo(y._1) == 0) {
        Bytes.compareTo(x._2, y._2)
      } else {
        x._1.compareTo(y._1)
      }
    }
  }

  /**
    * 将hive中的数据转存为HFile
    *
    * @param sourceDB
    * @param sourceTable
    * @param filedId
    * @param hadoopConfig
    * @param hFilePath
    * @return HFile存储路径
    */
  def hive2HFile(sourceDB: String, sourceTable: String, filedId: String, hadoopConfig: Configuration, hFilePath: String): Unit = {
    // 导出的hfile 需要保证 rowkey+family+Qualifier是有序的-->hbase中存储的数据是有序的
    val fs = FileSystem.get(hadoopConfig)
    val hFile = new Path(hFilePath)
    if (fs.exists(hFile)) {
      fs.delete(hFile, true)
    }

    // 创建spark对象
    val spark = SparkSession.builder()
      .appName("HBaseBulkLoad")
      .enableHiveSupport()
      .getOrCreate()

    spark.read
      .table(s"$sourceDB.$sourceTable")
      .rdd
      .filter(row => row.getAs(filedId) != null)
      .flatMap(row => {
        // rowKey
        val id = row.getAs(filedId).toString
        val rowKey = Bytes.toBytes(id)
        val cf = Bytes.toBytes(family)

        // 处理列
        row.schema
          .sortBy(field => field.name)
          .map(field => {
            // 获取列的值
            val fieldName = Bytes.toBytes(field.name)
            val fieldValue = if (row.getAs(field.name) != null) {
              row.getAs(field.name).toString
            } else {
              ""
            }
            val qualifierBytes = Bytes.toBytes(fieldValue)
            //创建 KV
            val kv = new KeyValue(rowKey, cf, fieldName, qualifierBytes)

            val qualifier = new Array[Byte](kv.getQualifierLength)
            System.arraycopy(kv.getQualifierArray, kv.getQualifierOffset, qualifier, 0, kv.getQualifierLength)
            ((new ImmutableBytesWritable(rowKey), qualifier), kv)
          })
      })
      .filter(item => item != null)
      .sortBy(x => x._1, true)
      .map(item => (item._1._1, item._2))
      .saveAsNewAPIHadoopFile(
        hFilePath,
        classOf[ImmutableBytesWritable],
        classOf[KeyValue],
        classOf[HFileOutputFormat2],
        hadoopConfig
      )

    spark.close()
  }

  /**
    * 加载HFile
    *
    * @param job
    * @param hFilePath
    * @param name
    */
  def bulkLoad(job: Job, hFilePath: String, name: String): Unit = {
    val connection = ConnectionFactory.createConnection(job.getConfiguration)
    val admin = connection.getAdmin

    //创建namespace
    if (!namespaceExists(admin, namaspace)) {
      val namespaceDescriptor = NamespaceDescriptor.create(namaspace).build
      admin.createNamespace(namespaceDescriptor)
    }

    //创建表
    val tableName = TableName.valueOf(Bytes.toBytes(namaspace), Bytes.toBytes(name))
    if (!admin.tableExists(tableName)) {
      admin.createTable(
        TableDescriptorBuilder.newBuilder(tableName)
          .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(family)).build())
          .build()
      )
    }

    val table = connection.getTable(tableName)
    val regionLocator = new HRegionLocator(tableName, connection.asInstanceOf[ClusterConnection])
    // 加载数据
    HFileOutputFormat2.configureIncrementalLoad(job, table, regionLocator)
    val loader = new LoadIncrementalHFiles(job.getConfiguration)
    loader.doBulkLoad(new Path(hFilePath), admin, table, regionLocator)
  }

  /**
    * 检查namespace是否存在
    * @param admin
    * @param namespace
    * @return
    */
  def namespaceExists(admin: Admin, namespace: String): Boolean = {
    try {
      admin.getNamespaceDescriptor(namespace)
      true
    } catch {
      case e: NamespaceNotFoundException => false
      case _: Throwable => throw new IllegalStateException()
    }
  }

}
