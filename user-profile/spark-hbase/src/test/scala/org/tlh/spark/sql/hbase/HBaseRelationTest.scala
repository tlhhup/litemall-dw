package org.tlh.spark.sql.hbase

import org.apache.spark.sql.SparkSession

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-20
  */
object HBaseRelationTest {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("HBaseRelationTest")
      .master("local[*]")
      .getOrCreate()

    val df = spark.read
      .format("hbase")
      .option("zkHosts", "cdh-master")
      .option("zkPort", "2181")
      .option("hBaseTable", "litemall:litemall_user")
      .option("family", "cf")
      .option("selectFields", "id,username,gender")
      .load()

    df.printSchema()

    df.show(10)

    df.write
      .format("hbase")
      .option("zkHosts", "cdh-master")
      .option("zkPort", "2181")
      .option("hBaseTable", "litemall:litemall_test")
      .option("family", "cf")
      .option("rowKey", "id")
      .save()

    spark.close()
  }

}
