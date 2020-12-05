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
INSERT OVERWRITE TABLE dws_uv_detail_daycount
PARTITION(dt='$do_date')
select
    mid,
    collect_set(uid),
    concat_ws('|',collect_set(mail)),
    concat_ws('|',collect_set(version_code)),
    concat_ws('|',collect_set(version_name)),
    concat_ws('|',collect_set(language)),
    concat_ws('|',collect_set(source)),
    concat_ws('|',collect_set(os)),
    concat_ws('|',collect_set(area)),
    concat_ws('|',collect_set(model)),
    concat_ws('|',collect_set(brand)),
    concat_ws('|',collect_set(sdk_version)),
    concat_ws('|',collect_set(hw)),
    count(1) as login_count
from dwd_start_log
where dt='$do_date' 
group by mid;
    
-- dws_region_detail_daycount

INSERT OVERWRITE TABLE dws_region_detail_daycount
PARTITION(dt='$do_date')
select
    province,
    city,
    country,
    sum(order_count),
    sum(order_total_amount),
    sum(payment_count),
    sum(payment_total_amount),
    sum(refund_count),
    sum(refund_total_amount)
from
(
    select
        province,
        city,
        country,
        count(1) as order_count,
        sum(order_price) as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount
    from dwd_fact_order_info
    where dt='$do_date'
    group by province,city,country

    union all

    select
        province,
        city,
        country,
        0 as order_count,
        0 as order_total_amount,
        count(1) as payment_count,
        sum(pay_price) as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount
    from dwd_fact_payment_info
    where dt='$do_date'
    group by province,city,country

    union all

    select
        province,
        city,
        country,
        0 as order_count,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        count(1) as refund_count,
        sum(refund_amount) as refund_total_amount
    from dwd_fact_refund_info
    where dt='$do_date'
    group by province,city,country
) t
group by province,city,country;

-- dws_user_action_daycount
with
temp_user as(
    select
        cast(uid as int) as user_id,
        count(1) as login_count
    from dwd_start_log
    where dt='$do_date'
    group by uid
),
temp_cart as(
    select
        user_id,
        count(1) as cart_count,
        sum(number) as cart_amount
    from dwd_fact_cart_info
    where dt='$do_date' and date_format(add_time,'yyyy-MM-dd')='$do_date'
    group by user_id
),
temp_comment as(
    select
        user_id,
        count(1) as comment_count
    from dwd_fact_comment_info
    where dt='$do_date'
    group by user_id
),
temp_collect as(
    select
        user_id,
        count(1) as collect_count
    from dwd_fact_collect_info
    where dt='$do_date'
    group by user_id
),
temp_order as(
    select
        user_id,
        count(1) as order_count,
        sum(order_price) as order_total_amount
    from dwd_fact_order_info
    where dt='$do_date'
    group by user_id
),
temp_payment as(
    select
        user_id,
        count(1) as payment_count,
        sum(pay_price) as payment_total_amount
    from dwd_fact_payment_info
    where dt='$do_date'
    group by user_id
),
temp_refund as(
    select
        user_id,
        count(1) as refund_count,
        sum(refund_amount) as refund_total_amount
    from dwd_fact_refund_info
    where dt='$do_date'
    group by user_id
),
temp_coupon as(
    select
        user_id,
        count(1) as coupon_count
    from dwd_fact_coupon_user_info
    where dt='$do_date'
    group by user_id
)

INSERT OVERWRITE TABLE dws_user_action_daycount
PARTITION(dt='$do_date')
select
    user_id,
    sum(login_count),
    sum(cart_count),
    sum(cart_amount),
    sum(comment_count),
    sum(collect_count),
    sum(order_count),
    sum(order_total_amount),
    sum(payment_count),
    sum(payment_total_amount),
    sum(refund_count),
    sum(refund_total_amount),
    sum(coupon_count)
from
(
    select
        user_id,
        login_count,
        0 as cart_count,
        0 as cart_amount,
        0 as comment_count,
        0 as collect_count,
        0 as order_count,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount,
        0 as coupon_count
    from temp_user

    union all

    select
        user_id,
        0 as login_count,
        cart_count,
        cart_amount,
        0 as comment_count,
        0 as collect_count,
        0 as order_count,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount,
        0 as coupon_count
    from temp_cart

    union all

    select
        user_id,
        0 as login_count,
        0 as cart_count,
        0 as cart_amount,
        comment_count,
        0 as collect_count,
        0 as order_count,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount,
        0 as coupon_count
    from temp_comment

    union all

    select
        user_id,
        0 as login_count,
        0 as cart_count,
        0 as cart_amount,
        0 as comment_count,
        collect_count,
        0 as order_count,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount,
        0 as coupon_count
    from temp_collect

    union all

    select
        user_id,
        0 as login_count,
        0 as cart_count,
        0 as cart_amount,
        0 as comment_count,
        0 as collect_count,
        order_count,
        order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount,
        0 as coupon_count
    from temp_order

    union all

    select
        user_id,
        0 as login_count,
        0 as cart_count,
        0 as cart_amount,
        0 as comment_count,
        0 as collect_count,
        0 as order_count,
        0 as order_total_amount,
        payment_count,
        payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount,
        0 as coupon_count
    from temp_payment

    union all

    select
        user_id,
        0 as login_count,
        0 as cart_count,
        0 as cart_amount,
        0 as comment_count,
        0 as collect_count,
        0 as order_count,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        refund_count,
        refund_total_amount,
        0 as coupon_count
    from temp_refund

    union all

    select
        user_id,
        0 as login_count,
        0 as cart_count,
        0 as cart_amount,
        0 as comment_count,
        0 as collect_count,
        0 as order_count,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_total_amount,
        coupon_count
    from temp_coupon
)t group by user_id;

-- dws_goods_action_daycount

with
temp_cart as
(
    select
        product_id as sku_id,
        count(1) as cart_count,
        sum(number) as cart_num
    from dwd_fact_cart_info
    where dt='$do_date' and date_format(add_time,'yyyy-MM-dd')='$do_date'
    group by product_id
),
temp_order as
(
    select
        product_id as sku_id,
        count(1) as order_count,
        sum(number) as order_num,
        sum(price*number) as order_total_amount
    from dwd_fact_order_goods_info
    where dt='$do_date'
    group by product_id
),
temp_collect as
(
    select
        sku_id,
        count(1) as collect_count
    from dwd_dim_goods_info
    where dt='$do_date' and spu_id in
    (
        select
            value_id as spu_id
        from dwd_fact_collect_info
        where dt='$do_date' and type=0 and is_cancel=0
    )
    group by sku_id
),
temp_comment as
(
    select
        value_id as sku_id,
        count(1) as comment_count,
        sum(if(star=5,1,0)) as appraise_good_count,
        sum(if(star=3,1,0)) as appraise_mid_count,
        sum(if(star=0,1,0)) as appraise_bad_count,
        sum(if(star=4,1,0)) as appraise_default_count
    from dwd_fact_comment_info
    where dt='$do_date' and type=0
    group by value_id
),
temp_payment as
(
    select
        product_id as sku_id,
        count(1) as payment_count,
        sum(number) as payment_num,
        sum(number*price) as payment_total_amount
    from dwd_fact_order_goods_info
    where dt='$do_date' and order_id in
    (
        select
            id
        from dwd_fact_order_info
        where dt='$do_date' and date_format(pay_time,'yyyy-MM-dd')='$do_date'
    ) group by product_id
),
temp_refund as
(
    select
        product_id as sku_id,
        count(1) as refund_count,
        sum(number) as refund_num,
        sum(number*price) as refund_total_amount
    from dwd_fact_order_goods_info
    where dt='$do_date' and order_id in
    (
        select
            id
        from dwd_fact_order_info
        where dt='$do_date' and order_status=203 and date_format(confirm_time,'yyyy-MM-dd')='$do_date'
    ) group by product_id
)

INSERT OVERWRITE TABLE dws_goods_action_daycount
PARTITION(dt='$do_date')
select
    sku_id,
    sum(cart_count),
    sum(cart_num),
    sum(order_count),
    sum(order_num),
    sum(order_total_amount),
    sum(payment_count),
    sum(payment_num),
    sum(payment_total_amount),
    sum(refund_count),
    sum(refund_num),
    sum(refund_total_amount),
    sum(collect_count),
    sum(comment_count),
    sum(appraise_good_count),
    sum(appraise_mid_count),
    sum(appraise_bad_count),
    sum(appraise_default_count)
from
(
    select
        sku_id,
        cart_count,
        cart_num,
        0 as order_count,
        0 as order_num,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_num,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_num,
        0 as refund_total_amount,
        0 as collect_count,
        0 as comment_count,
        0 as appraise_good_count,
        0 as appraise_mid_count,
        0 as appraise_bad_count,
        0 as appraise_default_count
    from temp_cart

    union all

    select
        sku_id,
        0 as cart_count,
        0 as cart_num,
        order_count,
        order_num,
        order_total_amount,
        0 as payment_count,
        0 as payment_num,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_num,
        0 as refund_total_amount,
        0 as collect_count,
        0 as comment_count,
        0 as appraise_good_count,
        0 as appraise_mid_count,
        0 as appraise_bad_count,
        0 as appraise_default_count
    from temp_order

    union all

    select
        sku_id,
        0 as cart_count,
        0 as cart_num,
        0 as order_count,
        0 as order_num,
        0 as order_total_amount,
        payment_count,
        payment_num,
        payment_total_amount,
        0 as refund_count,
        0 as refund_num,
        0 as refund_total_amount,
        0 as collect_count,
        0 as comment_count,
        0 as appraise_good_count,
        0 as appraise_mid_count,
        0 as appraise_bad_count,
        0 as appraise_default_count
    from temp_payment

    union all

    select
        sku_id,
        0 as cart_count,
        0 as cart_num,
        0 as order_count,
        0 as order_num,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_num,
        0 as payment_total_amount,
        refund_count,
        refund_num,
        refund_total_amount,
        0 as collect_count,
        0 as comment_count,
        0 as appraise_good_count,
        0 as appraise_mid_count,
        0 as appraise_bad_count,
        0 as appraise_default_count
    from temp_refund

    union all

    select
        sku_id,
        0 as cart_count,
        0 as cart_num,
        0 as order_count,
        0 as order_num,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_num,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_num,
        0 as refund_total_amount,
        collect_count,
        0 as comment_count,
        0 as appraise_good_count,
        0 as appraise_mid_count,
        0 as appraise_bad_count,
        0 as appraise_default_count
    from temp_collect

    union all

    select
        sku_id,
        0 as cart_count,
        0 as cart_num,
        0 as order_count,
        0 as order_num,
        0 as order_total_amount,
        0 as payment_count,
        0 as payment_num,
        0 as payment_total_amount,
        0 as refund_count,
        0 as refund_num,
        0 as refund_total_amount,
        0 as collect_count,
        comment_count,
        appraise_good_count,
        appraise_mid_count,
        appraise_bad_count,
        appraise_default_count
    from temp_comment
) t group by sku_id;

-- dws_goods_sale_detail_daycount
with
temp_user as(
    select
        id as user_id,
        gender as user_gender,
        months_between('$do_date',birthday) /12 as user_age,
        user_level
    from dwd_dim_user_info_his
    where end_date='9999-99-99'
),
temp_sale as(
    select
        user_id,
        product_id as sku_id,
        sum(number) as goods_num,
        count(1) as order_count,
        sum(number*price) as order_amount
    from dwd_fact_order_goods_info
    where dt='$do_date'
    group by user_id,product_id
),
temp_goods as(
    select
        *
    from dwd_dim_goods_info
    where dt='$do_date'
)

INSERT OVERWRITE TABLE dws_goods_sale_detail_daycount
PARTITION(dt='$do_date')
select
    u.user_id,
    s.sku_id,
    u.user_gender,
    u.user_age,
    u.user_level,
    g.name,
    g.brand_id,
    g.brand_name,
    g.category_id,
    g.category_name,
    g.category2_id,
    g.category2_name,
    g.spu_id,
    s.goods_num,
    g.retail_price,
    s.order_count,
    s.order_amount
from temp_sale s
join temp_goods g on s.sku_id=g.sku_id
join temp_user u on u.user_id=s.user_id;

-- dws_groupon_info_daycount
with
temp_groupon as(
    select
        id,
        goods_name as groupon_name,
        add_time as start_time,
        expire_time as end_time,
        add_time as create_time
    from dwd_dim_groupon_rules_info
    where dt='$do_date'
),
temp_order as(
    select
        groupon_id,
        count(1) as order_count
    from dwd_fact_order_info
    where dt='$do_date' and groupon_id is not null
    group by groupon_id
),
temp_payment as(
    select
        groupon_id,
        count(1) as payment_count
    from dwd_fact_order_info
    where dt='$do_date' and groupon_id is not null and date_format(pay_time,'yyyy-MM-dd')='$do_date'
    group by groupon_id
)

INSERT OVERWRITE TABLE dws_groupon_info_daycount
PARTITION(dt='$do_date')
select
    g.id,
    g.groupon_name,
    g.start_time,
    g.end_time,
    g.create_time,
    nvl(o.order_count,0),
    nvl(p.payment_count,0)
from temp_groupon g
left join temp_order o on g.id=o.groupon_id
left join temp_payment p on g.id=p.groupon_id;

-- dws_coupon_daycount

with
temp_get as(
	select
		coupon_id as id,
		count(1) as get_count
	from dwd_fact_coupon_user_info
	where dt='$do_date'
	group by coupon_id
),
temp_use as(
	select
		coupon_id as id,
		count(1) as used_count
	from dwd_fact_coupon_user_info
	where dt='$do_date' and order_id is not null
	group by coupon_id
),
temp_coupon as(
	select
		id,
		name
	from dwd_dim_coupon_info
	where dt='$do_date'
)

INSERT OVERWRITE TABLE dws_coupon_daycount
PARTITION(dt='$do_date')
select
	c.id,
	c.name,
	g.get_count,
	o.used_count
from temp_coupon c
join temp_get g on g.id=c.id
join temp_use o on c.id=o.id;
"

# 执行导入
$hive -e "$sql"