--- litemall
create database if not exists litemall on cluster litemall_2shards_1replicas;
-- 删除表
drop table if exists litemall.dws_order_wide_local on cluster litemall_2shards_1replicas;
-- 创建本地表
create table if not exists litemall.dws_order_wide_local on cluster litemall_2shards_1replicas(
	orderId UInt64 comment '订单ID',
	user_id UInt64 comment '用户ID',
	province UInt64 comment '省份ID',
	city UInt64 comment '城市ID',
	country UInt64 comment '乡镇ID',
	actual_price Decimal64(4) comment '实付金额',
	order_price Decimal64(4) comment '订单金额',
	goods_price Decimal64(4) comment '商品总金额',
	freight_price Decimal64(4) comment '配送费用',
	coupon_price Decimal64(4) comment '优惠券减免',
	integral_price Decimal64(4) comment '用户积分减免',
	add_time DateTime() comment '创建时间',
	is_first_order UInt8 comment '是否是首单',
	province_name String comment '省份名称',
	province_code UInt32 comment '省份编号',
	city_name String comment '城市名称',
	city_code UInt32 comment '城市编号',
	country_code UInt32 comment '乡镇编号',
	country_name String comment '乡镇名称',
	user_age_group String comment '用户年龄段',
	user_gender String comment '用户性别',
	orderDetailId UInt64 comment '订单详情ID',
	goods_id UInt64 comment '商品ID',
	goods_name String comment '商品名称',
	`number` UInt32 comment '商品数量',
	price Decimal64(4) comment '商品价格',
	category_id UInt32 comment '商品类别ID',
	category_name String comment '商品分类名称',
	brand_id UInt32 comment '商品品牌ID',
	brand_name String comment '商品品牌名称',
	capitation_price Decimal64(4) comment '该商品均摊金额',
	dt Date DEFAULT toDate(add_time) comment '分区列'
)engine=ReplicatedMergeTree('/clickhouse/tables/{shard}/dws_order_wide_local', '{replica}')
partition by dt
order by user_id;

-- 创建分布式表
DROP table if exists litemall.dws_order_wide_all on cluster litemall_2shards_1replicas;
create table if not exists litemall.dws_order_wide_all on cluster litemall_2shards_1replicas(
	orderId UInt64 comment '订单ID',
	user_id UInt64 comment '用户ID',
	province UInt64 comment '省份ID',
	city UInt64 comment '城市ID',
	country UInt64 comment '乡镇ID',
	actual_price Decimal64(4) comment '实付金额',
	order_price Decimal64(4) comment '订单金额',
	goods_price Decimal64(4) comment '商品总金额',
	freight_price Decimal64(4) comment '配送费用',
	coupon_price Decimal64(4) comment '优惠券减免',
	integral_price Decimal64(4) comment '用户积分减免',
	add_time DateTime() comment '创建时间',
	is_first_order UInt8 comment '是否是首单',
	province_name String comment '省份名称',
	province_code UInt32 comment '省份编号',
	city_name String comment '城市名称',
	city_code UInt32 comment '城市编号',
	country_code UInt32 comment '乡镇编号',
	country_name String comment '乡镇名称',
	user_age_group String comment '用户年龄段',
	user_gender String comment '用户性别',
	orderDetailId UInt64 comment '订单详情ID',
	goods_id UInt64 comment '商品ID',
	goods_name String comment '商品名称',
	`number` UInt32 comment '商品数量',
	price Decimal64(4) comment '商品价格',
	category_id UInt32 comment '商品类别ID',
	category_name String comment '商品分类名称',
	brand_id UInt32 comment '商品品牌ID',
	brand_name String comment '商品品牌名称',
	capitation_price Decimal64(4) comment '该商品均摊金额',
	dt Date DEFAULT toDate(add_time) comment '分区列'
)engine=Distributed(litemall_2shards_1replicas,litemall,dws_order_wide_local,user_id);

-- 消费Kafka中的数据
drop table if exists litemall.dwd_fact_order_detail on cluster litemall_2shards_1replicas;
CREATE table if not exists litemall.dwd_fact_order_detail on cluster litemall_2shards_1replicas(
	id UInt32 comment '订单详情ID',
	order_id UInt32 comment '订单ID',
	goods_id UInt32 comment '商品ID',
	goods_name String comment '商品名称',
	`number` UInt32 comment '商品数量',
	price Decimal32(4) comment '商品价格',
	category_id UInt32 comment '商品分类ID',
	category_name String comment '商品分类名称',
	brand_id UInt32 comment '商品品牌ID',
	brand_name String comment '商品品牌名称'
)ENGINE=Kafka('kafka-master:9092', 'dwd_fact_order_detail', 'ck', 'JSONEachRow');

-- 创建表
CREATE table if not exists litemall.dws_goods_action_local on cluster litemall_2shards_1replicas(
	goods_id UInt32 comment '商品ID',
	goods_name String comment '商品名称',
	`number` UInt32 comment '商品数量',
	category_id UInt32 comment '商品分类ID',
	category_name String comment '商品分类名称',
	brand_id UInt32 comment '商品品牌ID',
	brand_name String comment '商品品牌名称',
	dt Date DEFAULT toDate(now()) comment '分区'
)engine=ReplicatedSummingMergeTree('/clickhouse/tables/{shard}/dws_goods_action_local', '{replica}',(`number`))
ORDER by goods_id
PARTITION by dt;

CREATE table if not exists litemall.dws_goods_action_all on cluster litemall_2shards_1replicas(
	goods_id UInt32 comment '商品ID',
	goods_name String comment '商品名称',
	`number` UInt32 comment '商品数量',
	category_id UInt32 comment '商品分类ID',
	category_name String comment '商品分类名称',
	brand_id UInt32 comment '商品品牌ID',
	brand_name String comment '商品品牌名称',
	dt Date DEFAULT toDate(now()) comment '分区'
)engine=Distributed(litemall_2shards_1replicas,litemall,dws_goods_action_local,goods_id);

-- 创建物化视图将数据转发到表中(持久化到ck中),实时同步数据
CREATE MATERIALIZED VIEW if not exists litemall.dwd_fact_order_detail_consumer on
cluster litemall_2shards_1replicas TO litemall.dws_goods_action_all
    AS
SELECT
	goods_id,
	goods_name,
	`number`,
	category_id,
	category_name,
	brand_id,
	brand_name
from
	litemall.dwd_fact_order_detail;

-- 区域数据字典
create table if not exists litemall_region on cluster litemall_2shards_1replicas(
	id UInt64,
	iso_code String,
	iso_name String,
	region_name String,
	region_type String,
	iso_old_code String
)Engine = Dictionary(china_region_code);

-- 订单数据
drop table if exists litemall.dwd_fact_order_kafka on cluster litemall_2shards_1replicas;
CREATE table if not exists litemall.dwd_fact_order_kafka on cluster litemall_2shards_1replicas(
	id UInt32 comment '订单ID',
	user_id UInt64 comment '用户ID',
	province UInt64 comment '省份ID',
	city UInt64 comment '城市ID',
	country UInt64 comment '乡镇ID',
	actual_price Decimal64(4) comment '实付金额',
	order_price Decimal64(4) comment '订单金额',
	goods_price Decimal64(4) comment '商品总金额',
	freight_price Decimal64(4) comment '配送费用',
	coupon_price Decimal64(4) comment '优惠券减免',
	integral_price Decimal64(4) comment '用户积分减免',
	add_time String comment '创建时间',
	update_time String NULL comment '创建时间',
	is_first_order UInt8 comment '是否是首单',
	province_name String comment '省份名称',
	province_code UInt32 comment '省份编号',
	city_name String comment '城市名称',
	city_code UInt32 comment '城市编号',
	country_code UInt32 comment '乡镇编号',
	country_name String comment '乡镇名称',
	user_age_group String comment '用户年龄段',
	user_gender String comment '用户性别'
)ENGINE=Kafka('kafka-master:9092', 'dwd_fact_order', 'ck_order', 'JSONEachRow');

DROP table if exists litemall.dwd_fact_order_local on cluster litemall_2shards_1replicas;
CREATE table if not exists litemall.dwd_fact_order_local on cluster litemall_2shards_1replicas(
	id UInt32 comment '订单ID',
	user_id UInt64 comment '用户ID',
	province UInt64 comment '省份ID',
	city UInt64 comment '城市ID',
	country UInt64 comment '乡镇ID',
	actual_price Decimal64(4) comment '实付金额',
	order_price Decimal64(4) comment '订单金额',
	goods_price Decimal64(4) comment '商品总金额',
	freight_price Decimal64(4) comment '配送费用',
	coupon_price Decimal64(4) comment '优惠券减免',
	integral_price Decimal64(4) comment '用户积分减免',
	add_time String comment '创建时间',
	update_time String NULL comment '创建时间',
	is_first_order UInt8 comment '是否是首单',
	province_name String comment '省份名称',
	province_code UInt32 comment '省份编号',
	city_name String comment '城市名称',
	city_code UInt32 comment '城市编号',
	country_code UInt32 comment '乡镇编号',
	country_name String comment '乡镇名称',
	user_age_group String comment '用户年龄段',
	user_gender String comment '用户性别',
	dt Date DEFAULT toDate(add_time) comment '分区'
)engine=ReplicatedMergeTree('/clickhouse/tables/{shard}/dwd_fact_order_local', '{replica}')
ORDER by user_id
PARTITION by dt;

DROP table if exists litemall.dwd_fact_order_all on cluster litemall_2shards_1replicas;
CREATE table if not exists litemall.dwd_fact_order_all on cluster litemall_2shards_1replicas(
	id UInt32 comment '订单ID',
	user_id UInt64 comment '用户ID',
	province UInt64 comment '省份ID',
	city UInt64 comment '城市ID',
	country UInt64 comment '乡镇ID',
	actual_price Decimal64(4) comment '实付金额',
	order_price Decimal64(4) comment '订单金额',
	goods_price Decimal64(4) comment '商品总金额',
	freight_price Decimal64(4) comment '配送费用',
	coupon_price Decimal64(4) comment '优惠券减免',
	integral_price Decimal64(4) comment '用户积分减免',
	add_time String comment '创建时间',
	update_time String NULL comment '创建时间',
	is_first_order UInt8 comment '是否是首单',
	province_name String comment '省份名称',
	province_code UInt32 comment '省份编号',
	city_name String comment '城市名称',
	city_code UInt32 comment '城市编号',
	country_code UInt32 comment '乡镇编号',
	country_name String comment '乡镇名称',
	user_age_group String comment '用户年龄段',
	user_gender String comment '用户性别',
	dt Date DEFAULT toDate(add_time) comment '分区'
)engine=Distributed(litemall_2shards_1replicas,litemall,dwd_fact_order_local,user_id);

DROP VIEW if exists litemall.dwd_fact_order_consumer on cluster litemall_2shards_1replicas;
CREATE MATERIALIZED VIEW if not exists litemall.dwd_fact_order_consumer on
cluster litemall_2shards_1replicas TO litemall.dwd_fact_order_all
    AS
SELECT
	*
from
	litemall.dwd_fact_order_kafka;