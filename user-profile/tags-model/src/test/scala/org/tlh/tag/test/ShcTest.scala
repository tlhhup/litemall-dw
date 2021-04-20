package org.tlh.tag.test

import org.apache.spark.sql.SparkSession

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-10
  */
object ShcTest {

  def main(args: Array[String]): Unit = {
//    val catalog =
//      """
//        |{
//        |  "table": {
//        |    "namespace": "litemall",
//        |    "name": "litemall_user"
//        |  },
//        |  "rowkey": "id",
//        |  "columns": {
//        |    "gender": {
//        |      "cf": "cf",
//        |      "col": "gender",
//        |      "type": "string"
//        |    },
//        |    "id": {
//        |      "cf": "rowkey",
//        |      "col": "id",
//        |      "type": "string"
//        |    }
//        |  }
//        |}
//      """.stripMargin
//
//    val spark = SparkSession.builder()
//      .appName("test")
//      .master("local[*]")
//      .getOrCreate()
//
//    val df = spark.read
//      .option(HBaseTableCatalog.tableCatalog, catalog)
//      .format("org.apache.spark.sql.execution.datasources.hbase")
//      .load()
//
//    df.show(10)
//
//    spark.close()
  }

}
