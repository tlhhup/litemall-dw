package org.tlh.driver

import org.apache.spark.sql.SparkSession

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-07
  */
object CompressHBaseBulkLoad {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("CompressHBaseBulkLoad")
      .master("local[*]")
      .config("dfs.client.use.datanode.hostname", "true")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    spark.read
      .table("litemall.litemall_order_lzo")
      .show(10)


    spark.close()
  }

}
