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
insert into table ads_uv_count
select
    '$do_date' as dt,
    sum(if(last_date_login='$do_date',1,0)) as day_count,
    sum(if(last_date_login>=date_add(next_day('$do_date','MO'),-7) and last_date_login<=date_add(next_day('$do_date','MO'),-1) ,1,0)) as wk_count,
    sum(if(date_format(last_date_login,'yyyy-MM')=date_format('$do_date','yyyy-MM'),1,0)) as mn_count,
    if('$do_date'=date_sub(next_day('$do_date','MO'),1),'Y','N'),
    if('$do_date'=last_day('$do_date'),'Y','N')
from dwt_uv_topic;

insert into table ads_new_mid_count
select
    '$do_date' as dt,
    count(1) as new_mid_count
from dwt_uv_topic
where first_date_login='$do_date';

-- 第一天登录后，连续7天未登录
insert into table ads_silent_count
select
    '$do_date' as dt,
    count(1) as silent_count
from dwt_uv_topic
where first_date_login=last_date_login
and last_date_login<=date_add('$do_date',-7);

insert into table ads_back_count
select
    '$do_date' as dt,
    concat(date_sub(next_day('$do_date','MO'),7*2),'-',next_day('$do_date','SU')),
    count(1) as wastage_count
from
(
    -- 本周登陆用户
    select
        mid
    from dwt_uv_topic
    where last_date_login between date_sub(next_day('$do_date','MO'),7) and '$do_date'
    and mid in
    (
        -- 上周登陆用户
        select
            mid
        from dwt_uv_topic
        where last_date_login between date_sub(next_day('$do_date','MO'),7*2) and date_sub(next_day('$do_date','MO'),7)
    )
)t;

insert into table ads_wastage_count
select
    '$do_date' as dt,
    count(1) as wastage_count
from
(
    -- 最近7天未登陆用户
    select
        mid
    from dwt_uv_topic
    where last_date_login<=date_sub('$do_date',7)
    group by mid
)t;

insert into table ads_user_retention_day_rate
PARTITION(dt='$do_date')
select
    -- 一日留存
    '$do_date' as stat_date,
    date_sub('$do_date',1) as create_date,
    1 as retention_day,
    sum(if(first_date_login=date_sub('$do_date',1),1,0)) as new_mid_count,
    sum(if(last_date_login=date_sub('$do_date',1),1,0)) as retention_count,
    -- 昨天注册且今天登陆/昨天注册
    sum(if(first_date_login=date_sub('$do_date',1) and last_date_login='$do_date',1,0))/sum(if(first_date_login=date_sub('$do_date',1),1,0))
from dwt_uv_topic
union all
select
    -- 两日留存
    '$do_date' as stat_date,
    date_sub('$do_date',2) as create_date,
    2 as retention_day,
    sum(if(first_date_login=date_sub('$do_date',2),1,0)) as new_mid_count,
    sum(if(last_date_login=date_sub('$do_date',2),1,0)) as retention_count,
    -- 前天注册且今天登陆/前天注册
    sum(if(first_date_login=date_sub('$do_date',2) and last_date_login='$do_date',1,0))/sum(if(first_date_login=date_sub('$do_date',2),1,0))
from dwt_uv_topic
union all
select
    -- 三日留存
    '$do_date' as stat_date,
    date_sub('$do_date',3) as create_date,
    3 as retention_day,
    sum(if(first_date_login=date_sub('$do_date',3),1,0)) as new_mid_count,
    sum(if(last_date_login=date_sub('$do_date',3),1,0)) as retention_count,
    -- 三天前注册且今天登陆/三天前注册
    sum(if(first_date_login=date_sub('$do_date',3) and last_date_login='$do_date',1,0))/sum(if(first_date_login=date_sub('$do_date',3),1,0))
from dwt_uv_topic;

insert into table ads_continuity_wk_count
select
    '$do_date' as dt,
    concat(date_sub(next_day('$do_date','MO'),7*3),'-',next_day('$do_date','SU')),
    count(1) as continuity_count
from
(
    -- 本周登陆用户
    select
        mid
    from dwt_uv_topic
    where last_date_login between date_sub(next_day('$do_date','MO'),7) and '$do_date'
)current_week
join
(
    -- 上周登陆用户
    select
        mid
    from dwt_uv_topic
    where last_date_login between date_sub(next_day('$do_date','MO'),7*2) and date_sub(next_day('$do_date','MO'),7)
)tow_week on current_week.mid=tow_week.mid
join
(
    -- 上上周登陆用户
    select
        mid
    from dwt_uv_topic
    where last_date_login between date_sub(next_day('$do_date','MO'),7*3) and date_sub(next_day('$do_date','MO'),7*2)
)three_week on tow_week.mid=three_week.mid;

insert into table ads_continuity_uv_count
select
    '$do_date' as dt,
    concat(date_sub('$do_date',7),'_','$do_date'),
    count(1) as continuity_count
from
(
    select
        mid
    from
    (
        select
            mid
        from
        (
            select
                mid,
                date_sub(dt,rank) as diff
            from
            (
                select
                    mid,
                    dt,
                    rank() over (partition by mid order by dt) as rank
                from dws_uv_detail_daycount
                where dt>=date_sub('$do_date',6)
                order by mid,dt
            )t1
        )t2 group by mid,diff having count(1)>3
    )t3
    group by mid -- 去重
)t4;

insert into table ads_user_topic
select
    '$do_date' as dt,
    sum(if(login_last_date='$do_date',1,0)) as day_users,
    sum(if(login_first_date='$do_date',1,0)) as day_new_users,
    sum(if(payment_first_date='$do_date',1,0)) as day_new_payment_users,
    sum(if(payment_count>0,1,0)) as payment_users,
    count(1) as users,
    sum(if(login_last_date='$do_date',1,0))/count(1),
    sum(if(payment_count>0,1,0))/count(1),
    sum(if(login_first_date='$do_date',1,0))/count(1)
from dwt_user_topic;

insert into table ads_user_action_convert_day
select
    '$do_date' as dt,
    t.total_visitor_m_count,
    t.cart_u_count,
    t.cart_u_count/t.total_visitor_m_count,
    t.order_u_count,
    t.order_u_count/t.cart_u_count,
    t.payment_u_count,
    t.payment_u_count/t.order_u_count
from
(
	select
	    sum(if(login_count>0,1,0)) as total_visitor_m_count,
	    sum(if(cart_count>0,1,0)) as  cart_u_count,
	    sum(if(cart_count>0 and order_count>0,1,0)) as order_u_count,
	    sum(if(cart_count>0 and order_count>0 and payment_count>0,1,0)) as payment_u_count
	from dws_user_action_daycount
	where dt='$do_date'
)t;

insert into table ads_product_info
select
    '$do_date' as dt,
    sku.sku_num,
    spu.spu_num
from
(
    select
    '$do_date' as dt,
    count(1) as sku_num
    from dwt_sku_topic
)sku
join
(
    select
        '$do_date' as dt,
        count(1) as spu_num
    from
    (
        select
            spu_id
        from dwt_sku_topic
        group by spu_id
    )t
)spu on sku.dt=spu.dt;

insert OVERWRITE table ads_product_sale_topN
PARTITION(dt='$do_date')
select
    '$do_date' as stat_date,
    sku_id,
    payment_count
from dwt_sku_topic
order by payment_count desc limit 10;

insert OVERWRITE table ads_product_favor_topN
PARTITION(dt='$do_date')
select
    '$do_date' as stat_date,
    sku_id,
    collect_count
from dwt_sku_topic
order by collect_count desc limit 10;

insert OVERWRITE table ads_product_cart_topN
PARTITION(dt='$do_date')
select
    '$do_date' as stat_date,
    sku_id,
    cart_num
from dwt_sku_topic
order by cart_num desc limit 10;

insert OVERWRITE table ads_product_refund_topN
PARTITION(dt='$do_date')
select
    '$do_date' as stat_date,
    sku_id,
    if(order_30_days_count=0,0,(refund_30_days_count/payment_30_days_count)) as refund_ratio
from dwt_sku_topic
order by refund_ratio desc limit 10;

insert OVERWRITE table ads_appraise_bad_topN
PARTITION(dt='$do_date')
select
    '$do_date' as stat_date,
    sku_id,
    (comment_bad_count/(comment_bad_count+comment_mid_count+comment_good_count)) as appraise_bad_ratio
from dwt_sku_topic
order by appraise_bad_ratio desc limit 10;

insert into table ads_order_daycount
select
    '$do_date' as dt,
    sum(order_count),
    sum(order_total_amount),
    sum(if(order_count>0,1,0)) as order_users
from dws_user_action_daycount
where dt='$do_date';

insert into table ads_payment_daycount
select
    '$do_date' as dt,
    temp_payment.payment_count,
    temp_payment.payment_amount,
    temp_payment.payment_user_count,
    temp_sku.payment_sku_count,
    temp_order.payment_avg_time
from
(
    select
        '$do_date' as dt,
        sum(payment_count) as payment_count,
        sum(payment_total_amount) as payment_amount,
        sum(if(payment_count>0,1,0)) as payment_user_count
    from dws_user_action_daycount
    where dt='$do_date'
)temp_payment
join
(
    select
        '$do_date' as dt,
        sum(if(payment_count>0,1,0)) as payment_sku_count
    from dws_goods_action_daycount
    where dt='$do_date'
)temp_sku on temp_sku.dt=temp_payment.dt
join
(
    select
        '$do_date' as dt,
        sum(unix_timestamp(pay_time)-unix_timestamp(add_time))/count(*)/60 payment_avg_time
    from dwd_fact_order_info
    where dt='$do_date'
)temp_order on temp_order.dt=temp_sku.dt;

insert OVERWRITE table ads_sale_brand_category1_stat_mn
PARTITION(dt='$do_date')
select
    goods_brand_id,
    goods_category1_id,
    goods_category1_name,
    sum(if(order_count>0,1,0)) as buycount,
    sum(if(order_count>=2,1,0)) as buy_twice_last,
    sum(if(order_count>=2,1,0))/sum(if(order_count>0,1,0)) as buy_twice_last_ratio,
    sum(if(order_count>=3,1,0)) as buy_3times_last,
    sum(if(order_count>=3,1,0))/sum(if(order_count>0,1,0)) as buy_3times_last_ratio,
    date_format('$do_date' ,'yyyy-MM') stat_mn,
    '$do_date' stat_date
from
(
    select
        user_id,
        goods_brand_id,
        goods_category1_id,
        goods_category1_name,
        sum(order_count) as order_count
    from dws_goods_sale_detail_daycount
    where date_format(dt,'yyyy-MM')=date_format('$do_date','yyyy-MM')
    group by user_id,goods_brand_id,goods_category1_id,goods_category1_name
)t
group by goods_brand_id,goods_category1_id,goods_category1_name;

with
temp_uv as(
    select
        '$do_date' as dt,
        count(1) as uv_count
    from dws_uv_detail_daycount
    where dt='$do_date'
),
temp_user as(
    select
        '$do_date' as dt,
        count(1) as register_count
    from dwd_dim_user_info_his
    where date_format(add_time,'yyyy-MM-dd')='$do_date' and end_date='9999-99-99'
),
temp_goods as(
    select
        '$do_date' as dt,
        sum(cart_count) as cart_count,
        sum(comment_count) as comment_count,
        sum(collect_count) as collect_count,
        sum(order_count) as order_count,
        sum(order_total_amount) as order_total_amount,
        sum(payment_count) as payment_count,
        sum(payment_total_amount) as payment_total_amount,
        sum(refund_count) as refund_count,
        sum(refund_total_amount) as refund_total_amount,
        sum(coupon_count) as coupon_count,
        sum(if(payment_count>0,1,0)) as payment_user_count
    from dws_user_action_daycount
    where dt='$do_date'
),
temp_payoff as(
    select
        '$do_date' as dt,
        sum(actual_price) as payoff,
        count(distinct user_id) as payoff_user_count
    from dwd_fact_order_info
    where dt='$do_date' and order_status in (401,402)
)

INSERT INTO TABLE ads_date_topic
select
    di.date_id,
    di.week_id,
    di.week_day,
    di.day,
    di.month,
    di.quarter,
    di.year,
    di.is_workday,
    di.holiday_id,
    uv.uv_count,
    u.register_count,
    g.cart_count,
    g.comment_count,
    g.collect_count,
    g.order_count,
    g.order_total_amount,
    g.payment_count,
    g.payment_total_amount,
    g.refund_count,
    g.refund_total_amount,
    g.coupon_count,
    g.payment_user_count,
    p.payoff,
    p.payoff_user_count
from temp_uv uv
join temp_user u on uv.dt=u.dt
join temp_goods g on g.dt=u.dt
join temp_payoff p on p.dt=g.dt
join dwd_dim_date_info di on di.date_id=p.dt;

insert OVERWRITE table ads_region_order_daycount
PARTITION(dt='$do_date')
select
    '$do_date' as dt,
    p.id as province_id,
    p.name as province_name,
    c.id as city_id,
    if(p.pid=0 and substr(p.name,-1)='市',p.name,c.name) as city_name,
    t.id as country_id,
    t.name as country_name,
    r.order_date_first,
    r.order_date_last,
    r.order_count,
    r.order_amount,
    r.order_day_count,
    r.order_day_amount
from dwt_region_topic r
join dwd_dim_region_info p on r.province=p.id
join dwd_dim_region_info c on r.city=c.id
join dwd_dim_region_info t on r.country=t.id;
"

# 执行导入
$hive -e "$sql"