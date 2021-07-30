package org.tlh.rt.dw.utils

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement}

import org.tlh.rt.dw.entity.{GoodsBrand, GoodsCategory, GoodsSku, RegionInfo, UserDwdDim}

import scala.collection.mutable

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-23
  */
object PhoenixUtils {

  /**
    * 查询用户订单历史
    *
    * @param userId
    * @return
    */
  def queryUserOrder(userId: List[Long]): mutable.ArrayBuffer[Long] = {
    var connection: Connection = null
    var statement: Statement = null
    var set: ResultSet = null
    try {
      Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
      connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
      statement = connection.createStatement()
      set = statement.executeQuery(s"SELECT USER_ID FROM litemall.user_order_status WHERE USER_ID IN (${userId.mkString(",")})")
      val userOder = new mutable.ArrayBuffer[Long]()
      while (set.next()) {
        val userId = set.getLong(1)
        userOder.append(userId)
      }
      userOder
    } finally {
      release(connection, statement, set)
    }
  }

  /**
    * 保存用户订单记录
    *
    * @param userId
    * @return
    */
  def saveUserOrder(userId: List[Long]): Unit = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
      connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
      statement = connection.prepareStatement("INSERT INTO LITEMALL.USER_ORDER_STATUS(USER_ID,IS_FIRST) values(?,?)")
      for (id <- userId) {
        statement.setLong(1, id)
        statement.setBoolean(2, true)
        statement.addBatch()
      }

      statement.execute()
    } finally {
      release(connection, statement)
    }
  }

  /**
    * 查询用户信息
    *
    * @param userId
    * @return
    */
  def queryUser(userId: List[Long]): mutable.HashMap[Long, UserDwdDim] = {
    var connection: Connection = null
    var statement: Statement = null
    var set: ResultSet = null
    try {
      Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
      connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
      statement = connection.createStatement()
      set = statement.executeQuery(s"SELECT ID,GENDER,AGE_GROUP FROM LITEMALL.USERS WHERE ID IN (${userId.mkString(",")})")
      val users = new mutable.HashMap[Long, UserDwdDim]()
      while (set.next()) {
        val userId = set.getLong(1)
        val gender = set.getString(2)
        val age = set.getString(3)
        users.put(userId, UserDwdDim(userId, gender, age))
      }
      users
    } finally {
      release(connection, statement, set)
    }
  }

  /**
    * 查询商品sku信息
    *
    * @param skuIds
    * @return
    */
  def querySku(skuIds: List[Int]): mutable.HashMap[Long, GoodsSku] = {
    var connection: Connection = null
    var statement: Statement = null
    var set: ResultSet = null
    try {
      Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
      connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
      statement = connection.createStatement()
      set = statement.executeQuery(s"SELECT * FROM LITEMALL.DWD_DIM_SKU WHERE ID IN (${skuIds.mkString(",")})")
      val skus = new mutable.HashMap[Long, GoodsSku]()
      while (set.next()) {
        val id = set.getInt(1)
        val name = set.getString(2)
        val categoryId = set.getInt(3)
        val categoryName = set.getString(5)
        val brandId = set.getInt(4)
        val brandName = set.getString(6)
        skus.put(id, GoodsSku(id, name, categoryId, brandId, categoryName, brandName))
      }
      skus
    } finally {
      release(connection, statement, set)
    }
  }

  /**
    * 获取所有region信息
    *
    * @return
    */
  def queryRegion(): mutable.HashMap[Long, RegionInfo] = {
    var connection: Connection = null
    var statement: Statement = null
    var set: ResultSet = null
    try {
      Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
      connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
      statement = connection.createStatement()
      set = statement.executeQuery("SELECT * FROM LITEMALL.REGION")
      val regions = new mutable.HashMap[Long, RegionInfo]()
      while (set.next()) {
        val id = set.getLong(1)
        val name = set.getString(2)
        val code = set.getInt(3)
        regions.put(id, RegionInfo(id, name, code))
      }
      regions
    } finally {
      release(connection, statement, set)
    }
  }

  /**
    * 获取商品品牌
    *
    * @return
    */
  def queryBrands(): mutable.HashMap[Long, GoodsBrand] = {
    var connection: Connection = null
    var statement: Statement = null
    var set: ResultSet = null
    try {
      Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
      connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
      statement = connection.createStatement()
      set = statement.executeQuery("SELECT * FROM LITEMALL.BRAND")
      val brands = new mutable.HashMap[Long, GoodsBrand]()
      while (set.next()) {
        val id = set.getInt(1)
        val name = set.getString(2)
        brands.put(id, GoodsBrand(id, name))
      }
      brands
    } finally {
      release(connection, statement, set)
    }
  }

  /**
    * 获取商品类别
    *
    * @return
    */
  def queryCategories(): mutable.HashMap[Long, GoodsCategory] = {
    var connection: Connection = null
    var statement: Statement = null
    var set: ResultSet = null
    try {
      Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
      connection = DriverManager.getConnection("jdbc:phoenix:hadoop-master")
      statement = connection.createStatement()
      set = statement.executeQuery("SELECT * FROM LITEMALL.GOODS_CATEGORY")
      val categories = new mutable.HashMap[Long, GoodsCategory]()
      while (set.next()) {
        val id = set.getInt(1)
        val name = set.getString(2)
        categories.put(id, GoodsCategory(id, name))
      }
      categories
    } finally {
      release(connection, statement, set)
    }
  }

  private[this] def release(connection: Connection, statement: Statement, resultSet: ResultSet = null): Unit = {
    if (resultSet != null) {
      resultSet.close()
    }
    if (statement != null) {
      statement.close()
    }
    if (connection != null) {
      connection.close()
    }
  }

  def main(args: Array[String]): Unit = {
    val l = queryUser(List(1, 2, 3))
    println(l)
  }

}
