package org.tlh.profile.model

import com.typesafe.config.ConfigFactory
import org.apache.commons.lang3.StringUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.tlh.profile.entity.{CommonMeta, HBaseCatalog, HBaseColumn, HBaseTable, MetaData, Tag}
import org.tlh.profile.enums.MetaDataTypeEnum._

import scala.collection.mutable

/**
  * 模型基类
  *
  * @author 离歌笑
  * @desc
  * @date 2021-04-09
  */
trait BaseModel {

  private[this] val config = ConfigFactory.load()

  protected[this] val spark = init()

  /**
    * 初始化spark
    *
    * @return
    */
  private[this] def init(): SparkSession = {
    // 1.加载spark环境配置信息
    def loadSparkConf(): SparkConf = {

      val conf = new SparkConf()
      conf.setAppName(getAppName())

      val config = ConfigFactory.load("spark.conf")

      import scala.collection.JavaConverters._

      config.entrySet().asScala.foreach(item => {
        val resource = item.getValue.origin().resource()
        if ("spark.conf".equals(resource)) {
          conf.set(item.getKey, item.getValue.unwrapped().toString)
        }
      })

      conf
    }

    val conf = loadSparkConf()

    // 2.构建sparkSession
    val builder = SparkSession.builder()
    // 2.1通用信息
    builder.config(conf)
    // 2.2hive信息
    if (hiveEnable) {
      builder.enableHiveSupport()
    }

    builder.getOrCreate()
  }

  /**
    * 启动模型
    */
  def start() = {
    //1. 加载标签规则
    val (model, rules) = this.loadTagRules()

    //2. 加载标签元数据
    val metaData = this.loadTagMetaData(model)

    //3. 创建数据源
    val sources = this.createDataSources(metaData)

    //4. 标签规则 业务逻辑 处理
    this.processDetail(model, rules, sources)

    //5. 存储数据到hBase

    //6. 释放资源
    this.release()
  }

  /**
    * 加载模型规则信息
    *
    * @return
    */
  private[this] def loadTagRules(): (Tag, Array[Tag]) = {
    //1. 获取需要加载的标签名称
    val tagName = getTagName()
    if (StringUtils.isEmpty(tagName)) {
      throw new IllegalArgumentException("TagName must not been Null!")
    }
    //2. 加载标签数据
    val tagDf = this.spark.read
      .format("jdbc")
      .option("url", this.config.getString("jdbc.url"))
      .option("dbtable", this.config.getString("jdbc.tag.table"))
      .option("user", this.config.getString("jdbc.user"))
      .option("password", this.config.getString("jdbc.passowrd"))
      .load()

    import spark.implicits._
    //3. 获取四级标签  模型标签
    val fourTag = tagDf.filter('name === tagName)
      .as[Tag]
      .collect()(0)

    //4. 获取五级标签  值域
    val fiveTags = tagDf.filter('pid === fourTag.id)
      .as[Tag]
      .collect()

    (fourTag, fiveTags)
  }

  /**
    * 加载标签元数据信息
    *
    * @param tag
    * @return
    */
  private[this] def loadTagMetaData(tag: Tag): Array[MetaData] = {
    //1. 查询sql
    val sql =
      s"""
         |select
         |*
         |from
         |${config.getString("jdbc.meta.table")}
         |where
         |tag_id=${tag.id}
      """.stripMargin

    //2. 加载数据
    val metaDf = this.spark.read
      .format("jdbc")
      .option("url", this.config.getString("jdbc.url"))
      .option("query", sql)
      .option("user", this.config.getString("jdbc.user"))
      .option("password", this.config.getString("jdbc.passowrd"))
      .load()

    import spark.implicits._

    metaDf.as[MetaData].collect()
  }

  /**
    * 创建数据源
    *
    * @param metaData
    * @return
    */
  private[this] def createDataSources(metaData: Array[MetaData]): Array[(CommonMeta, DataFrame)] = {
    metaData.map(item => {
      item.metaDataType() match {
        case RDBMS => this.createRDBMSSource(item)
        case HDFS => this.createHDFSSource(item)
        case HBASE => this.createHBaseSource(item)
        case HIVE => this.createHiveSource(item)
      }
    })
  }

  /**
    * 创建RDBMS数据源
    *
    * @param metaData
    * @return
    */
  protected[this] def createRDBMSSource(metaData: MetaData): (CommonMeta, DataFrame) = {
    val rdbms = metaData.toRDBMSMeta()
    // 基本信息
    val loader = this.spark.read
      .format("jdbc")
      .option("url", rdbms.url)
      .option("driver", rdbms.driver)
      .option("user", rdbms.user)
      .option("password", rdbms.password)

    // 设置查询
    if (StringUtils.isEmpty(rdbms.dbTable)) {
      loader.option("query", rdbms.querySql)
    } else {
      loader.option("dbtable", rdbms.dbTable)
    }

    //加载数据
    val sourceDf = loader.load()

    (rdbms.commonMeta, sourceDf)
  }

  /**
    * 创建HDFS数据源
    *
    * @param metaData
    * @return
    */
  protected[this] def createHDFSSource(metaData: MetaData): (CommonMeta, DataFrame) = {
    val hdfs = metaData.toHDFSMeta()
    val sourceDf = spark.read
      .load(hdfs.inPath)

    (hdfs.commonMeta, sourceDf)
  }

  /**
    * 创建Hive数据源
    *
    * @param metaData
    * @return
    */
  protected[this] def createHiveSource(metaData: MetaData): (CommonMeta, DataFrame) = {
    val hive = metaData.toHiveMeta()
    val sourceDf = spark.read.table(hive.dbTable)
    (hive.commonMeta, sourceDf)
  }

  /**
    * 创建hBase数据源
    *
    * @param metaData
    * @return
    */
  protected[this] def createHBaseSource(metaData: MetaData): (CommonMeta, DataFrame) = {
    val hBase = metaData.toHBaseMeta()
    //1. 构建HBaseCatalog信息
    //1.1 表
    val table = HBaseTable(hBase.namespace, hBase.table)
    // 1.2 处理列
    val columns = new mutable.HashMap[String, HBaseColumn]()
    // 1.2.1 处理rowKey
    columns.put(hBase.rowKey, HBaseColumn("rowkey", hBase.rowKey, "string"))
    // 1.2.2 处理普通列
    val fields = hBase.commonMeta.selectFieldNames.split(",")
    fields.foreach(field => {
      columns.put(field, HBaseColumn(hBase.family, field, "string"))
    })
    val hBaseCatalog = HBaseCatalog(table, hBase.rowKey, columns)
    //2. 构建数据源
    val sourceDf = spark.read
      .options(Map(HBaseTableCatalog.tableCatalog -> hBaseCatalog.toJson()))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .load()

    (hBase.commonMeta, sourceDf)
  }

  def release() = {
    if (spark != null) {
      spark.close()
    }
  }


  /**
    * 应用名称
    *
    * @return
    */
  def getAppName(): String

  /**
    * 获取标签名称
    *
    * @return
    */
  def getTagName(): String

  /**
    * 是否使用hive数据源
    *
    * @return
    */
  def hiveEnable(): Boolean = false

  /**
    * 业务逻辑处理
    *
    * @param model
    * @param rules
    * @param sources
    * @return
    */
  def processDetail(model: Tag, rules: Array[Tag], sources: Array[(CommonMeta, DataFrame)]): DataFrame

}
