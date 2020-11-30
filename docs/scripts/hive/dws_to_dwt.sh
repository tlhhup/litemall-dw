#!/bin/bash

# 获取日期
if [ -n "$1" ];then
   do_date=$1
else
   do_date=`date -d '-1 day' +%F`
fi

# 定义变量
APP=litemall
hive=/opt/apache-hive/bin/hive

# 定义sql
sql="
use ${APP};

INSERT OVERWRITE TABLE dwt_uv_topic
select
    nvl(old.mid,new.mid),
    nvl(old.uid,new.uid),
    nvl(old.mail,new.mail),
    nvl(old.version_code,new.version_code),
    nvl(old.version_name,new.version_name),
    nvl(old.language,new.language),
    nvl(old.source,new.source),
    nvl(old.os,new.os),
    nvl(old.area,new.area),
    nvl(old.model,new.model),
    nvl(old.brand,new.brand),
    nvl(old.sdk_version,new.sdk_version),
    nvl(old.hw,new.hw),
    nvl(old.first_date_login,new.first_date_login),
    nvl(new.last_date_login,old.last_date_login),
    nvl(old.login_count,0)+if(new.login_count>0,1,0),
    nvl(new.login_count,0)
from dwt_uv_topic old
full outer join
(
    select
        *,
        '$do_date' as first_date_login,
        '$do_date' as last_date_login
    from dws_uv_detail_daycount
    where dt='$do_date'
)new on old.mid=new.mid;

INSERT OVERWRITE TABLE dwt_region_topic
select
    nvl(old.province,new.province),
    nvl(old.city,new.city),
    nvl(old.country,new.country),
    nvl(old.order_date_first,new.order_date_first),
    nvl(new.order_date_last,old.order_date_last),
    nvl(old.order_count,0)+nvl(new.order_count,0),
    nvl(old.order_amount,0)+nvl(new.order_total_amount,0),
    nvl(new.order_count,0),
    nvl(new.order_total_amount,0),
    nvl(old.payment_amount,0)+nvl(new.payment_total_amount,0),
    nvl(new.payment_total_amount,0),
    nvl(old.refund_amount,0)+nvl(new.refund_total_amount,0),
    nvl(new.refund_total_amount,0)
from dwt_region_topic old
full outer join
(
    select
        *,
        '$do_date' as order_date_first,
        '$do_date' as order_date_last
    from dws_region_detail_daycount
    where dt='$do_date'
)new on old.city=new.city;

INSERT OVERWRITE TABLE dwt_user_topic
select
    nvl(old.user_id,new.user_id),
    if(old.login_first_date is null and new.login_dt_count>0,'$do_date',old.login_first_date),
    if(new.login_dt_count>0,'$do_date',old.login_last_date),
    nvl(old.login_count,0)+nvl(new.login_dt_count,0),
    nvl(new.login_30_days_count,0),
    if(old.order_first_date is null and new.order_dt_count>0,'$do_date',old.order_first_date),
    if(new.order_dt_count>0,'$do_date',old.order_last_date),
    nvl(old.order_count,0)+nvl(new.order_dt_count,0),
    nvl(old.order_amount,0)+nvl(new.order_dt_amount,0),
    nvl(new.order_30_days_count,0),
    nvl(new.order_30_days_amount,0),
    if(old.payment_first_date is null and new.payment_dt_count>0,'$do_date',old.payment_first_date),
    if(new.payment_dt_count>0,'$do_date',old.payment_last_date),
    nvl(old.payment_count,0)+nvl(new.payment_dt_count,0),
    nvl(old.payment_amount,0)+nvl(new.payment_dt_amount,0),
    nvl(new.payment_30_days_count,0),
    nvl(new.payment_30_days_amount,0),
    if(old.refund_first_date is not null and new.refund_dt_count>0,'$do_date',old.refund_first_date),
    if(new.refund_dt_count>0,'$do_date',old.refund_last_date),
    nvl(old.refund_count,0)+nvl(new.refund_dt_count,0),
    nvl(old.refund_amount,0)+nvl(new.refund_dt_amount,0),
    nvl(new.refund_30_days_count,0),
    nvl(new.refund_30_days_amount,0)
from dwt_user_topic old
full outer join
(
    select
        user_id,
        sum(if(dt='$do_date',login_count,0)) as login_dt_count,
        sum(login_count) as login_30_days_count,
        sum(if(dt='$do_date',order_count,0)) as order_dt_count,
        sum(if(dt='$do_date',order_total_amount,0)) as order_dt_amount,
        sum(order_count) as order_30_days_count,
        sum(order_total_amount) as order_30_days_amount,
        sum(if(dt='$do_date',payment_count,0)) as payment_dt_count,
        sum(if(dt='$do_date',payment_total_amount,0)) as payment_dt_amount,
        sum(payment_count) as payment_30_days_count,
        sum(payment_total_amount) as payment_30_days_amount,
        sum(if(dt='$do_date',refund_count,0)) as refund_dt_count,
        sum(if(dt='$do_date',refund_total_amount,0)) as refund_dt_amount,
        sum(refund_count) as refund_30_days_count,
        sum(refund_total_amount) as refund_30_days_amount
    from dws_user_action_daycount
    where dt between date_sub('$do_date',30) and '$do_date'
    group by user_id
)new on old.user_id=new.user_id;

INSERT OVERWRITE TABLE dwt_sku_topic
select
    nvl(old.sku_id,new.goods_id),
    nvl(old.order_count,0)+nvl(new.order_dt_count,0),
    nvl(old.order_amount,0)+nvl(new.order_dt_amount,0),
    nvl(old.order_num,0)+nvl(new.order_dt_num,0),
    nvl(new.order_30_days_count,0),
    nvl(new.order_30_days_amount,0),
    nvl(new.order_30_days_num,0),
    nvl(old.payment_count,0)+nvl(new.payment_dt_count,0),
    nvl(old.payment_amount,0)+nvl(new.payment_dt_amount,0),
    nvl(new.payment_30_days_count,0),
    nvl(new.payment_30_days_amount,0),
    nvl(old.refund_count,0)+nvl(new.refund_dt_count,0),
    nvl(old.refund_amount,0)+nvl(new.refund_dt_amount,0),
    nvl(new.refund_30_days_count,0),
    nvl(new.refund_30_days_amount,0),
    nvl(old.cart_count,0)+nvl(new.cart_dt_count,0),
    nvl(old.cart_num,0)+nvl(new.cart_dt_num,0),
    nvl(new.cart_30_days_count,0),
    nvl(new.cart_30_days_num,0),
    nvl(old.collect_count,0)+nvl(new.collect_dt_count,0),
    nvl(new.collect_30_days_count,0),
    nvl(old.comment_good_count,0)+nvl(new.comment_good_dt_count,0),
    nvl(new.comment_good_30_days_count,0),
    nvl(old.comment_mid_count,0)+nvl(new.comment_mid_dt_count,0),
    nvl(new.comment_mid_30_days_count,0),
    nvl(old.comment_bad_count,0)+nvl(new.comment_bad_dt_count,0),
    nvl(new.comment_bad_30_days_count,0),
    nvl(old.comment_default_count,0)+nvl(new.comment_default_dt_count,0)
from dwt_sku_topic old
full outer join
(
    select
        goods_id,
        sum(if(dt='$do_date',order_count,0)) as order_dt_count,
        sum(if(dt='$do_date',order_total_amount,0)) as order_dt_amount,
        sum(if(dt='$do_date',order_num,0)) as order_dt_num,
        sum(order_count) as order_30_days_count,
        sum(order_total_amount) as order_30_days_amount,
        sum(order_num) as order_30_days_num,
        sum(if(dt='$do_date',payment_count,0)) as payment_dt_count,
        sum(if(dt='$do_date',payment_total_amount,0)) as payment_dt_amount,
        sum(payment_count) as payment_30_days_count,
        sum(payment_total_amount) as payment_30_days_amount,
        sum(if(dt='$do_date',refund_count,0)) as refund_dt_count,
        sum(if(dt='$do_date',refund_total_amount,0)) as refund_dt_amount,
        sum(refund_count) as refund_30_days_count,
        sum(refund_total_amount) as refund_30_days_amount,
        sum(if(dt='$do_date',cart_count,0)) as cart_dt_count,
        sum(if(dt='$do_date',cart_num,0)) as cart_dt_num,
        sum(cart_count) as cart_30_days_count,
        sum(cart_num) as cart_30_days_num,
        sum(if(dt='$do_date',collect_count,0)) as collect_dt_count,
        sum(collect_count) as collect_30_days_count,
        sum(if(dt='$do_date',appraise_good_count,0)) as comment_good_dt_count,
        sum(appraise_good_count) as comment_good_30_days_count,
        sum(if(dt='$do_date',appraise_mid_count,0)) as comment_mid_dt_count,
        sum(appraise_mid_count) as comment_mid_30_days_count,
        sum(if(dt='$do_date',appraise_bad_count,0)) as comment_bad_dt_count,
        sum(appraise_bad_count) as comment_bad_30_days_count,
        sum(if(dt='$do_date',appraise_default_count,0)) as comment_default_dt_count
    from dws_goods_action_daycount
    where dt between date_sub('$do_date',30) and '$do_date'
    group by goods_id
)new on old.sku_id=new.goods_id;

INSERT OVERWRITE TABLE dwt_coupon_topic
select
    nvl(old.id,new.id),
    nvl(old.name,new.name),
    nvl(old.get_count,0)+nvl(new.get_count,0),
    nvl(new.get_count,0),
    nvl(old.used_count,0)+nvl(new.used_count,0),
    nvl(new.used_count,0)
from dwt_coupon_topic old
full outer join
(
    select
        *
    from dws_coupon_daycount
    where dt='$do_date'
)new on old.id=new.id;

INSERT OVERWRITE TABLE dwt_groupon_topic
select
    nvl(old.id,new.id),
    nvl(old.groupon_name,new.groupon_name),
    nvl(old.order_count,0)+nvl(new.order_count,0),
    nvl(new.order_count,0),
    nvl(old.payment_count,0)+nvl(new.payment_count,0),
    nvl(new.payment_count,0)
from dwt_groupon_topic old
full outer join
(
    select
        *
    from dws_groupon_info_daycount
    where dt='$do_date'
)new on old.id=new.id;
"

# 执行导入
$hive -e "$sql"