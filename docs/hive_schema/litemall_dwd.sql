use litemall;

-- 日志表
-- dwd_start_log
drop table if exists dwd_start_log;
create external table dwd_start_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`action` string comment '状态',
	`loadingTime` string comment '加载时长',
	`detail` string comment '失败码',
	`extend1` string comment '失败的message'
) comment '启动日志表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_start_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_base_log
drop table if exists dwd_event_base_log;
create external table dwd_event_base_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`event_type` string comment '事件类型',
	`event_json` string comment '事件原始数据'
)comment '事件日志基础表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_base_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_ad_log
drop table if exists dwd_event_ad_log;
create external table dwd_event_ad_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`activity_id` string comment '活动id',
	`display_mills` string comment '展示时长',
	`entry` string comment '入口',
	`item_id` string,
	`action` string,
	`content_type` string
)comment '广告事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_ad_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_addCar_log
drop table if exists dwd_event_addCar_log;
create external table dwd_event_addCar_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`add_time` string comment '加入购物车时间',
	`goods_id` int comment '商品id',
	`num` int comment '商品数量',
	`user_id` int comment '用户id'
)comment '加购事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_addCar_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_comment_log
drop table if exists dwd_event_comment_log;
create external table dwd_event_comment_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`value_id` int comment '数据id',
	`add_time` string comment '评论时间',
	`star` int comment '评分',
	`type` int comment '数据类型 1 主题 0 商品',
	`user_id` int comment '用户id',
	`content` string comment '评论内容'
)comment '评论事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_comment_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_display_log
drop table if exists dwd_event_display_log;
create external table dwd_event_display_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`goods_id` int comment '商品id',
	`action` string,
	`extend1` string,
	`place` string,
	`category` string comment '商品分类'
)comment '展示事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_display_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_favorites_log
drop table if exists dwd_event_favorites_log;
create external table dwd_event_favorites_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`add_time` string comment '收藏时间',
	`goods_id` int comment '商品id',
	`user_id` int comment '用户id'
)comment '收藏事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_favorites_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_loading_log
drop table if exists dwd_event_loading_log;
create external table dwd_event_loading_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`loading_time` string comment '耗时',
	`extend2` string,
	`loadingWay` string,
	`action` string,
	`extend1` string,
	`type` string,
	`type1` string
)comment '加载事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_loading_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_praise_log
drop table if exists dwd_event_praise_log;
create external table dwd_event_praise_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
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
	`app_time` string comment '时间戳',
	`add_time` string comment '点赞时间',
	`target_id` int comment '数据id',
	`type` int comment '数据类型',
	`user_id` int comment '用户id'
)comment '点赞事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_praise_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- 业务表
-- 维度表
-- 特殊表，客观事实，不变的
-- dwd_dim_date_info
drop table if exists dwd_dim_date_info;
create external table dwd_dim_date_info(
	`date_id` string COMMENT '日', 
    `week_id` int COMMENT '周',
    `week_day` int COMMENT '周的第几天', 
    `day` int COMMENT '每月的第几天', 
    `month` int COMMENT '第几月', 
    `quarter` int COMMENT '第几季度', 
    `year` int COMMENT '年',
    `is_workday` int COMMENT '是否是周末', 
    `holiday_id` int COMMENT '是否是节假日'
)comment '时间维度表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_dim_date_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_dim_region_info
drop table if exists dwd_dim_region_info;
create EXTERNAL table dwd_dim_region_info(
  `id` int,
  `pid` int COMMENT '行政区域父ID，例如区县的pid指向市，市的pid指向省，省的pid则是0',
  `name` string COMMENT '行政区域名称',
  `type` tinyint COMMENT '行政区域类型，如如1则是省， 如果是2则是市，如果是3则是区县',
  `code` int COMMENT '行政区域编码'
)comment '行政区域表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_dim_region_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_dim_coupon_info
drop table if exists dwd_dim_coupon_info;
create EXTERNAL table dwd_dim_coupon_info(
  `id` int,
  `name` string COMMENT '优惠券名称',
  `desc` string COMMENT '优惠券介绍，通常是显示优惠券使用限制文字',
  `tag` string COMMENT '优惠券标签，例如新人专用',
  `total` int COMMENT '优惠券数量，如果是0，则是无限量',
  `discount` decimal(10,2) COMMENT '优惠金额，',
  `min` decimal(10,2) COMMENT '最少消费金额才能使用优惠券。',
  `limit` smallint COMMENT '用户领券限制数量，如果是0，则是不限制；默认是1，限领一张.',
  `type` smallint COMMENT '优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换；',
  `status` smallint COMMENT '优惠券状态，如果是0则是正常可用；如果是1则是过期; 如果是2则是下架。',
  `goods_type` smallint COMMENT '商品限制类型，如果0则全商品，如果是1则是类目限制，如果是2则是商品限制。',
  `goods_value` string COMMENT '商品限制值，goods_type如果是0则空集合，如果是1则是类目集合，如果是2则是商品集合。',
  `code` string COMMENT '优惠券兑换码',
  `time_type` smallint COMMENT '有效时间限制，如果是0，则基于领取时间的有效天数days；如果是1，则start_time和end_time是优惠券有效期；',
  `days` smallint COMMENT '基于领取时间的有效天数days。',
  `start_time` string COMMENT '使用券开始时间',
  `end_time` string COMMENT '使用券截至时间',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '优惠券信息及规则表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_dim_coupon_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_dim_groupon_rules_info
drop table if exists dwd_dim_groupon_rules_info;
create EXTERNAL table dwd_dim_groupon_rules_info(
  `id` int,
  `goods_id` int COMMENT '商品表的商品ID',
  `goods_name` string COMMENT '商品名称',
  `pic_url` string COMMENT '商品图片或者商品货品图片',
  `discount` decimal(10,0) COMMENT '优惠金额',
  `discount_member` int COMMENT '达到优惠条件的人数',
  `expire_time` string COMMENT '团购过期时间',
  `status` smallint COMMENT '团购规则状态，正常上线则0，到期自动下线则1，管理手动下线则2',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间'
)comment '团购规则表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_dim_groupon_rules_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_dim_goods_info
drop table if exists dwd_dim_goods_info;
create external table dwd_dim_goods_info(
	`id` int,
  	`goods_sn` string COMMENT '商品编号',
  	`product_id` int COMMENT '商品货品表的货品ID',
  	`name` string COMMENT '商品名称',
  	`category_id` int COMMENT '商品所属一级类目ID',
  	`category_name` string comment '商品所属一级类目名称',
  	`category2_id` int COMMENT '商品所属二级类目ID',
  	`category2_name` string comment '商品所属二级类目名称',
  	`brand_id` int comment '品牌ID',
  	`brand_name` string comment '品牌名称',
  	`brief` string COMMENT '商品简介',
  	`unit` string COMMENT '商品单位，例如件、盒',
  	`counter_price` decimal(10,2) COMMENT '专柜价格',
  	`retail_price` decimal(10,2) COMMENT '零售价格',
  	`add_time` string COMMENT '创建时间'
)comment '商品维度表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_dim_goods_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- 事实表
-- 事务性事实表
-- dwd_fact_order_goods_info
drop table if exists dwd_fact_order_goods_info;
create external table dwd_fact_order_goods_info(
	`id` int,
	`order_id` int COMMENT '订单表的订单ID',
	`goods_id` int COMMENT '商品表的商品ID',
	`goods_name` string COMMENT '商品名称',
	`goods_sn` string COMMENT '商品编号',
	`product_id` int COMMENT '商品货品表的货品ID',
	`number` smallint COMMENT '商品货品的购买数量',
	`price` decimal(10,2)  COMMENT '商品货品的售价',
	`add_time` string COMMENT '创建时间',
	`user_id` int comment '用户id',
  	`province` int COMMENT '省份ID',
  	`city` int COMMENT '城市ID',
  	`country` int COMMENT '乡镇ID'
)comment '订单详情事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_order_goods_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_fact_comment_info
drop table if exists dwd_fact_comment_info;
create external table dwd_fact_comment_info(
	`id` int,
	`value_id` int COMMENT '如果type=0，则是商品评论；如果是type=1，则是专题评论。',
	`type` tinyint COMMENT '评论类型，如果type=0，则是商品评论；如果是type=1，则是专题评论；',
	`user_id` int COMMENT '用户表的用户ID',
	`star` smallint COMMENT '评分， 1-5',
	`add_time` string COMMENT '创建时间'
)comment '评论事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_comment_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_fact_payment_info
drop table if exists dwd_fact_payment_info;
create external table dwd_fact_payment_info(
	`order_id` int comment '订单ID',
	`user_id` int COMMENT '用户表的用户ID',
	`order_sn` string COMMENT '订单编号',
	`pay_price` decimal(10,2) COMMENT '实付费用',
	`pay_id` string COMMENT '微信付款编号',
	`pay_time` string COMMENT '微信付款时间',
	`add_time` string COMMENT '创建时间',
	`province` int COMMENT '省份ID',
	`city` int COMMENT '城市ID',
	`country` int COMMENT '乡镇ID'
)comment '订单支付事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_payment_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_fact_refund_info
drop table if exists dwd_fact_refund_info;
create external table dwd_fact_refund_info(
	`order_id` int comment '订单ID',
	`user_id` int COMMENT '用户表的用户ID',
	`order_sn` string COMMENT '订单编号',
	`refund_amount` decimal(10,2) COMMENT '实际退款金额，（有可能退款金额小于实际支付金额）',
	`refund_type` string COMMENT '退款方式',
	`refund_content` string COMMENT '退款备注',
	`refund_time` string COMMENT '退款时间',
	`add_time` string COMMENT '创建时间',
	`province` int COMMENT '省份ID',
	`city` int COMMENT '城市ID',
	`country` int COMMENT '乡镇ID'
)comment '订单退款事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_refund_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- 周期性快照事实表
-- dwd_fact_cart_info
drop table if exists dwd_fact_cart_info;
create external table dwd_fact_cart_info(
	`id` int,
	`user_id` int COMMENT '用户表的用户ID',
	`goods_id` int COMMENT '商品表的商品ID',
	`goods_sn` string COMMENT '商品编号',
	`goods_name` string COMMENT '商品名称',
	`product_id` int COMMENT '商品货品表的货品ID',
	`price` decimal(10,2) COMMENT '商品货品的价格',
	`number` smallint COMMENT '商品货品的数量',
	`checked` tinyint COMMENT '购物车中商品是否选择状态'
)comment '加购事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_cart_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_fact_collect_info
drop table if exists dwd_fact_collect_info;
create external table dwd_fact_collect_info(
	`id` int,
	`user_id` int COMMENT '用户表的用户ID',
	`value_id` int COMMENT '如果type=0，则是商品ID；如果type=1，则是专题ID',
	`type` tinyint COMMENT '收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID',
	`collect_time` string COMMENT '收藏时间',
	`cancel_time` string COMMENT '取消收藏时间',
	`is_cancel` tinyint COMMENT '是否取消'
)comment '收藏事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_collect_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- 累计型快照事实表: 以add_time作为分区
-- dwd_fact_coupon_user_info
drop table if exists dwd_fact_coupon_user_info;
create external table dwd_fact_coupon_user_info(
  `id` int,
  `user_id` int COMMENT '用户ID',
  `coupon_id` int COMMENT '优惠券ID',
  `status` smallint COMMENT '使用状态, 如果是0则未使用；如果是1则已使用；如果是2则已过期；如果是3则已经下架；',
  `order_id` int COMMENT '订单ID',
  `get_time` string COMMENT '获取时间',
  `used_time` string COMMENT '使用时间',
  `start_time` string COMMENT '有效期开始时间',
  `end_time` string COMMENT '有效期截至时间'
)comment '优惠券用户使用事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_coupon_user_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_fact_order_info
drop table if exists dwd_fact_order_info;
create external table dwd_fact_order_info(
	`id` int,
	`user_id` int COMMENT '用户表的用户ID',
	`order_sn` string COMMENT '订单编号',
	`order_status` smallint COMMENT '订单状态',
	`goods_price` decimal(10,2) COMMENT '商品总费用',
	`freight_price` decimal(10,2) COMMENT '配送费用',
	`coupon_price` decimal(10,2) COMMENT '优惠券减免',
	`integral_price` decimal(10,2) COMMENT '用户积分减免',
	`groupon_id` int comment '团购ID',
	`groupon_price` decimal(10,2) COMMENT '团购优惠价减免',
	`order_price` decimal(10,2) COMMENT '订单费用， = goods_price + freight_price - coupon_price',
	`actual_price` decimal(10,2) COMMENT '实付费用， = order_price - integral_price',
	`add_time` string COMMENT '创建时间',
	`pay_id` string COMMENT '微信付款编号',
	`pay_time` string COMMENT '微信付款时间',
	`ship_sn` string COMMENT '发货编号',
	`ship_time` string COMMENT '发货开始时间',
	`refund_time` string COMMENT '退款时间',
	`confirm_time` string COMMENT '用户确认收货时间',
	`province` int COMMENT '省份ID',
	`city` int COMMENT '城市ID',
	`country` int COMMENT '乡镇ID'
)comment '订单事实表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_fact_order_info'
TBLPROPERTIES ('parquet.compression'='lzo');

-- 用户拉链表
-- dwd_dim_user_info_his
drop table if exists dwd_dim_user_info_his;
create external table dwd_dim_user_info_his(
	`id` int,
	`username` string COMMENT '用户名称',
	`gender` tinyint COMMENT '性别：0 未知， 1男， 2 女',
	`birthday` string COMMENT '生日',
	`user_level` tinyint COMMENT '0 普通用户，1 VIP用户，2 高级VIP用户',
	`nickname` string COMMENT '用户昵称或网络名称',
	`mobile` string COMMENT '用户手机号码',
	`avatar` string COMMENT '用户头像图片',
	`weixin_openid` string COMMENT '微信登录openid',
	`status` tinyint COMMENT '0 可用, 1 禁用, 2 注销',
	`add_time` string COMMENT '创建时间',
	`update_time` string COMMENT '更新时间',
	`start_date` string COMMENT '有效开始日期', 
	`end_date` string COMMENT '有效结束日期'
)comment '用户拉链表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_dim_user_info_his'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_dim_user_info_his_temp
drop table if exists dwd_dim_user_info_his_temp;
create external table dwd_dim_user_info_his_temp(
	`id` int,
	`username` string COMMENT '用户名称',
	`gender` tinyint COMMENT '性别：0 未知， 1男， 2 女',
	`birthday` string COMMENT '生日',
	`user_level` tinyint COMMENT '0 普通用户，1 VIP用户，2 高级VIP用户',
	`nickname` string COMMENT '用户昵称或网络名称',
	`mobile` string COMMENT '用户手机号码',
	`avatar` string COMMENT '用户头像图片',
	`weixin_openid` string COMMENT '微信登录openid',
	`status` tinyint COMMENT '0 可用, 1 禁用, 2 注销',
	`add_time` string COMMENT '创建时间',
	`update_time` string COMMENT '更新时间',
	`start_date` string COMMENT '有效开始日期', 
	`end_date` string COMMENT '有效结束日期'
)comment '用户拉链表'
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_dim_user_info_his_temp'
TBLPROPERTIES ('parquet.compression'='lzo');











































