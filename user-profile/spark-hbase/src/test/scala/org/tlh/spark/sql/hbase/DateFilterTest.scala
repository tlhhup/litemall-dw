package org.tlh.spark.sql.hbase

import org.tlh.spark.sql.hbase.filter.DateFilter

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-21
  */
object DateFilterTest {

  def main(args: Array[String]): Unit = {
    val dateFilter = DateFilter("createTime#year#3")
    println(s"${dateFilter.start()}~${dateFilter.end()}")
  }

}
