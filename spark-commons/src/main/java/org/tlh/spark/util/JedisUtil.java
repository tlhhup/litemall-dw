package org.tlh.spark.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-16
 */
public final class JedisUtil {

    private static JedisPool pool = null;

    private JedisUtil() {
    }

    static {
        // 创建一个redis的连接池
        Properties prop = new Properties();
        try {
            prop.load(JedisUtil.class.getClassLoader().getResourceAsStream("redis.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        int maxIdle = new Integer(prop.getProperty("redis.maxIdle"));
        int minIdle = new Integer(prop.getProperty("redis.minIdle"));
        int maxTotal = new Integer(prop.getProperty("redis.maxTotal"));
        poolConfig.setMaxIdle(maxIdle);// 最大闲置个数
        poolConfig.setMinIdle(minIdle);// 最小闲置个数
        poolConfig.setMaxTotal(maxTotal);// 最大连接数
        String host = prop.getProperty("redis.host");
        int port = new Integer(prop.getProperty("redis.port"));
        pool = new JedisPool(poolConfig, host, port);
    }

    /**
     * 获取jedis
     *
     * @return
     */
    public static Jedis getJedis() {
        return pool.getResource();
    }

    /**
     * 增1
     *
     * @param key
     */
    public static void inc(String key) {
        try (Jedis jedis = getJedis()) {
            jedis.incr(key);
        } catch (Exception e) {

        }
    }

    /**
     * 减1
     *
     * @param key
     */
    public static void desc(String key) {
        try (Jedis jedis = getJedis()) {
            jedis.decr(key);
        } catch (Exception e) {

        }
    }

    public static void inc(String key, long value) {
        try (Jedis jedis = getJedis()) {
            jedis.incrBy(key, value);
        } catch (Exception e) {

        }
    }

    public static void incrByFloat(String key, double value) {
        try (Jedis jedis = getJedis()) {
            jedis.incrByFloat(key, value);
        } catch (Exception e) {

        }
    }

    public static void setValue(String key, String value) {
        try (Jedis jedis = getJedis()) {
            jedis.set(key, value);
        } catch (Exception e) {

        }
    }

    public static void zSetAppend(String key, String member, long value) {
        try (Jedis jedis = getJedis()) {
            jedis.zadd(key, value, member);
        } catch (Exception e) {

        }
    }

    public static void zSetIncBy(String key, String member, long value) {
        try (Jedis jedis = getJedis()) {
            jedis.zincrby(key, value, member);
        } catch (Exception e) {

        }
    }

    public static long zCount(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.zcard(key);
        } catch (Exception e) {

        }
        return 0;
    }

    public static void setex(String key, int seconds, String value) {
        try (Jedis jedis = getJedis()) {
            jedis.setex(key, seconds, value);
        }
    }

    public static Boolean getBoolean(String key) {
        try (Jedis jedis = getJedis()) {
            String value = jedis.get(key);
            return Boolean.parseBoolean(value);
        }
    }

}