package org.tlh.warehouse.util

import org.junit.{After, Before, Test}
import redis.clients.jedis.Jedis

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-17
  */
class JedisUtilsTest {

  var jedis: Jedis = _

  @Before
  def before(): Unit = {
    jedis = JedisUtils.getResource()
  }

  @After
  def after(): Unit = {
    JedisUtils.release(jedis)
  }

  @Test
  def str(): Unit = {
    jedis.set("dd", "hello")
  }

  @Test
  def get(): Unit = {
    println(jedis.get("dd"))
  }

}
