package org.tlh.rt.dw.utils

import java.sql.DriverManager

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-23
  */
object PhoenixUtils {

  def queryUser() = {
    Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
    val connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
    val statement = connection.createStatement()
    val set = statement.executeQuery("select count(1) from SYSTEM.LOG")
    set.next()
    val l = set.getLong(1)
    l
  }

  def main(args: Array[String]): Unit = {
    val l = queryUser()
    println(l)
  }

}
