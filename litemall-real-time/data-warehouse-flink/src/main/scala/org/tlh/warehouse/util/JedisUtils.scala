package org.tlh.warehouse.util

import redis.clients.jedis.{Jedis, JedisPoolConfig, JedisSentinelPool, Protocol}

import collection.JavaConverters._

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-17
  */
object JedisUtils {

  private[this] val pool = {
    // 连接池配置
    val jedisPoolConfig = new JedisPoolConfig()
    jedisPoolConfig.setMaxTotal(AppConfig.redis_pool_max)
    jedisPoolConfig.setMaxIdle(AppConfig.redis_pool_idle_max)
    jedisPoolConfig.setMinIdle(AppConfig.redis_pool_idle_min)

    // 哨兵信息   :_* 解包处理
    val sentinels = Set(AppConfig.redis_sentinels.split(","): _*).asJava
    // 创建连接池
    val sentinelPool = new JedisSentinelPool(AppConfig.redis_master,
      sentinels,
      jedisPoolConfig,
      Protocol.DEFAULT_TIMEOUT,
      null,
      AppConfig.redis_db_index)
    sentinelPool
  }

  /**
    * 获取资源
    *
    * @return
    */
  def getResource(): Jedis = {
    pool.getResource
  }

  /**
    * 释放资源
    *
    * @param jedis
    */
  def release(jedis: Jedis) = {
    if (jedis != null)
      jedis.close()
  }

}
