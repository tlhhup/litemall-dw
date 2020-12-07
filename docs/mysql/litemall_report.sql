# ************************************************************
# Sequel Pro SQL dump
# Version 5446
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: storage (MySQL 5.7.31)
# Database: litemall_report
# Generation Time: 2020-12-07 06:23:05 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table ads_appraise_bad_topN
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_appraise_bad_topN`;

CREATE TABLE `ads_appraise_bad_topN` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期',
  `sku_id` int(11) DEFAULT NULL COMMENT '商品 ID',
  `appraise_bad_ratio` decimal(10,2) DEFAULT NULL COMMENT '差评率'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品差评率 TopN';



# Dump of table ads_back_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_back_count`;

CREATE TABLE `ads_back_count` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `wk_dt` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期所在周',
  `wastage_count` bigint(20) DEFAULT NULL COMMENT '回流设备数',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本周回流用户数';



# Dump of table ads_continuity_uv_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_continuity_uv_count`;

CREATE TABLE `ads_continuity_uv_count` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `wk_dt` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最近 7 天日期',
  `continuity_count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='最近七天连续三天活跃用户数';



# Dump of table ads_continuity_wk_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_continuity_wk_count`;

CREATE TABLE `ads_continuity_wk_count` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期,一般用结束周周日日期,如果每天计算一次,可用当天日期',
  `wk_dt` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '持续时间',
  `continuity_count` bigint(20) DEFAULT NULL COMMENT '活跃次数',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='最近连续三周活跃用户数';



# Dump of table ads_date_topic
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_date_topic`;

CREATE TABLE `ads_date_topic` (
  `date` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `week_id` int(11) DEFAULT NULL COMMENT '周',
  `week_day` int(11) DEFAULT NULL COMMENT '周的第几天',
  `day` int(11) DEFAULT NULL COMMENT '每月的第几天',
  `month` int(11) DEFAULT NULL COMMENT '第几月',
  `quarter` int(11) DEFAULT NULL COMMENT '第几季度',
  `year` int(11) DEFAULT NULL COMMENT '年',
  `is_workday` int(11) DEFAULT NULL COMMENT '是否是周末',
  `holiday_id` int(11) DEFAULT NULL COMMENT '是否是节假日',
  `uv_count` bigint(20) DEFAULT NULL COMMENT '活跃用户数',
  `register_count` bigint(20) DEFAULT NULL COMMENT '新增用户数',
  `cart_count` bigint(20) DEFAULT NULL COMMENT '加购数量',
  `comment_count` bigint(20) DEFAULT NULL COMMENT '评论次数',
  `collect_count` bigint(20) DEFAULT NULL COMMENT '收藏次数',
  `order_count` bigint(20) DEFAULT NULL COMMENT '下单次数',
  `order_total_amount` decimal(10,2) DEFAULT NULL COMMENT '下单总金额',
  `payment_count` bigint(20) DEFAULT NULL COMMENT '支付次数',
  `payment_total_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `refund_count` bigint(20) DEFAULT NULL COMMENT '退单次数',
  `refund_total_amount` decimal(10,2) DEFAULT NULL COMMENT '退单金额',
  `coupon_count` bigint(20) DEFAULT NULL COMMENT '领用优惠卷次数',
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日数据总汇表';



# Dump of table ads_new_mid_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_new_mid_count`;

CREATE TABLE `ads_new_mid_count` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `new_mid_count` bigint(20) DEFAULT NULL COMMENT '新增设备数量',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日新增设备信息数量';



# Dump of table ads_order_daycount
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_order_daycount`;

CREATE TABLE `ads_order_daycount` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `order_count` bigint(20) DEFAULT NULL COMMENT '单日下单笔数',
  `order_amount` bigint(20) DEFAULT NULL COMMENT '单日下单金额',
  `order_users` bigint(20) DEFAULT NULL COMMENT '单日下单用户数',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日订单总计表';



# Dump of table ads_payment_daycount
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_payment_daycount`;

CREATE TABLE `ads_payment_daycount` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `payment_count` bigint(20) DEFAULT NULL COMMENT '单日支付笔数',
  `payment_amount` bigint(20) DEFAULT NULL COMMENT '单日支付金额',
  `payment_user_count` bigint(20) DEFAULT NULL COMMENT '单日支付人数',
  `payment_sku_count` bigint(20) DEFAULT NULL COMMENT '单日支付商品数',
  `payment_avg_time` double DEFAULT NULL COMMENT '下单到支付的平均时长，取分钟数',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日支付总计表';



# Dump of table ads_product_cart_topN
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_product_cart_topN`;

CREATE TABLE `ads_product_cart_topN` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期',
  `sku_id` int(11) DEFAULT NULL COMMENT '商品 ID',
  `cart_num` bigint(20) DEFAULT NULL COMMENT '加入购物车数量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品加入购物车 TopN';



# Dump of table ads_product_favor_topN
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_product_favor_topN`;

CREATE TABLE `ads_product_favor_topN` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期',
  `sku_id` int(11) DEFAULT NULL COMMENT '商品 ID',
  `favor_count` bigint(20) DEFAULT NULL COMMENT '收藏量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品收藏 TopN';



# Dump of table ads_product_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_product_info`;

CREATE TABLE `ads_product_info` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `sku_num` bigint(20) DEFAULT NULL COMMENT 'sku 个数',
  `spu_num` bigint(20) DEFAULT NULL COMMENT 'spu 个数',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品个数信息';



# Dump of table ads_product_refund_topN
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_product_refund_topN`;

CREATE TABLE `ads_product_refund_topN` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期',
  `sku_id` int(11) DEFAULT NULL COMMENT '商品 ID',
  `refund_ratio` decimal(10,2) DEFAULT NULL COMMENT '退款率'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品退款率 TopN';



# Dump of table ads_product_sale_topN
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_product_sale_topN`;

CREATE TABLE `ads_product_sale_topN` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期',
  `sku_id` int(11) DEFAULT NULL COMMENT '商品 ID',
  `payment_count` bigint(20) DEFAULT NULL COMMENT '销量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品个数信息';



# Dump of table ads_sale_brand_category1_stat_mn
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_sale_brand_category1_stat_mn`;

CREATE TABLE `ads_sale_brand_category1_stat_mn` (
  `brand_id` int(11) DEFAULT NULL COMMENT '品牌 id',
  `category1_id` int(11) DEFAULT NULL COMMENT '1级品类id ',
  `category1_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '1 级品类名称 ',
  `buycount` bigint(20) DEFAULT NULL COMMENT '购买人数',
  `buy_twice_last` bigint(20) DEFAULT NULL COMMENT '两次以上购买人数',
  `buy_twice_last_ratio` decimal(10,2) DEFAULT NULL COMMENT '单次复购率',
  `buy_3times_last` bigint(20) DEFAULT NULL COMMENT '三次以上购买人数',
  `buy_3times_last_ratio` decimal(10,2) DEFAULT NULL COMMENT '多次复购率',
  `stat_mn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计月份',
  `stat_date` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='品牌复购率统计';



# Dump of table ads_silent_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_silent_count`;

CREATE TABLE `ads_silent_count` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `silent_count` bigint(20) DEFAULT NULL COMMENT '沉默设备数',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沉默用户数';



# Dump of table ads_user_action_convert_day
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_user_action_convert_day`;

CREATE TABLE `ads_user_action_convert_day` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `total_visitor_m_count` bigint(20) DEFAULT NULL COMMENT '总访问人数',
  `cart_u_count` bigint(20) DEFAULT NULL COMMENT '加入购物车的人数',
  `visitor2cart_convert_ratio` decimal(10,2) DEFAULT NULL COMMENT '访问到加入购物车转化率',
  `order_u_count` bigint(20) DEFAULT NULL COMMENT '下单人数',
  `cart2order_convert_ratio` decimal(10,2) DEFAULT NULL COMMENT '加入购物车到下单转化率',
  `payment_u_count` bigint(20) DEFAULT NULL COMMENT '支付人数',
  `order2payment_convert_ratio` decimal(10,2) DEFAULT NULL COMMENT '下单到支付的转化率',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为漏斗分析';



# Dump of table ads_user_retention_day_rate
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_user_retention_day_rate`;

CREATE TABLE `ads_user_retention_day_rate` (
  `stat_date` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统计日期',
  `create_date` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备新增日期',
  `retention_day` int(11) DEFAULT NULL COMMENT '截止当前日期留存天数',
  `new_mid_count` bigint(20) DEFAULT NULL COMMENT '设备新增数量',
  `retention_count` bigint(20) DEFAULT NULL COMMENT '留存数量',
  `retention_ratio` decimal(10,2) DEFAULT NULL COMMENT '留存率'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日用户留存情况';



# Dump of table ads_user_topic
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_user_topic`;

CREATE TABLE `ads_user_topic` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `day_users` bigint(20) DEFAULT NULL COMMENT '活跃会员数',
  `day_new_users` bigint(20) DEFAULT NULL COMMENT '新增会员数',
  `day_new_payment_users` bigint(20) DEFAULT NULL COMMENT '新增消费会员数',
  `payment_users` bigint(20) DEFAULT NULL COMMENT '总付费会员数',
  `users` bigint(20) DEFAULT NULL COMMENT '总会员数',
  `day_users2users` decimal(10,2) DEFAULT NULL COMMENT '会员活跃率',
  `payment_users2users` decimal(10,2) DEFAULT NULL COMMENT '会员付费率',
  `day_new_users2users` decimal(10,2) DEFAULT NULL COMMENT '会员新鲜度',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员主题信息表';



# Dump of table ads_uv_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_uv_count`;

CREATE TABLE `ads_uv_count` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `day_count` bigint(20) DEFAULT NULL COMMENT '当日用户数量',
  `wk_count` bigint(20) DEFAULT NULL COMMENT '当周用户数量',
  `mn_count` bigint(20) DEFAULT NULL COMMENT '当月用户数量',
  `is_weekend` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Y,N 是否是周末,用于得到本周最终结果',
  `is_monthend` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Y,N 是否是月末,用于得到本月最终结果',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活跃设备数';



# Dump of table ads_wastage_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ads_wastage_count`;

CREATE TABLE `ads_wastage_count` (
  `dt` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计日期',
  `wastage_count` bigint(20) DEFAULT NULL COMMENT '流失设备数',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流失用户数';




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
