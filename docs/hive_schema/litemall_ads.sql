use litemall;

-- ads_uv_count
drop table if exists ads_uv_count;
create external table ads_uv_count(
    `dt` string COMMENT '统计日期',
    `day_count` bigint COMMENT '当日用户数量',
    `wk_count` bigint COMMENT '当周用户数量',
    `mn_count` bigint COMMENT '当月用户数量',
    `is_weekend` string COMMENT 'Y,N 是否是周末,用于得到本周最终结果',
    `is_monthend` string COMMENT 'Y,N 是否是月末,用于得到本月最终结果'
) COMMENT '活跃设备数'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_uv_count';

-- ads_new_mid_count
drop table if exists ads_new_mid_count;
create external table ads_new_mid_count(
    `dt` string comment '创建时间' ,
    `new_mid_count` BIGINT comment '新增设备数量'
) COMMENT '每日新增设备信息数量'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_new_mid_count';

-- ads_silent_count
drop table if exists ads_silent_count;
create external table ads_silent_count(
    `dt` string COMMENT '统计日期',
    `silent_count` bigint COMMENT '沉默设备数'
)comment '沉默用户数'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_silent_count';

-- ads_back_count
drop table if exists ads_back_count;
create external table ads_back_count(
    `dt` string COMMENT '统计日期',
    `wk_dt` string COMMENT '统计日期所在周',
    `wastage_count` bigint COMMENT '回流设备数'
)comment '本周回流用户数'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_back_count';

-- ads_wastage_count
drop table if exists ads_wastage_count;
create external table ads_wastage_count(
    `dt` string COMMENT '统计日期',
    `wastage_count` bigint COMMENT '流失设备数'
)comment '流失用户数'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_wastage_count';

-- ads_user_retention_day_rate
drop table if exists ads_user_retention_day_rate;
create external table ads_user_retention_day_rate(
    `stat_date` string comment '统计日期',
    `create_date` string comment '设备新增日期',
    `retention_day` int comment '截止当前日期留存天数',
    `new_mid_count` bigint comment '设备新增数量',
    `retention_count` bigint comment '留存数量',
    `retention_ratio` decimal(10,2) comment '留存率'
) COMMENT '每日用户留存情况'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_user_retention_day_rate';

-- ads_continuity_wk_count
drop table if exists ads_continuity_wk_count;
create external table ads_continuity_wk_count(
    `dt` string COMMENT '统计日期,一般用结束周周日日期,如果每天计算一次,可用当天日期',
    `wk_dt` string COMMENT '持续时间',
    `continuity_count` bigint COMMENT '活跃次数'
)comment '最近连续三周活跃用户数'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_continuity_wk_count';

-- ads_continuity_uv_count
drop table if exists ads_continuity_uv_count;
create external table ads_continuity_uv_count(
    `dt` string COMMENT '统计日期',
    `wk_dt` string COMMENT '最近 7 天日期',
    `continuity_count` bigint
) COMMENT '最近七天连续三天活跃用户数'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_continuity_uv_count';

-- ads_user_topic
drop table if exists ads_user_topic;
create external table ads_user_topic(
    `dt` string COMMENT '统计日期',
    `day_users` bigint COMMENT '活跃会员数',
    `day_new_users` bigint COMMENT '新增会员数',
    `day_new_payment_users` bigint COMMENT '新增消费会员数',
    `payment_users` bigint COMMENT '总付费会员数',
    `users` bigint COMMENT '总会员数',
    `day_users2users` decimal(10,2) COMMENT '会员活跃率',
    `payment_users2users` decimal(10,2) COMMENT '会员付费率',
    `day_new_users2users` decimal(10,2) COMMENT '会员新鲜度'
) COMMENT '会员主题信息表'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_user_topic';

-- 前一天数据统计
-- ads_user_action_convert_day
drop table if exists ads_user_action_convert_day;
create external table ads_user_action_convert_day(
    `dt` string COMMENT '统计日期',
    `total_visitor_m_count` bigint COMMENT '总访问人数',
    `cart_u_count` bigint COMMENT '加入购物车的人数',
    `visitor2cart_convert_ratio` decimal(10,2) COMMENT '访问到加入购物车转化率',
    `order_u_count` bigint COMMENT '下单人数',
    `cart2order_convert_ratio` decimal(10,2) COMMENT '加入购物车到下单转化率',
    `payment_u_count` bigint COMMENT '支付人数',
    `order2payment_convert_ratio` decimal(10,2) COMMENT '下单到支付的转化率'
) COMMENT '用户行为漏斗分析'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_user_action_convert_day';

-- ads_product_info
drop table if exists ads_product_info;
create external table ads_product_info(
    `dt` string COMMENT '统计日期',
    `sku_num` bigint COMMENT 'sku 个数',
    `spu_num` bigint COMMENT 'spu 个数'
) COMMENT '商品个数信息'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_product_info';

-- ads_product_sale_topN
drop table if exists ads_product_sale_topN;
create external table ads_product_sale_topN(
    `stat_date` string COMMENT '统计日期',
    `sku_id` int COMMENT '商品 ID',
    `payment_count` bigint COMMENT '销量'
) COMMENT '商品个数信息'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_product_sale_topN';

-- ads_product_favor_topN
drop table if exists ads_product_favor_topN;
create external table ads_product_favor_topN(
    `stat_date` string COMMENT '统计日期',
    `sku_id` int COMMENT '商品 ID',
    `favor_count` bigint COMMENT '收藏量'
) COMMENT '商品收藏 TopN'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_product_favor_topN';

-- ads_product_cart_topN
drop table if exists ads_product_cart_topN;
create external table ads_product_cart_topN(
    `stat_date` string COMMENT '统计日期',
    `sku_id` int COMMENT '商品 ID',
    `cart_num` bigint COMMENT '加入购物车数量'
) COMMENT '商品加入购物车 TopN'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_product_cart_topN';

-- ads_product_refund_topN(最近30天)
drop table if exists ads_product_refund_topN;
create external table ads_product_refund_topN(
    `stat_date` string COMMENT '统计日期',
    `sku_id` int COMMENT '商品 ID',
    `refund_ratio` decimal(10,2) COMMENT '退款率'
) COMMENT '商品退款率 TopN'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_product_refund_topN';

-- ads_appraise_bad_topN
drop table if exists ads_appraise_bad_topN;
create external table ads_appraise_bad_topN(
    `stat_date` string COMMENT '统计日期',
    `sku_id` int COMMENT '商品 ID',
    `appraise_bad_ratio` decimal(10,2) COMMENT '差评率'
) COMMENT '商品差评率 TopN'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_appraise_bad_topN';

-- 统计每日下单数，下单金额及下单用户数
-- ads_order_daycount
drop table if exists ads_order_daycount;
create external table ads_order_daycount(
    `dt` string comment '统计日期',
    `order_count` bigint comment '单日下单笔数',
    `order_amount` bigint comment '单日下单金额',
    `order_users` bigint comment '单日下单用户数'
) comment '每日订单总计表'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_order_daycount';

-- 每日支付金额、支付人数、支付商品数、支付笔数以及下单到支付的平均时长(取自 DWD)
-- ads_payment_daycount
drop table if exists ads_payment_daycount;
create external table ads_payment_daycount(
    `dt` string comment '统计日期',
    `payment_count` bigint comment '单日支付笔数',
    `payment_amount` bigint comment '单日支付金额',
    `payment_user_count` bigint comment '单日支付人数',
    `payment_sku_count` bigint comment '单日支付商品数',
    `payment_avg_time` double comment '下单到支付的平均时长，取分钟数'
) comment '每日支付总计表'
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_payment_daycount';

-- ads_sale_brand_category1_stat_mn
drop table ads_sale_brand_category1_stat_mn;
create external table ads_sale_brand_category1_stat_mn (
    `brand_id` int comment '品牌 id',
    `category1_id` int comment '1级品类id ',
    `category1_name` string comment '1 级品类名称 ',
    `buycount` bigint comment '购买人数',
    `buy_twice_last` bigint comment '两次以上购买人数',
    `buy_twice_last_ratio` decimal(10,2) comment '单次复购率',
    `buy_3times_last` bigint comment '三次以上购买人数',
    `buy_3times_last_ratio` decimal(10,2) comment '多次复购率',
    `stat_mn` string comment '统计月份',
    `stat_date` string comment '统计日期'
) COMMENT'复购率统计'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
location '/warehouse/litemall/ads/ads_sale_brand_category1_stat_mn';

-- ads_date_topic
drop table if exists ads_date_topic;
create external table ads_date_topic(
	`date` string comment '统计日期',
    `week_id` int COMMENT '周',
    `week_day` int COMMENT '周的第几天',
    `day` int COMMENT '每月的第几天',
    `month` int COMMENT '第几月',
    `quarter` int COMMENT '第几季度',
    `year` int COMMENT '年',
    `is_workday` int COMMENT '是否是周末',
    `holiday_id` int COMMENT '是否是节假日',
	`uv_count` bigint comment '活跃用户数',
	`register_count` bigint comment '新增用户数',
	`cart_count` bigint comment '加购数量',
	`comment_count` bigint comment '评论次数',
	`collect_count` bigint comment '收藏次数',
	`order_count` bigint comment '下单次数',
	`order_total_amount` decimal(10,2) comment '下单总金额',
	`payment_count` bigint comment '支付次数',
	`payment_total_amount` decimal(10,2) comment '支付金额',
	`refund_count` bigint comment '退单次数',
	`refund_total_amount` decimal(10,2) comment '退单金额',
	`coupon_count` bigint comment '领用优惠卷次数'
)comment '每日数据总汇表'
row format delimited fields terminated by '\t'
LOCATION '/warehouse/litemall/ads/ads_date_topic';

-- 修改 ads_payment_daycount的payment_amount的数据类型
alter table ads_payment_daycount CHANGE `payment_amount` `payment_amount` decimal(10,2) comment '单日支付金额';

-- ads_date_topic 添加列
alter table ads_date_topic add columns(
  `payment_user_count` bigint comment '单日支付人数',
  `payoff` decimal(10,2) comment '单日销售额',
  `payoff_user_count` bigint comment '单日成交顾客数'
);