#!/bin/bash

sqoop=/opt/sqoop-1.4.7/bin/sqoop
# 设置数据导入日期 T+1
do_date=`date -d '-1 day' +%F`

if [[ -n "$2" ]]; then
   do_date=$2
fi

# 通用方法
import_data(){
# 导入数据
$sqoop import \
--connect jdbc:mysql://storage:3306/litemall?tinyInt1isBit=false \
--username root \
--password 123456 \
--delete-target-dir \
--target-dir /original_data/litemall/db/$1/$do_date \
--query "$2 and \$CONDITIONS" \
--num-mappers 1 \
--fields-terminated-by '\t' \
--compress \
--compression-codec lzop \
--null-string '\\N' \
--null-non-string '\\N'

# lzo压缩，创建索引
hadoop jar /opt/hadoop/share/hadoop/common/hadoop-lzo-0.4.21.jar \
com.hadoop.compression.lzo.DistributedLzoIndexer \
/original_data/litemall/db/$1/$do_date
}

# 全量表
import_goods(){
	import_data litemall_goods 'select
								  `id`,            
								  `goods_sn`,      
								  `name`,          
								  `category_id`,   
								  `brand_id`,      
								  `gallery`,       
								  `keywords`,      
								  `brief`,         
								  `is_on_sale`,    
								  `sort_order`,    
								  `pic_url`,       
								  `share_url`,     
								  `is_new`,        
								  `is_hot`,        
								  `unit`,          
								  `counter_price`, 
								  `retail_price`,  
								  `detail`,        
								  `add_time`,      
								  `update_time`,   
								  `deleted`
								from litemall_goods where 1=1'
}

import_category(){
	import_data litemall_category 'select
									  `id`,
									  `name`,
									  `keywords`,
									  `desc`,
									  `pid`,
									  `icon_url`,
									  `pic_url`,
									  `level`,
									  `sort_order`,
									  `add_time`,
									  `update_time`,
									  `deleted`
									from litemall_category where 1=1'
}

import_brand(){
	import_data litemall_brand 'select
								  `id`,
								  `name`,
								  `desc`,
								  `pic_url`,
								  `sort_order`,
								  `floor_price`,
								  `add_time`,
								  `update_time`,
								  `deleted`
								from litemall_brand where 1=1'
}

import_goods_product(){
	import_data litemall_goods_product 'select
										  `id`,
										  `goods_id`,
										  `specifications`,
										  `price`,
										  `number`,
										  `url`,
										  `add_time`,
										  `update_time`,
										  `deleted`
										from litemall_goods_product where 1=1'
}

import_goods_attribute(){
	import_data litemall_goods_attribute 'select
										  `id`,
										  `goods_id`,
										  `attribute`,
										  `value`,
										  `add_time`,
										  `update_time`,
										  `deleted`
										from litemall_goods_attribute where 1=1'
}

import_goods_specification(){
	import_data litemall_goods_specification 'select
											  `id`,
											  `goods_id`,
											  `specification`,
											  `value`,
											  `pic_url`,
											  `add_time`,
											  `update_time`,
											  `deleted`
											from litemall_goods_specification where 1=1'
}

import_coupon(){
	import_data litemall_coupon 'select
								  `id`,
								  `name`,
								  `desc`,
								  `tag`,
								  `total`,
								  `discount`,
								  `min`,
								  `limit`,
								  `type`,
								  `status`,
								  `goods_type`,
								  `goods_value`,
								  `code`,
								  `time_type`,
								  `days`,
								  `start_time`,
								  `end_time`,
								  `add_time`,
								  `update_time`,
								  `deleted`
								from litemall_coupon where 1=1'
}

import_groupon_rules(){
	import_data litemall_groupon_rules 'select
										  `id`,
										  `goods_id`,
										  `goods_name`,
										  `pic_url`,
										  `discount`,
										  `discount_member`,
										  `expire_time`,
										  `status`,
										  `add_time`,
										  `update_time`,
										  `deleted`
										from litemall_groupon_rules where 1=1'
}

import_cart(){
	import_data litemall_cart 'select
								  `id`,
								  `user_id`,
								  `goods_id`,
								  `goods_sn`,
								  `goods_name`,
								  `product_id`,
								  `price`,
								  `number`,
								  `specifications`,
								  `checked`,
								  `pic_url`,
								  `add_time`,
								  `update_time`,
								  `deleted`
								from litemall_cart where 1=1'
}

import_collect(){
	import_data litemall_collect 'select
									  `id`,
									  `user_id`,
									  `value_id`,
									  `type`,
									  `add_time`,
									  `update_time`,
									  `deleted`
									from litemall_collect where 1=1'
}

import_system(){
	import_data litemall_system 'select
								  `id`,
								  `key_name`,
								  `key_value`,
								  `add_time`,
								  `update_time`,
								  `deleted`
								from litemall_system where 1=1'
}

# 增量表
import_comment(){
	import_data litemall_comment "select
								  id,
								  value_id,
								  type,
								  content,
								  admin_content,
								  user_id,
								  has_picture,
								  pic_urls,
								  star,
								  add_time,
								  update_time,
								  deleted
								from litemall_comment
								where date_format(add_time,'%Y-%m-%d')='${do_date}'"
}

import_order_goods(){
	import_data litemall_order_goods "select
									    id,
									    order_id,
									    goods_id,
									    goods_name,
									    goods_sn,
									    product_id,
									    number,
									    price,
									    specifications,
									    pic_url,
									    comment,
									    add_time,
									    update_time,
									    deleted
									from litemall_order_goods
									where date_format(add_time,'%Y-%m-%d')='${do_date}'"
}

import_groupon(){
	import_data litemall_groupon "select
								    id,
								    order_id,
								    groupon_id,
								    rules_id,
								    user_id,
								    share_url,
								    creator_user_id,
								    creator_user_time,
								    status,
								    add_time,
								    update_time,
								    deleted
								from litemall_groupon
								where date_format(add_time,'%Y-%m-%d')='${do_date}'"
}

# 新增及变化表
import_order(){
	import_data litemall_order "select
								    id,
								    user_id,
								    order_sn,
								    order_status,
								    aftersale_status,
								    consignee,
								    mobile,
								    address,
								    message,
								    goods_price,
								    freight_price,
								    coupon_price,
								    integral_price,
								    groupon_price,
								    order_price,
								    actual_price,
								    pay_id,
								    pay_time,
								    ship_sn,
								    ship_channel,
								    ship_time,
								    refund_amount,
								    refund_type,
								    refund_content,
								    refund_time,
								    confirm_time,
								    comments,
								    end_time,
								    add_time,
								    update_time,
								    deleted,
								    province,
								    city,
								    country
								from litemall_order
								where date_format(add_time,'%Y-%m-%d')='${do_date}'
								or date_format(update_time,'%Y-%m-%d')='${do_date}'"
}

import_user(){
	import_data litemall_user "select
								    id,
								    username,
								    password,
								    gender,
								    birthday,
								    last_login_time,
								    last_login_ip,
								    user_level,
								    nickname,
								    mobile,
								    avatar,
								    weixin_openid,
								    session_key,
								    status,
								    add_time,
								    update_time,
								    deleted
								from litemall_user
								where date_format(add_time,'%Y-%m-%d')='${do_date}'
								or date_format(update_time,'%Y-%m-%d')='${do_date}'"
}

import_coupon_user(){
	import_data litemall_coupon_user "select
									    id,
									    user_id,
									    coupon_id,
									    status,
									    used_time,
									    start_time,
									    end_time,
									    order_id,
									    add_time,
									    update_time,
									    deleted
									from litemall_coupon_user
									where date_format(add_time,'%Y-%m-%d')='${do_date}'
									or date_format(update_time,'%Y-%m-%d')='${do_date}'"
}

# 特殊表：客观事实
import_region(){
	import_data litemall_region "select
								    id,
								    pid,
								    name,
								    type,
								    code
								from litemall_region
								where 1=1"
}

case $1 in
	litemall_goods)
		import_goods
;;
	litemall_category)
		import_category
;;
	litemall_brand)
		import_brand
;;
	litemall_goods_product)
		import_goods_product
;;
	litemall_goods_attribute)
		import_goods_attribute
;;
	litemall_goods_specification)
		import_goods_specification
;;
	litemall_coupon)
		import_coupon
;;
	litemall_groupon_rules)
		import_groupon_rules
;;
	litemall_cart)
		import_cart
;;
	litemall_collect)
		import_collect
;;
	litemall_system)
		import_system
;;
	litemall_comment)
		import_comment
;;
	litemall_order_goods)
		import_order_goods
;;
	litemall_groupon)
		import_groupon
;;
	litemall_order)
		import_order
;;
	litemall_user)
		import_user
;;
	litemall_coupon_user)
		import_coupon_user
;;
	litemall_region)
		import_region
;;
	first)
		import_goods
		import_category
		import_brand
		import_goods_product
		import_goods_attribute
		import_goods_specification
		import_coupon
		import_groupon_rules
		import_cart
		import_collect
		import_system
		import_comment
		import_order_goods
		import_groupon
		import_order
		import_user
		import_coupon_user
		import_region
;;
	all)
		import_goods
		import_category
		import_brand
		import_goods_product
		import_goods_attribute
		import_goods_specification
		import_coupon
		import_groupon_rules
		import_cart
		import_collect
		import_system
		import_comment
		import_order_goods
		import_groupon
		import_order
		import_user
		import_coupon_user
;;
esac