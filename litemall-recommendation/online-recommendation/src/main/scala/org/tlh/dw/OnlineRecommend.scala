package org.tlh.dw

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.tlh.spark.util.{JedisUtil, MongoUtil}
import org.tlh.spark.util.Helpers._

/**
  * 自定义实时推荐：用户最近的口味是一致的
  *
  * @author 离歌笑
  * @desc
  * @date 2021-01-27
  */
object OnlineRecommend {

  val MAX_USER_RATING_NUM = 20
  val MAX_SIM_PRODUCTS_NUM = 20
  val NEED_RELOAD_PRODUCT_SIM = "NEED_RELOAD_PRODUCT_SIM"

  //mongo表名
  val RATING_COLLECTION = "ratings"
  val PRODUCT_SIM = "product_sims"
  val STREAM_RECS = "stream_recs"

  //配置信息
  val conf = Map(
    "spark.master" -> "local[*]",
    "mongo.uri" -> "mongodb://storage:27017/litemall",
    "mongo.db" -> "litemall"
  )

  def main(args: Array[String]): Unit = {
    //当应用被停止的时候，进行如下设置可以保证当前批次执行完之后再停止应用
    System.setProperty("spark.streaming.stopGracefullyOnShutdown", "true")

    //1. 获取duration
    val duration = if (args.length > 0) args(0).toInt else 5

    //创建sparkSession
    val sparkConf = new SparkConf().setMaster(conf("spark.master")).setAppName("OnlineRecommend")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext

    import spark.implicits._

    //加载商品相似度数据
    var products_sim = spark.read
      .option("uri", conf("mongo.uri"))
      .option("collection", PRODUCT_SIM)
      .format("com.mongodb.spark.sql")
      .load()
      .as[ProductRecs]
      .rdd
      .map(item => (item.productId, item.recs.map(x => (x.productId, x.score)).toMap))
      .collectAsMap()

    //创建广播变量
    var productSimBC = sc.broadcast(products_sim)

    //订阅kafka的数据
    val scc = new StreamingContext(sc, Seconds(duration))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kafka-master:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "litemall_recommender",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val stream = KafkaUtils.createDirectStream[String, String](
      scc,
      PreferConsistent,
      Subscribe[String, String](Array(conf("kafka.topic")), kafkaParams)
    )

    //解析kafka中的数据
    val ratingDStream = stream.map(item => {
      //eventType|userId|goodsIds
      val attrs = item.value().split("\\|")
      UserGoods(attrs(0).toInt, attrs(1).toInt, attrs(2).split(",").map(_.toInt))
    })


    //处理Kafka中的数据
    ratingDStream.foreachRDD(item => {
      // todo 是否重新加载商品相似度数据
      if (JedisUtil.getBoolean(NEED_RELOAD_PRODUCT_SIM)) {
        products_sim = spark.read
          .option("uri", conf("mongo.uri"))
          .option("collection", PRODUCT_SIM)
          .format("com.mongodb.spark.sql")
          .load()
          .as[ProductRecs]
          .rdd
          .map(item => (item.productId, item.recs.map(x => (x.productId, x.score)).toMap))
          .collectAsMap()

        //创建广播变量
        productSimBC = sc.broadcast(products_sim)
      }
      // todo 更具不同的事件不同的推荐处理，存入不同的collection
      // todo 最近数据存入到redis中
      item.foreach(rating => {
        //1.获取用户最近评分数据
        val userRecentlyRatingProducts = getUserRecentlyRatingProducts(MAX_USER_RATING_NUM, rating.userId)
        //2.获取备选商品,并过滤掉用户评分过的商品
        val candidateProducts = getCandidateProducts(MAX_SIM_PRODUCTS_NUM, rating.productIds, rating.userId, productSimBC.value)
        //3.计算所以备选商品的推荐度
        val streamRecs = calculateProductRating(userRecentlyRatingProducts, candidateProducts, productSimBC.value)
        //4.把推荐列表保存到mongodb
        saveDataToMongoDB(rating.userId, streamRecs)
      })
    })


    //开启
    scc.start()
    scc.awaitTermination()
  }

  import scala.collection.JavaConversions._

  /**
    * 获取用户最近交互的商品
    * @param num
    * @param userId
    * @return
    */
  def getUserRecentlyRatingProducts(num: Int,
                                    userId: Int
                                   ): Array[(Int, Double)] = {
    // key: userId:1  values: productId:score
    JedisUtil.lrange("userId:" + userId, 0, num)
      .map(item => {
        val attrs = item.split("\\:")
        (attrs(0).toInt, attrs(1).toDouble)
      })
      .toArray
  }

  /**
    * 获取候选商品，查询与交互的商品相似，但之前没有交互过的商品
    *
    * @param num
    * @param productIds
    * @param userId
    * @param productSim
    * @return
    */
  def getCandidateProducts(num: Int,
                           productIds: Seq[Int],
                           userId: Int,
                           productSim: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]]): Array[Int] = {
    //获取相似的商品
    val allProducts = productIds.flatMap(productId => productSim(productId)).toArray
    //获取用户评分过的商品 todo 有交互的数据存储
    val existsProducts = MongoUtil.getDBOrDefault()
      .getCollection(RATING_COLLECTION)
      .find(equal("userId", userId))
      .results()
      .map(_.get("productId").toString.toInt)

    allProducts.filter(item => !existsProducts.contains(item._1))
      .sortWith(_._2 > _._2)
      .take(num)
      .map(_._1)
  }

  /**
    * 计算候选商品与最近交互的商品的相似度
    *
    * @param userRecentlyRatingProducts
    * @param candidateProducts
    * @param productSim
    * @return
    */
  def calculateProductRating(
                              userRecentlyRatingProducts: Array[(Int, Double)],
                              candidateProducts: Array[Int],
                              productSim: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]]
                            ): Array[(Int, Double)] = {
    //存储商品的推荐度得分
    val scores = scala.collection.mutable.ArrayBuffer[(Int, Double)]()
    val inCount = scala.collection.mutable.Map[Int, Int]()
    val reCount = scala.collection.mutable.Map[Int, Int]()
    //计算所有候选商品和用户最近评分商品的相似度
    for (candidateProduct <- candidateProducts; userRecentlyProduct <- userRecentlyRatingProducts) {
      //获取商品相识度
      val simScore = getProductsSimScore(candidateProduct, userRecentlyProduct._1, productSim)
      if (simScore > 0.4) {
        scores += ((candidateProduct, simScore * userRecentlyProduct._2))
        if (userRecentlyProduct._2 > 3) {
          inCount(candidateProduct) = inCount.getOrDefault(candidateProduct, 0) + 1
        } else {
          reCount(candidateProduct) = reCount.getOrDefault(candidateProduct, 0) + 1
        }
      }
    }

    //计算推荐度
    scores.groupBy(_._1)
      .map {
        case (productId, scoreList) => {
          (productId, scoreList.map(_._2).sum / scoreList.length + log(inCount.getOrDefault(productId, 1)) - log(reCount.getOrDefault(productId, 1)))
        }
      }
      .toArray
      .sortWith(_._2 > _._2)
  }

  def getProductsSimScore(product1: Int, product2: Int,
                          simProducts: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]]): Double = {
    simProducts.get(product1) match {
      case Some(sims) => sims.get(product2) match {
        case Some(score) => score
        case None => 0.0
      }
      case None => 0.0
    }
  }

  def log(m: Int): Double = {
    val N = 10
    math.log(m) / math.log(N)
  }

  def saveDataToMongoDB(userId: Int, streamRecs: Array[(Int, Double)]): Unit = {
    val streamRecsCollection = MongoUtil.getDBOrDefault().getCollection(STREAM_RECS)
    // 按照userId查询并更新
    streamRecsCollection.deleteMany(equal("userId", userId)).results()
    streamRecsCollection.insertOne(Document("userId" -> userId,
      "recs" -> streamRecs.map(x => Document("productId" -> x._1, "score" -> x._2)).seq)).results()
  }


}