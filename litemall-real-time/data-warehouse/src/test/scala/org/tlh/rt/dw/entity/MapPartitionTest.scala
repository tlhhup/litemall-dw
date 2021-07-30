package org.tlh.rt.dw.entity

import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-29
  */
object MapPartitionTest extends App {

  val conf = new SparkConf().setMaster("local[*]").setAppName("MapPartition")
  val sc = new SparkContext(conf)

  val rdd = sc.parallelize(1 to 9, 3)

  rdd.mapPartitions(iter => {
    val items = iter.toList
    items.map(item => item * 2)
    items.iterator
  })
    .foreach(print)

  sc.stop()

}
