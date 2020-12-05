#!/bin/bash

# 获取日期
if [ -n "$2" ];then
   do_date=$2
else
   do_date=`date -d '-1 day' +%F`
fi

echo "导入数据日期 $do_date"

# 定义变量
APP=litemall
hive=/opt/apache-hive/bin/hive

# 定义sql
sql="
LOAD DATA  INPATH '/original_data/litemall/db/litemall_goods/$do_date' OVERWRITE INTO TABLE ${APP}.ods_goods PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_category/$do_date' OVERWRITE INTO TABLE ${APP}.ods_goods_category PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_brand/$do_date' OVERWRITE INTO TABLE ${APP}.ods_goods_brand PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_goods_product/$do_date' OVERWRITE INTO TABLE ${APP}.ods_goods_product PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_goods_attribute/$do_date' OVERWRITE INTO TABLE ${APP}.ods_goods_attribute PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_goods_specification/$do_date' OVERWRITE INTO TABLE ${APP}.ods_goods_specification PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_coupon/$do_date' OVERWRITE INTO TABLE ${APP}.ods_coupon PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_groupon_rules/$do_date' OVERWRITE INTO TABLE ${APP}.ods_groupon_rules PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_cart/$do_date' OVERWRITE INTO TABLE ${APP}.ods_cart PARTITION (dt='${do_date}');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_collect/$do_date' OVERWRITE INTO TABLE ${APP}.ods_collect PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_system/$do_date' OVERWRITE INTO TABLE ${APP}.ods_system PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_comment/$do_date' OVERWRITE INTO TABLE ${APP}.ods_comment PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_order_goods/$do_date' OVERWRITE INTO TABLE ${APP}.ods_order_goods PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_groupon/$do_date' OVERWRITE INTO TABLE ${APP}.ods_groupon PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_order/$do_date' OVERWRITE INTO TABLE ${APP}.ods_order PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_user/$do_date' OVERWRITE INTO TABLE ${APP}.ods_user PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/db/litemall_coupon_user/$do_date' OVERWRITE INTO TABLE ${APP}.ods_coupon_user PARTITION (dt='$do_date');
"
sql1="
LOAD DATA  INPATH '/original_data/litemall/db/litemall_region/$do_date' OVERWRITE INTO TABLE ${APP}.ods_region PARTITION (dt='$do_date');
"

# 执行导入
case $1 in
     first)
	$hive -e "$sql"
	$hive -e "$sql1"
;;
     all)
	$hive -e "$sql"
;;
esac