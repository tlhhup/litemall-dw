use litemall;

-- 行为数据
-- dws_uv_detail_daycount start_log
drop table if exists dws_uv_detail_daycount; 
create external table dws_uv_detail_daycount (
	`mid` string comment '设备id',
	`uid` array<int> comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`login_count` bigint COMMENT '活跃次数' 
)comment 'uv详情表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dws/dws_uv_detail_daycount'
TBLPROPERTIES ('parquet.compression'='lzo');

-- 业务数据
-- dws_region_detail_daycount
drop table if exists dws_region_detail_daycount;
create external table dws_region_detail_daycount(
  	`province` int COMMENT '省份ID',
  	`city` int COMMENT '城市ID',
  	`country` int COMMENT '乡镇ID',
	`order_count` bigint comment '下单次数',
	`order_total_amount` decimal(10,2) comment '下单总金额',
	`payment_count` bigint comment '支付次数',
	`payment_total_amount` decimal(10,2) comment '支付金额',
	`refund_count` bigint comment '退单次数',
	`refund_total_amount` decimal(10,2) comment '退单金额'
)comment '每日地域数据总汇表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dws/dws_region_detail_daycount'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dws_user_action_daycount
drop table if exists dws_user_action_daycount;
create external table dws_user_action_daycount(
	`user_id` int comment '用户ID',
	`login_count` bigint comment '登陆次数',
	`cart_count` bigint comment '加购数量',
	`cart_amount` decimal(10,2) comment '加购金额',
	`comment_count` bigint comment '评论次数',
	`collect_count` bigint comment '收藏次数',
	`order_count` bigint comment '下单次数',
	`order_total_amount` decimal(10,2) comment '下单总金额',
	`payment_count` bigint comment '支付次数',
	`payment_total_amount` decimal(10,2) comment '支付金额',
	`refund_count` bigint comment '退单次数',
	`refund_total_amount` decimal(10,2) comment '退单金额',
	`coupon_count` bigint comment '领用优惠卷次数'
)comment '每日用户行为表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dws/dws_user_action_daycount'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dws_goods_action_daycount
drop table if exists dws_goods_action_daycount;
create external table dws_goods_action_daycount(
	`sku_id` int comment '商品 sku ID',
	`cart_count` bigint comment '加购次数',
	`cart_num` bigint comment '加购数量',
	`order_count` bigint comment '下单次数',
	`order_num` bigint comment '下单件数',
	`order_total_amount` decimal(10,2) comment '下单总金额',
	`payment_count` bigint comment '支付次数',
	`payment_num` bigint comment '支付件数',
	`payment_total_amount` decimal(10,2) comment '支付金额',
	`refund_count` bigint comment '退单次数',
	`refund_num` bigint comment '退单件数',
	`refund_total_amount` decimal(10,2) comment '退单金额',
	`collect_count` bigint comment '收藏次数',
	`comment_count` bigint comment '评论次数',
	`appraise_good_count` bigint comment '好评数', 
	`appraise_mid_count` bigint comment '中评数',
	`appraise_bad_count` bigint comment '差评数', 
	`appraise_default_count` bigint comment '默认评价数'
)comment '每日商品行为表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dws/dws_goods_action_daycount'
TBLPROPERTIES ('parquet.compression'='lzo');

-- 用户的购买行为
-- dws_goods_sale_detail_daycount
drop table if exists dws_goods_sale_detail_daycount; 
create external table dws_goods_sale_detail_daycount (
	`user_id` int comment '用户ID',
	`sku_id` int comment '商品 sku id',
	`user_gender` string comment '用户性别',
	`user_age` int comment '用户年龄',
	`user_level` tinyint comment '用户等级',
	`goods_name` string comment '商品名称',
	`goods_brand_id` int comment '品牌id',
	`goods_brand_name` string comment '品牌名称',
	`goods_category1_id` int comment '商品一级品类ID',
	`goods_category1_name` string comment '商品一级品类名称',
	`goods_category2_id` int comment '商品二级品类ID',
	`goods_category2_name` string comment '商品二级品类名称',
	`goods_product_id` int comment '商品 spu',
	`goods_num` int comment '购买个数',
	`goods_price` decimal(10,2) comment '商品价格',
	`order_count` bigint comment '当日下单单数',
	`order_amount` decimal(16,2) comment '当日下单金额'
) comment '每日购买行为'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dws/dws_goods_sale_detail_daycount'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dws_coupon_daycount
drop table if exists dws_coupon_daycount;
create external table dws_coupon_daycount(
    `id` int comment '优惠卷ID',
    `name` string COMMENT '优惠券名称',
    `get_count` bigint comment '领用数量',
    `used_count` bigint comment '使用数量'
)comment '每日优惠卷情况'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dws/dws_coupon_daycount'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dws_groupon_info_daycount
drop table if exists dws_groupon_info_daycount; 
create external table dws_groupon_info_daycount(
	`id` int comment '编号', 
	`groupon_name` string comment '活动名称',
	`start_time` string comment '开始时间', 
	`end_time` string comment '结束时间',
	`create_time` string comment '创建时间', 
	`order_count` bigint comment '下单次数',
	`payment_count` bigint comment '支付次数' 
) comment '团购表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dws/dws_groupon_info_daycount'
TBLPROPERTIES ('parquet.compression'='lzo');