use litemall;

-- 该层属于累积性数据表，不进行分区处理
-- dwt_uv_topic
drop table if exists dwt_uv_topic;
create external table dwt_uv_topic(
	`mid` string comment '设备id',
	`uid` array<string> comment '用户id',
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
	`first_date_login` string comment '首次登录时间',
	`last_date_login` string comment '最近登录时间',
	`login_count` bigint comment '累计活跃次数',
	`login_day_count` bigint comment '当日活跃次数'
)comment 'uv主题宽表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwt/dwt_uv_topic'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwt_region_topic
drop table if exists dwt_region_topic;
create external table dwt_region_topic(
	`province` int COMMENT '省份ID',
  	`city` int COMMENT '城市ID',
  	`country` int COMMENT '乡镇ID',
  	`order_date_first` string comment '首次下单时间',
  	`order_date_last` string comment '最近下单时间',
  	`order_count` bigint comment '累计下单总数',
  	`order_amount` decimal(10,2) comment '累计下单金额'
  	`order_day_count` bigint comment '当日下单总数',
  	`order_day_amount` decimal(10,2) '当日下单金额',
  	`payment_amount` decimal(10,2) comment '累计支付金额',
  	`payment_day_amount` decimal(10,2) comment '当日支付金额',
  	`refund_amount` decimal(10,2) comment '累计退单金额',
  	`refund_day_amount` decimal(10,2) comment '当日退单金额' 
)comment '地域主题宽表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwt/dwt_uv_topic'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwt_user_topic
drop table if exists dwt_user_topic;
create external table dwt_user_topic(
	`user_id` int comment '用户ID',
	`login_first_date` string comment '首次登录时间',
	`login_last_date` string comment '最近登录时间',
	`login_count` bigint comment '累计登录次数',
	`login_30_days_count` bigint comment '近30天登录次数',
	`order_first_date` string comment '首次下单时间',
	`order_last_date` string comment '最近下单时间',
	`order_count` bigint comment '累计下单次数',
	`order_amount` decimal(10,2) comment '累计下单金额',
	`order_30_days_count` bigint comment '近30天下单次数'，
	`order_30_days_amount` decimal(10,2) comment '近30天下单金额',
	`payment_first_date` string comment '首次支付时间',
	`payment_last_date` string comment '最近支付时间',
	`payment_count` bigint comment '累计支付次数',
	`payment_amount` decimal(10,2) comment '累计支付金额',
	`payment_30_days_count` bigint comment '近30天支付次数',
	`payment_30_days_amount` decimal(10,2) comment '近30天支付金额',
	`refund_first_date` string comment '首次退单时间',
	`refund_last_date` string comment '最近退单时间',
	`refund_count` bigint comment '累计退单次数',
	`refund_amount` decimal(10,2) '累计退单金额',
	`refund_30_days_count` bigint comment '近30天退单次数',
	`refund_30_days_amount` decimal(10,2) comment '近30天退单金额'
)comment '用户主题宽表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwt/dwt_user_topic'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwt_sku_topic
drop table if exists dwt_sku_topic;
create external table dwt_sku_topic(
	`sku_id` int comment '商品ID',
	`spu_id` int comment '库存ID',
	`order_count` bigint comment '累计被下单次数',
	`order_amount` decimal(10,2) comment '累计被下单金额',
	`order_num` bigint comment '累计被下单件数',
	`order_30_days_count` bigint comment '近30天被下单次数',
	`order_30_days_amount` decimal(10,2) comment '近30天被下单金额',
	`order_30_days_num` bigint comment '近30天被下单件数',
	`payment_count` bigint comment '累计被支付次数',
	`payment_amount` decimal(10,2) comment '累计被支付金额',
	`payment_30_days_count` bigint comment '近30天被支付次数',
	`payment_30_days_amount` decimal(10,2) comment '近30天被支付金额',
	`refund_count` bigint comment '累计被退单次数',
	`refund_num` bigint comment '累计被退单件数',
	`refund_amount` decimal(10,2) '累计被退单金额',
	`refund_30_days_count` bigint comment '近30天被退单次数',
	`refund_30_days_num` bigint comment '近30天被退单件数',
	`refund_30_days_amount` decimal(10,2) comment '近30天被退单金额',
	`cart_count` bigint comment '累计被加购次数',
	`cart_num` bigint comment '累计被加购件数',
	`cart_30_days_count` bigint comment '近30天被加购次数',
	`cart_30_days_num` bigint comment '近30天被加购件数',
	`collect_count` bigint comment '累计被收藏次数',
	`collect_30_days_count` bigint comment '近30天被收藏次数',
	`comment_good_count` bigint comment '累计被好评次数',
	`comment_good_30_days_count` bigint comment '近30天被好评次数',
	`comment_mid_count` bigint comment '累计被中评次数',
	`comment_mid_30_days_count` bigint comment '近30天被中评次数',
	`comment_bad_count` bigint comment '累计被差评次数',
	`comment_bad_30_days_count` bigint comment '近30天被差评次数',
	`comment_default_count` bigint comment '累计默认评分次数'
)comment '商品主题宽表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwt/dwt_sku_topic'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwt_coupon_topic
drop table if exists dwt_coupon_topic;
create external table dwt_coupon_topic(
	`id` int comment '优惠卷ID',
	`get_count` bigint comment '累计被领取次数',
	`get_day_count` bigint comment '当日被领取次数',
	`used_count` bigint comment '累计使用次数',
	`used_day_count` bigint comment '当日使用次数'
)comment '优惠卷主题宽表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwt/dwt_coupon_topic'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwt_groupon_topic
drop table if exists dwt_groupon_topic;
create external table dwt_groupon_topic(
	`id` int comment '团购ID',
	`groupon_name` string '团购名称',
	`order_count` bigint comment '累计下单次数',
	`order_day_count` bigint comment '当日下单次数',
	`payment_count` bigint comment '累计支付次数',
	`payment_day_count` bigint comment '当日支付次数'
)comment '团购主题宽表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwt/dwt_groupon_topic'
TBLPROPERTIES ('parquet.compression'='lzo');











