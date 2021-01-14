use litemall;

-- 用户行为
-- ods_start_log
drop table if exists ods_start_log;
create EXTERNAL table ods_start_log(
	`line` string comment '日志'
) comment '启动日志表'
PARTITIONED BY (dt string)
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_start_log';

-- ods_event_log
drop table if exists ods_event_log;
create EXTERNAL table ods_event_log(
	`line` string comment '日志'
) comment '事件日志表'
PARTITIONED BY (dt string)
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_event_log';

-- 业务表
-- ods_goods
drop table if exists ods_goods;
create EXTERNAL table ods_goods(
	`id` int,
  	`goods_sn` string COMMENT '商品编号',
  	`name` string COMMENT '商品名称',
  	`category_id` int COMMENT '商品所属类目ID',
  	`brand_id` int comment '品牌ID',
  	`gallery` string COMMENT '商品宣传图片列表，采用JSON数组格式',
  	`keywords` string COMMENT '商品关键字，采用逗号间隔',
  	`brief` string COMMENT '商品简介',
  	`is_on_sale` tinyint COMMENT '是否上架',
  	`sort_order` smallint comment '排序编号',
  	`pic_url` string COMMENT '商品页面商品图片',
  	`share_url` string COMMENT '商品分享海报',
  	`is_new` tinyint COMMENT '是否新品首发，如果设置则可以在新品首发页面展示',
  	`is_hot` tinyint COMMENT '是否人气推荐，如果设置则可以在人气推荐页面展示',
  	`unit` string COMMENT '商品单位，例如件、盒',
  	`counter_price` decimal(10,2) COMMENT '专柜价格',
  	`retail_price` decimal(10,2) COMMENT '零售价格',
  	`detail` string COMMENT '商品详细介绍，是富文本格式',
  	`add_time` string COMMENT '创建时间',
  	`update_time` string COMMENT '更新时间',
  	`deleted` tinyint COMMENT '逻辑删除'
)comment '商品基本信息表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_goods';

-- ods_goods_category
drop table if exists ods_goods_category;
create EXTERNAL table ods_goods_category(
  `id` int,
  `name` string COMMENT '类目名称',
  `keywords` string COMMENT '类目关键字，以JSON数组格式',
  `desc` string COMMENT '类目广告语介绍',
  `pid` int COMMENT '父类目ID',
  `icon_url` string COMMENT '类目图标',
  `pic_url` string COMMENT '类目图片',
  `level` string comment '层级',
  `sort_order` tinyint COMMENT '排序',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '商品类目表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_goods_category';

-- ods_goods_brand
drop table if exists ods_goods_brand;
create EXTERNAL table ods_goods_brand(
  `id` int,
  `name` string COMMENT '品牌商名称',
  `desc` string COMMENT '品牌商简介',
  `pic_url` string COMMENT '品牌商页的品牌商图片',
  `sort_order` tinyint,
  `floor_price` decimal(10,2) COMMENT '品牌商的商品低价，仅用于页面展示',
  `add_time` string COMMENT '创建时间',
  `update_time` string  COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '商品品牌商表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_goods_brand';

-- ods_goods_product
drop table if exists ods_goods_product;
create EXTERNAL table ods_goods_product(
  `id` int,
  `goods_id` int,
  `specifications` string COMMENT '商品规格值列表，采用JSON数组格式',
  `price` decimal(10,2) COMMENT '商品货品价格',
  `number` int COMMENT '商品货品数量',
  `url` string COMMENT '商品货品图片',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '商品货品表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_goods_product';

-- ods_goods_attribute
drop table if exists ods_goods_attribute;
create EXTERNAL table ods_goods_attribute(
  `id` int,
  `goods_id` int COMMENT '商品表的商品ID',
  `attribute` string COMMENT '商品参数名称',
  `value` string COMMENT '商品参数值',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '商品参数表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_goods_attribute';

-- ods_goods_specification
drop table if exists ods_goods_specification;
create EXTERNAL table ods_goods_specification(
  `id` int,
  `goods_id` int COMMENT '商品表的商品ID',
  `specification` string COMMENT '商品规格名称',
  `value` string COMMENT '商品规格值',
  `pic_url` string COMMENT '商品规格图片',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '商品规格表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_goods_specification';

-- ods_coupon
drop table if exists ods_coupon;
create EXTERNAL table ods_coupon(
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
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_coupon';

-- ods_groupon_rules
drop table if exists ods_groupon_rules;
create EXTERNAL table ods_groupon_rules(
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
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_groupon_rules';

-- ods_cart
drop table if exists ods_cart;
create EXTERNAL table ods_cart(
  `id` int,
  `user_id` int COMMENT '用户表的用户ID',
  `goods_id` int COMMENT '商品表的商品ID',
  `goods_sn` string COMMENT '商品编号',
  `goods_name` string COMMENT '商品名称',
  `product_id` int COMMENT '商品货品表的货品ID',
  `price` decimal(10,2) COMMENT '商品货品的价格',
  `number` smallint COMMENT '商品货品的数量',
  `specifications` string COMMENT '商品规格值列表，采用JSON数组格式',
  `checked` tinyint COMMENT '购物车中商品是否选择状态',
  `pic_url` string COMMENT '商品图片或者商品货品图片',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '购物车商品表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_cart';

-- ods_collect
drop table if exists ods_collect;
create EXTERNAL table ods_collect(
  `id` int,
  `user_id` int COMMENT '用户表的用户ID',
  `value_id` int COMMENT '如果type=0，则是商品ID；如果type=1，则是专题ID',
  `type` tinyint COMMENT '收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '收藏表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_collect';

-- ods_system
drop table if exists ods_system;
create EXTERNAL table ods_system(
  `id` int,
  `key_name` string COMMENT '系统配置名',
  `key_value` string COMMENT '系统配置值',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '系统配置表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_system';

-- ods_comment
drop table if exists ods_comment;
create EXTERNAL table ods_comment(
  `id` int,
  `value_id` int COMMENT '如果type=0，则是商品评论；如果是type=1，则是专题评论。',
  `type` tinyint COMMENT '评论类型，如果type=0，则是商品评论；如果是type=1，则是专题评论；',
  `content` string COMMENT '评论内容',
  `admin_content` string COMMENT '管理员回复内容',
  `user_id` int COMMENT '用户表的用户ID',
  `has_picture` tinyint COMMENT '是否含有图片',
  `pic_urls` string COMMENT '图片地址列表，采用JSON数组格式',
  `star` smallint COMMENT '评分， 1-5',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '评论表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_comment';

-- ods_order_goods
drop table if exists ods_order_goods;
create EXTERNAL table ods_order_goods(
  `id` int,
  `order_id` int COMMENT '订单表的订单ID',
  `goods_id` int COMMENT '商品表的商品ID',
  `goods_name` string COMMENT '商品名称',
  `goods_sn` string COMMENT '商品编号',
  `product_id` int COMMENT '商品货品表的货品ID',
  `number` smallint COMMENT '商品货品的购买数量',
  `price` decimal(10,2)  COMMENT '商品货品的售价',
  `specifications` string COMMENT '商品货品的规格列表',
  `pic_url` string COMMENT '商品货品图片或者商品图片',
  `comment` int COMMENT '订单商品评论，如果是-1，则超期不能评价；如果是0，则可以评价；如果其他值，则是comment表里面的评论ID。',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '订单商品表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_order_goods';

-- ods_groupon
drop table if exists ods_groupon;
create EXTERNAL table ods_groupon(
  `id` int,
  `order_id` int COMMENT '关联的订单ID',
  `groupon_id` int COMMENT '如果是开团用户，则groupon_id是0；如果是参团用户，则groupon_id是团购活动ID',
  `rules_id` int COMMENT '团购规则ID，关联litemall_groupon_rules表ID字段',
  `user_id` int COMMENT '用户ID',
  `share_url` string COMMENT '团购分享图片地址',
  `creator_user_id` int COMMENT '开团用户ID',
  `creator_user_time` string COMMENT '开团时间',
  `status` smallint COMMENT '团购活动状态，开团未支付则0，开团中则1，开团失败则2',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '团购活动表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_groupon';

-- ods_order
drop table if exists ods_order;
create EXTERNAL table ods_order(
  `id` int,
  `user_id` int COMMENT '用户表的用户ID',
  `order_sn` string COMMENT '订单编号',
  `order_status` smallint COMMENT '订单状态',
  `aftersale_status` smallint COMMENT '售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消',
  `consignee` string COMMENT '收货人名称',
  `mobile` string COMMENT '收货人手机号',
  `address` string COMMENT '收货具体地址',
  `message` string COMMENT '用户订单留言',
  `goods_price` decimal(10,2) COMMENT '商品总费用',
  `freight_price` decimal(10,2) COMMENT '配送费用',
  `coupon_price` decimal(10,2) COMMENT '优惠券减免',
  `integral_price` decimal(10,2) COMMENT '用户积分减免',
  `groupon_price` decimal(10,2) COMMENT '团购优惠价减免',
  `order_price` decimal(10,2) COMMENT '订单费用， = goods_price + freight_price - coupon_price',
  `actual_price` decimal(10,2) COMMENT '实付费用， = order_price - integral_price',
  `pay_id` string COMMENT '微信付款编号',
  `pay_time` string COMMENT '微信付款时间',
  `ship_sn` string COMMENT '发货编号',
  `ship_channel` string COMMENT '发货快递公司',
  `ship_time` string COMMENT '发货开始时间',
  `refund_amount` decimal(10,2) COMMENT '实际退款金额，（有可能退款金额小于实际支付金额）',
  `refund_type` string COMMENT '退款方式',
  `refund_content` string COMMENT '退款备注',
  `refund_time` string COMMENT '退款时间',
  `confirm_time` string COMMENT '用户确认收货时间',
  `comments` smallint COMMENT '待评价订单商品数量',
  `end_time` string COMMENT '订单关闭时间',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除',
  `province` int COMMENT '省份ID',
  `city` int COMMENT '城市ID',
  `country` int COMMENT '乡镇ID'
)comment '订单表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_order';

-- ods_user
drop table if exists ods_user;
create EXTERNAL table ods_user(
  `id` int,
  `username` string COMMENT '用户名称',
  `password` string COMMENT '用户密码',
  `gender` tinyint COMMENT '性别：0 未知， 1男， 2 女',
  `birthday` string COMMENT '生日',
  `last_login_time`string COMMENT '最近一次登录时间',
  `last_login_ip` string COMMENT '最近一次登录IP地址',
  `user_level` tinyint COMMENT '0 普通用户，1 VIP用户，2 高级VIP用户',
  `nickname` string COMMENT '用户昵称或网络名称',
  `mobile` string COMMENT '用户手机号码',
  `avatar` string COMMENT '用户头像图片',
  `weixin_openid` string COMMENT '微信登录openid',
  `session_key` string COMMENT '微信登录会话KEY',
  `status` tinyint COMMENT '0 可用, 1 禁用, 2 注销',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '用户表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_user';

-- ods_coupon_user
drop table if exists ods_coupon_user;
create EXTERNAL table ods_coupon_user(
  `id` int,
  `user_id` int COMMENT '用户ID',
  `coupon_id` int COMMENT '优惠券ID',
  `status` smallint COMMENT '使用状态, 如果是0则未使用；如果是1则已使用；如果是2则已过期；如果是3则已经下架；',
  `used_time` string COMMENT '使用时间',
  `start_time` string COMMENT '有效期开始时间',
  `end_time` string COMMENT '有效期截至时间',
  `order_id` int COMMENT '订单ID',
  `add_time` string COMMENT '创建时间',
  `update_time` string COMMENT '更新时间',
  `deleted` tinyint COMMENT '逻辑删除'
)comment '优惠券用户使用表'
PARTITIONED BY (dt string)
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_coupon_user';

-- ods_region
drop table if exists ods_region;
create EXTERNAL table ods_region(
  `id` int,
  `pid` int COMMENT '行政区域父ID，例如区县的pid指向市，市的pid指向省，省的pid则是0',
  `name` string COMMENT '行政区域名称',
  `type` tinyint COMMENT '行政区域类型，如如1则是省， 如果是2则是市，如果是3则是区县',
  `code` int COMMENT '行政区域编码'
)comment '行政区域表'
row format delimited fields terminated by '\t'
STORED AS 
INPUTFORMAT  "com.hadoop.mapred.DeprecatedLzoTextInputFormat"
OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"
LOCATION '/warehouse/litemall/ods/ods_region';

-- 评论表添加product_id
alter table ods_comment add columns (product_id int comment '商品货品表ID');

-- 加购表添加ordered和ordered_time
alter table ods_cart add columns (ordered tinyint comment '是否下单',ordered_time string comment '下单时间');












