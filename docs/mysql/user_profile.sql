-- MySQL dump 10.13  Distrib 8.0.26, for macos10.15 (x86_64)
--
-- Host: storage    Database: user_profile
-- ------------------------------------------------------
-- Server version	5.7.31-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tb_tag_model`
--

DROP TABLE IF EXISTS `tb_tag_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_tag_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID(四级标签)',
  `model_type` int(11) DEFAULT NULL COMMENT '模型类型：1 匹配 2 统计 3 挖掘',
  `model_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模型名称',
  `model_main` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型driver全限定名',
  `model_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型在hdfs中的地址',
  `model_jar` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型jar包文件名',
  `model_args` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模型参数',
  `spark_opts` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'spark的执行参数',
  `schedule_rule` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'oozie的调度规则',
  `operator` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `operation` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `state` int(11) DEFAULT NULL COMMENT '状态',
  `oozie_task_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'oozie调度任务ID',
  PRIMARY KEY (`id`),
  KEY `fk_tb_tag_model_tb_basic_tag` (`tag_id`),
  CONSTRAINT `fk_tb_tag_model_tb_basic_tag` FOREIGN KEY (`tag_id`) REFERENCES `tb_basic_tag` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签模型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_tag_model`
--

LOCK TABLES `tb_tag_model` WRITE;
/*!40000 ALTER TABLE `tb_tag_model` DISABLE KEYS */;
INSERT INTO `tb_tag_model` VALUES (1,7,NULL,'用户性别','org.tlh.litemall.GenderModel','/app/litemall/model/2021-03-23/spark-cdh-1.0.0.jar','spark-cdh-1.0.0.jar','','--executor-memory 1G --num-executors 1','1,2021-02-03T00:00+0800,2022-09-23T00:00+0800',NULL,NULL,'2021-03-23 12:22:09','2021-07-14 14:24:58',4,''),(2,8,NULL,'年龄段','org.tlh.litemall.GenderModel','/app/litemall/model/2021-03-23/spark-cdh-1.0.0.jar','spark-cdh-1.0.0.jar','','--executor-memory 1G --num-executors 1','1,2021-02-03T00:00+0800,2022-09-23T00:00+0800',NULL,NULL,'2021-03-23 12:26:37','2021-07-14 14:25:24',5,'0000006-210331021525664-oozie-oozi-C'),(12,43,NULL,'消费周期','org.tlh.litemall.ConsumptionModel','/app/litemall/model/2021-03-26/spark-cdh-1.0.0.jar','spark-cdh-1.0.0.jar','','--executor-memory 1G --num-executors 1','2,2021-02-03T00:00+0800,2022-09-23T00:00+0800',NULL,NULL,'2021-03-26 10:27:04','2021-07-14 14:27:43',2,NULL),(17,54,NULL,'消费能力','fff','','','','--executor-memory 1G --num-executors 1','1,2021-04-08T00:00+0800,2021-05-11T00:00+0800',NULL,NULL,'2021-04-21 02:49:19','2021-07-14 14:27:09',NULL,NULL),(18,55,NULL,'客单价','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-08T00:00+0800,2021-05-19T00:00+0800',NULL,NULL,'2021-04-21 02:50:03','2021-07-14 14:28:32',NULL,NULL),(20,57,NULL,'单笔最高','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-02T00:00+0800,2021-05-21T00:00+0800',NULL,NULL,'2021-04-21 02:51:08','2021-07-14 14:28:56',NULL,NULL),(21,58,NULL,'购买频率','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-10T00:00+0800,2021-05-27T00:00+0800',NULL,NULL,'2021-04-21 02:51:45','2021-07-14 14:29:10',NULL,NULL),(22,87,NULL,'退货率','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-08T00:00+0800,2021-05-21T00:00+0800',NULL,NULL,'2021-04-22 01:47:03','2021-07-14 14:29:41',NULL,NULL),(23,91,NULL,'有券必买','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-10T00:00+0800,2021-05-12T00:00+0800',NULL,NULL,'2021-04-22 14:03:02','2021-07-14 14:30:13',NULL,NULL),(24,96,NULL,'省钱小能手','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-09T00:00+0800,2021-05-29T00:00+0800',NULL,NULL,'2021-04-23 02:12:51','2021-07-14 14:30:37',NULL,NULL),(25,100,NULL,'用户价值','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-28T00:00+0800,2021-05-29T00:00+0800',NULL,NULL,'2021-04-28 06:35:50','2021-07-14 14:31:18',NULL,NULL),(26,109,NULL,'促销敏感度','dd','','','','--executor-memory 1G --num-executors 1','1,2021-04-29T00:00+0800,2021-05-19T00:00+0800',NULL,NULL,'2021-04-29 08:52:39','2021-07-14 14:31:32',NULL,NULL);
/*!40000 ALTER TABLE `tb_tag_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_basic_tag`
--

DROP TABLE IF EXISTS `tb_basic_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_basic_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `industry` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行业、子行业、业务类型、标签、属性',
  `rule` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签规则: 四级 metadata表中数据 五级 值域',
  `business` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务描述',
  `level` int(11) DEFAULT NULL COMMENT '标签等级',
  `pid` bigint(20) DEFAULT NULL COMMENT '父标签ID',
  `order` int(11) DEFAULT NULL COMMENT '子标签排序字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `state` int(11) DEFAULT NULL COMMENT '状态：1申请中、2开发中、3开发完成、4已上线、5已下线、6已禁用',
  `remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `hbase_fields` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冗余字段，对应hbase中的列名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基础标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_basic_tag`
--

LOCK TABLES `tb_basic_tag` WRITE;
/*!40000 ALTER TABLE `tb_basic_tag` DISABLE KEYS */;
INSERT INTO `tb_basic_tag` VALUES (1,'电商','电商',NULL,NULL,1,NULL,0,'2021-03-22 01:02:04',NULL,NULL,NULL,NULL),(2,'Litemall','子行业',NULL,NULL,2,1,0,'2021-03-22 01:02:27',NULL,NULL,NULL,NULL),(3,'人口属性','用户特征',NULL,NULL,3,2,0,'2021-03-22 01:04:46',NULL,NULL,NULL,NULL),(4,'商业属性','消费特征',NULL,NULL,3,2,0,'2021-03-22 01:05:23',NULL,NULL,NULL,NULL),(7,'性别','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_user\nrowKey=id\nfamily=cf\nselectFieldNames=id,gender\noutFields=gender','注册会员的性别',4,3,0,'2021-03-23 12:21:11','2021-07-14 14:24:58',4,NULL,'gender'),(8,'年龄段','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_user\nrowKey=id\nfamily=cf\nselectFieldNames=id,birthday\noutFields=age','注册用户的生日所属年龄段',4,3,0,'2021-03-23 12:26:37','2021-07-14 14:25:24',4,NULL,'age'),(9,'男','属性','1','注册会员性别男',5,7,0,'2021-03-23 12:55:14',NULL,NULL,NULL,NULL),(19,'女','属性','2','注册会员性别女',5,7,0,'2021-03-25 02:51:38',NULL,NULL,NULL,NULL),(20,'50后','属性','19500101-19591231','注册会员出生日期为1950年-1959年区间的',5,8,0,'2021-03-25 05:54:24',NULL,NULL,NULL,NULL),(21,'60后','属性','19600101-19691231','注册会员出生日期为1960年-1969年区间的',5,8,0,'2021-03-25 05:54:47',NULL,NULL,NULL,NULL),(38,'70后','属性','19700101-19791231','注册会员出生日期为1970年-1979年区间的',5,8,0,'2021-03-26 06:13:12',NULL,NULL,NULL,NULL),(41,'行为属性','兴趣特征',NULL,NULL,3,2,0,'2021-03-26 06:14:29',NULL,NULL,NULL,NULL),(43,'消费周期','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,end_time\noutFields=consumption','用户的消费周期：7日、2周、1月、2月、3月、4月、5月、6月、1年',4,4,0,'2021-03-26 10:27:04','2021-07-14 14:27:43',2,NULL,'consumption'),(49,'80后','属性','19800101-19891231','注册会员出生日期为1980年-1989年区间的',5,8,0,'2021-04-09 07:02:04',NULL,NULL,NULL,NULL),(50,'90后','属性','19900101-19991231','注册会员出生日期为1990年-1999年区间的',5,8,0,'2021-04-09 07:02:24',NULL,NULL,NULL,NULL),(51,'00后','属性','20000101-20091231','注册会员出生日期为2000年-2009年区间的',5,8,0,'2021-04-09 07:02:40',NULL,NULL,NULL,NULL),(52,'10后','属性','20100101-20191231','注册会员出生日期为2010年-2019年区间的',5,8,0,'2021-04-09 07:02:55',NULL,NULL,NULL,NULL),(53,'20后','属性','20200101-20291231','注册会员出生日期为2020年-2029年区间的',5,8,0,'2021-04-09 07:03:09',NULL,NULL,NULL,NULL),(54,'消费能力','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,order_status,actual_price\nwhereFieldNames=end_time#month#6\noutFields=consumptionAbility','用户的消费能力：超高、高、中上、中、中下、低、很低',4,4,0,'2021-04-21 02:49:19','2021-07-14 14:27:09',1,NULL,'consumptionAbility'),(55,'客单价','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,order_status,order_price\nwhereFieldNames=add_time#month#6\noutFields=pct','用户订单数据的客单价：1~999、1000~2999、3000~4999、5000~9999',4,4,0,'2021-04-21 02:50:03','2021-07-14 14:28:32',1,NULL,'pct'),(57,'单笔最高','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,order_price\nwhereFieldNames=add_time#month#6\noutFields=singleOrderMax','用户订单数据中的金额最高的订单：1~999、1000~2999、3000~4999、5000~9999',4,4,0,'2021-04-21 02:51:08','2021-07-14 14:28:56',1,NULL,'singleOrderMax'),(58,'购买频率','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,id\nwhereFieldNames=add_time#month#6\noutFields=orderFrequency','用户订单数据中的购买频率：高、中、低',4,4,0,'2021-04-21 02:51:45','2021-07-14 14:29:10',1,NULL,'orderFrequency'),(59,'近7天','属性','0-7','消费周期是近7日',5,43,0,'2021-04-21 02:53:15',NULL,NULL,NULL,NULL),(60,'近14天','属性','8-14','消费周期是近2周',5,43,0,'2021-04-21 02:53:29',NULL,NULL,NULL,NULL),(61,'近30天','属性','15-30','消费周期是近1月',5,43,0,'2021-04-21 02:53:43',NULL,NULL,NULL,NULL),(62,'近60天','属性','31-60','消费周期是近60天',5,43,0,'2021-04-21 02:53:56',NULL,NULL,NULL,NULL),(63,'近90天','属性','61-90','消费周期是近90天',5,43,0,'2021-04-21 02:54:10',NULL,NULL,NULL,NULL),(64,'超高','属性','50000-1000000','超高的消费能力',5,54,0,'2021-04-21 02:54:37',NULL,NULL,NULL,NULL),(65,'高','属性','40000-49999','高消费能力',5,54,0,'2021-04-21 02:54:50',NULL,NULL,NULL,NULL),(66,'中上','属性','30000-39999','中等偏上的消费能力',5,54,0,'2021-04-21 02:55:02',NULL,NULL,NULL,NULL),(67,'中','属性','20000-29999','消费能力中等',5,54,0,'2021-04-21 02:55:15',NULL,NULL,NULL,NULL),(68,'中下','属性','10000-19999','消费能力中等偏下哦',5,54,0,'2021-04-21 02:55:29',NULL,NULL,NULL,NULL),(69,'低','属性','1000-9999','消费能力较低',5,54,0,'2021-04-21 02:55:41',NULL,NULL,NULL,NULL),(70,'很低','属性','1-999','消费能力很低哦',5,54,0,'2021-04-21 02:55:54',NULL,NULL,NULL,NULL),(71,'1-999','属性','1-999','客单价在1-999区间',5,55,0,'2021-04-21 02:56:24',NULL,NULL,NULL,NULL),(72,'1000-2999','属性','1000-2999','客单价在1000-2999区间',5,55,0,'2021-04-21 02:56:35',NULL,NULL,NULL,NULL),(73,'3000-4999','属性','3000-4999','客单价在3000-4999区间',5,55,0,'2021-04-21 02:56:46',NULL,NULL,NULL,NULL),(74,'5000-9999','属性','5000-9999','客单价在5000-9999区间',5,55,0,'2021-04-21 02:56:56',NULL,NULL,NULL,NULL),(79,'1-999','属性','1-999','单笔最高金额范围是1-999',5,57,0,'2021-04-21 02:58:20',NULL,NULL,NULL,NULL),(80,'1000-2999','属性','1000-2999','单笔最高支付金额的范围是1000-2999',5,57,0,'2021-04-21 02:58:35',NULL,NULL,NULL,NULL),(81,'3000-4999','属性','3000-4999','单笔最高的支付金额是3000-4999',5,57,0,'2021-04-21 02:58:45',NULL,NULL,NULL,NULL),(82,'5000-9999','属性','5000-9999','单笔最高支付金额为5000-9999',5,57,0,'2021-04-21 02:58:54',NULL,NULL,NULL,NULL),(83,'高','属性','91-500','购买频率很高',5,58,0,'2021-04-21 02:59:24','2021-04-22 18:02:01',NULL,NULL,NULL),(84,'中','属性','21-90','购买频率中等',5,58,0,'2021-04-21 02:59:36','2021-04-22 18:02:35',NULL,NULL,NULL),(85,'低','属性','0-20','购买频率较低',5,58,0,'2021-04-21 02:59:48','2021-04-22 18:02:41',NULL,NULL,NULL),(86,'近120天','属性','91-120','消费周期是近120天',5,43,0,'2021-04-21 06:17:35',NULL,NULL,NULL,NULL),(87,'退货率','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,order_status\nwhereFieldNames=add_time#month#6\noutFields=refundRate','用户订单中的退货率：高、中、低',4,4,0,'2021-04-22 01:47:03','2021-07-14 14:29:41',1,NULL,'refundRate'),(88,'高','属性','51-100','高退货率',5,87,0,'2021-04-22 01:48:24','2021-04-22 18:03:49',NULL,NULL,NULL),(89,'中','属性','21-50','退货率中等',5,87,0,'2021-04-22 01:48:45','2021-04-22 18:03:59',NULL,NULL,NULL),(90,'低','属性','0-20','退货率很低哦',5,87,0,'2021-04-22 01:48:57','2021-04-22 18:04:08',NULL,NULL,NULL),(91,'有券必买','标签','inType=mysql\ndriver=com.mysql.jdbc.Driver\nurl=jdbc:mysql://storage:3306/litemall\nuser=litemall\npassword=litemall123456\ndbTable=litemall_coupon_user\noutFields=coupon','订单中的有券必买：限时满减券、限时满减券、新用户优惠券、可兑换优惠券',4,4,0,'2021-04-22 14:03:02','2021-07-14 14:30:13',1,NULL,'coupon'),(92,'限时满减券','属性','1','限时满减券',5,91,0,'2021-04-22 14:03:32',NULL,NULL,NULL,NULL),(93,'限时满减券','属性','2','限时满减券',5,91,0,'2021-04-22 14:03:44',NULL,NULL,NULL,NULL),(94,'新用户优惠券','属性','3','新用户优惠券',5,91,0,'2021-04-22 14:03:59',NULL,NULL,NULL,NULL),(95,'可兑换优惠券','属性','8','可兑换优惠券',5,91,0,'2021-04-22 14:04:14',NULL,NULL,NULL,NULL),(96,'省钱小能手','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,coupon_price,groupon_price,goods_price\nwhereFieldNames=add_time#month#6\noutFields=discountRate','订单中的省钱能手（折扣）：3折~4折、5折~7折、8折~9折',4,4,0,'2021-04-23 02:12:51','2021-07-14 14:30:37',1,NULL,'discountRate'),(97,'3折-4折','属性','0.3-0.4','3折~4折时必买',5,96,0,'2021-04-23 02:13:25',NULL,NULL,NULL,NULL),(98,'5折-7折','属性','0.5-0.7','5折~7折时必买',5,96,0,'2021-04-23 02:13:40',NULL,NULL,NULL,NULL),(99,'8折-9折','属性','0.8-0.9','8折-9折时必买',5,96,0,'2021-04-23 02:13:56',NULL,NULL,NULL,NULL),(100,'用户价值','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,order_sn,actual_price,end_time\noutFields=rfm','评估用户价值',4,4,0,'2021-04-28 06:35:50','2021-07-14 14:31:18',1,NULL,'rfm'),(101,'重要价值客户','属性','0','最近消费时间近、消费频次和消费金额都很高',5,100,0,'2021-04-29 03:45:37',NULL,NULL,NULL,NULL),(102,'重要保持客户','属性','1','最近消费时间较远，但消费频次和金额都很高，说明这是个一段时间没来的忠诚客户，我们需要主动和他保持联系',5,100,0,'2021-04-29 03:46:34',NULL,NULL,NULL,NULL),(103,'重要发展客户','属性','2','最近消费时间较近、消费金额高，但频次不高，忠诚度不高，很有潜力的用户，必须重点发展',5,100,0,'2021-04-29 03:48:20',NULL,NULL,NULL,NULL),(104,'重要挽留客户','属性','3','最近消费时间较远、消费频次不高，但消费金额高的用户，可能是将要流失或者已经要流失的用户，应当给予挽留措施',5,100,0,'2021-04-29 03:48:37',NULL,NULL,NULL,NULL),(105,'一般价值用户','属性','4','最近消费时间较近、频次高，但消费金额不高',5,100,0,'2021-04-29 03:50:37',NULL,NULL,NULL,NULL),(106,'一般发展用户','属性','5','最近消费时间较近、但消费金额高，频次不高',5,100,0,'2021-04-29 03:51:28',NULL,NULL,NULL,NULL),(107,'一般保持用户','属性','6','最近消费时间较远，金额不高，但消费频次高',5,100,0,'2021-04-29 03:53:03',NULL,NULL,NULL,NULL),(108,'一般挽留用户','属性','7','最近消费时间较远、消费频次不高，消费金额也不高的用户',5,100,0,'2021-04-29 03:54:06',NULL,NULL,NULL,NULL),(109,'促销敏感度','标签','inType=hbase\nzkHosts=cdh-master\nzkPort=2181\nhbaseNamespace=litemall\nhbaseTable=litemall_order\nrowKey=id\nfamily=cf\nselectFieldNames=user_id,order_price,actual_price,coupon_price,integral_price,groupon_price\noutFields=psm','通过该模型，可以得到产品的最优价格和合理的价格区间',4,4,0,'2021-04-29 08:52:39','2021-07-14 14:31:32',1,NULL,'psm'),(110,'极度敏感','属性','>=1','含促销商品的订单量和金额占比非常高',5,109,0,'2021-04-29 08:54:19',NULL,NULL,NULL,NULL),(111,'比较敏感','属性','0.4~1','比较敏感：含促销商品的订单量和金额占比较高',5,109,0,'2021-04-29 08:54:47',NULL,NULL,NULL,NULL),(112,'一般敏感','属性','0.1~0.3','一般敏感：含促销商品的订单量和金额占比中等',5,109,0,'2021-04-29 08:55:14',NULL,NULL,NULL,NULL),(113,'不太敏感','属性','0','不太敏感：含促销商品的订单量和金额占比中等',5,109,0,'2021-04-29 08:55:37',NULL,NULL,NULL,NULL),(114,'极度不敏感','属性','<0','极度不敏感:含促销商品的订单量和金额占比很小',5,109,0,'2021-04-29 08:55:53',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `tb_basic_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_merge_tag_detail`
--

DROP TABLE IF EXISTS `tb_merge_tag_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_merge_tag_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `merge_tag_id` bigint(20) NOT NULL COMMENT '组合标签ID',
  `basic_tag_id` bigint(20) NOT NULL COMMENT '基础标签ID（1级行业 or 5级属性）',
  `condition` int(11) NOT NULL COMMENT '条件间关系： 1 and 2 or 3 not',
  `condition_order` int(11) NOT NULL DEFAULT '0' COMMENT '条件顺序',
  `remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_tb_merge_tag_detail_tb_merge_tag` (`merge_tag_id`),
  KEY `fk_tb_merge_tag_detail_tb_basic_tag` (`basic_tag_id`),
  CONSTRAINT `fk_tb_merge_tag_detail_tb_basic_tag` FOREIGN KEY (`basic_tag_id`) REFERENCES `tb_basic_tag` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_tb_merge_tag_detail_tb_merge_tag` FOREIGN KEY (`merge_tag_id`) REFERENCES `tb_merge_tag` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组合标签规则详情';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_merge_tag_detail`
--

LOCK TABLES `tb_merge_tag_detail` WRITE;
/*!40000 ALTER TABLE `tb_merge_tag_detail` DISABLE KEYS */;
INSERT INTO `tb_merge_tag_detail` VALUES (1,1,9,1,1,NULL,'2021-07-14 11:44:01',NULL),(2,1,50,2,2,NULL,'2021-07-14 11:44:01',NULL),(3,1,65,1,3,NULL,'2021-07-14 11:44:01',NULL),(4,1,101,1,4,NULL,'2021-07-14 11:44:01',NULL),(5,2,19,1,1,NULL,'2021-07-14 11:51:00',NULL),(6,2,64,1,2,NULL,'2021-07-14 11:51:00',NULL),(7,2,50,1,3,NULL,'2021-07-14 11:51:00',NULL);
/*!40000 ALTER TABLE `tb_merge_tag_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_merge_tag`
--

DROP TABLE IF EXISTS `tb_merge_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_merge_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组合标签名称',
  `condition` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组合标签条件',
  `intro` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组合标签含义',
  `purpose` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组合用途',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  `state` int(11) DEFAULT NULL COMMENT '状态：1申请中、2开发中、3开发完成、4已上线、5已下线、6已禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组合标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_merge_tag`
--

LOCK TABLES `tb_merge_tag` WRITE;
/*!40000 ALTER TABLE `tb_merge_tag` DISABLE KEYS */;
INSERT INTO `tb_merge_tag` VALUES (1,'测试','t re','隋东风','发生地方',NULL,'2021-07-14 11:44:01',NULL,4),(2,'白富美','白富美','白富美','白富美',NULL,'2021-07-14 11:51:00',NULL,4);
/*!40000 ALTER TABLE `tb_merge_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_tag_metadata`
--

DROP TABLE IF EXISTS `tb_tag_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_tag_metadata` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `in_type` int(11) NOT NULL COMMENT '数据源类型： 1 RDBMS 2 File 3 Hbase 4 Hive',
  `driver` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'RDBMS数据库驱动',
  `url` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'RDBMS数据库连接地址',
  `user` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'RDBMS数据库用户名',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'RDBMS数据库密码',
  `db_table` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'RDBMS数据库表名',
  `query_sql` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查询的sql语句',
  `in_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件地址',
  `sperator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分隔符',
  `out_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理后输出的文件地址',
  `zk_hosts` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'zookeeper主机地址, 格式： host:port',
  `zk_port` int(11) DEFAULT NULL COMMENT 'zookeeper主机端口号',
  `hbase_namespace` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'hBase 表的 namespace',
  `hbase_table` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Hbase数据源中的表名',
  `row_key` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'hBase 的rowkey',
  `family` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'hbase数据源列簇',
  `select_field_names` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查询结果集中的列名，采用","分隔',
  `where_field_names` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查询where 字段',
  `where_field_values` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查询where 字段值',
  `out_fields` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理之后的输出字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `state` int(11) DEFAULT NULL COMMENT '状态',
  `remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `fk_tb_tag_metadata_tb_basic_tag` (`tag_id`),
  CONSTRAINT `fk_tb_tag_metadata_tb_basic_tag` FOREIGN KEY (`tag_id`) REFERENCES `tb_basic_tag` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签数据元数据信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_tag_metadata`
--

LOCK TABLES `tb_tag_metadata` WRITE;
/*!40000 ALTER TABLE `tb_tag_metadata` DISABLE KEYS */;
INSERT INTO `tb_tag_metadata` VALUES (4,7,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_user','id','cf','id,gender',NULL,NULL,'gender','2021-03-26 10:07:31','2021-07-14 14:24:58',NULL,NULL),(6,8,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_user','id','cf','id,birthday',NULL,NULL,'age','2021-03-26 10:09:28','2021-07-14 14:25:24',NULL,NULL),(7,43,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,end_time',NULL,NULL,'consumption','2021-03-26 10:27:04','2021-07-14 14:27:43',NULL,NULL),(12,54,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,order_status,actual_price','end_time#month#6',NULL,'consumptionAbility','2021-04-21 02:49:19','2021-07-14 14:27:09',NULL,NULL),(13,55,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,order_status,order_price','add_time#month#6',NULL,'pct','2021-04-21 02:50:03','2021-07-14 14:28:32',NULL,NULL),(15,57,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,order_price','add_time#month#6',NULL,'singleOrderMax','2021-04-21 02:51:08','2021-07-14 14:28:56',NULL,NULL),(16,58,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,id','add_time#month#6',NULL,'orderFrequency','2021-04-21 02:51:45','2021-07-14 14:29:10',NULL,NULL),(17,87,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,order_status','add_time#month#6',NULL,'refundRate','2021-04-22 01:47:03','2021-07-14 14:29:41',NULL,NULL),(18,91,1,'com.mysql.jdbc.Driver','jdbc:mysql://storage:3306/litemall','litemall','litemall123456','litemall_coupon_user',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'coupon','2021-04-22 14:03:02','2021-07-14 14:30:13',NULL,NULL),(19,96,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,coupon_price,groupon_price,goods_price','add_time#month#6',NULL,'discountRate','2021-04-23 02:12:51','2021-07-14 14:30:37',NULL,NULL),(20,100,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,order_sn,actual_price,end_time',NULL,NULL,'rfm','2021-04-28 06:35:50','2021-07-14 14:31:18',NULL,NULL),(21,109,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'cdh-master',2181,'litemall','litemall_order','id','cf','user_id,order_price,actual_price,coupon_price,integral_price,groupon_price',NULL,NULL,'psm','2021-04-29 08:52:39','2021-07-14 14:31:32',NULL,NULL);
/*!40000 ALTER TABLE `tb_tag_metadata` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-08-08 16:42:47
