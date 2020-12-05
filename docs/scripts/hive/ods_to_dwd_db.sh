#!/bin/bash

# 获取日期
if [ -n "$2" ];then
   do_date=$2
else
   do_date=`date -d '-1 day' +%F`
fi

# 定义变量
APP=litemall
hive=/opt/apache-hive/bin/hive

# 定义sql
sql="
use ${APP};

INSERT OVERWRITE TABLE dwd_dim_coupon_info
PARTITION(dt='$do_date')
select
    id,
    name,
    desc,
    tag,
    total,
    discount,
    min,
    limit,
    type,
    status,
    goods_type,
    goods_value,
    code,
    time_type,
    days,
    start_time,
    end_time,
    add_time,
    update_time,
    deleted
from ods_coupon
where dt='$do_date';

INSERT OVERWRITE TABLE dwd_dim_groupon_rules_info
PARTITION(dt='$do_date')
select
    id,
    goods_id,
    goods_name,
    pic_url,
    discount,
    discount_member,
    expire_time,
    status,
    add_time,
    update_time
from ods_groupon_rules
where dt='$do_date';

INSERT OVERWRITE TABLE dwd_dim_goods_info
PARTITION(dt='$do_date')
select
    ogp.id,
    og.goods_sn,
    og.id,
    og.name,
    c1.id,
    c1.name,
    c2.id,
    c2.name,
    ogb.id,
    ogb.name,
    og.brief,
    og.unit,
    ogp.price,
    og.counter_price,
    og.retail_price,
    og.add_time
from
(
    select
        id,
        goods_id,
        price
    from ods_goods_product
    where dt='$do_date'
) ogp 
join
(
    select
        id,
        goods_sn,
        name,
        category_id as category2_id,
        brand_id,
        brief,
        unit,
        counter_price,
        retail_price,
        add_time
    from ods_goods
    where dt='$do_date'
) og on ogp.goods_id=og.id
join
(
    select
        id,
        name,
        pid
    from ods_goods_category
    where dt='$do_date' and level='L2'
) c2 on og.category2_id=c2.id
join
(
    select
        id,
        name
    from ods_goods_category
    where dt='$do_date' and level='L1'
)c1 on c1.id=c2.pid
join
(
    select
        id,
        name
    from ods_goods_brand
    where dt='$do_date'
)ogb on og.brand_id=ogb.id;

INSERT OVERWRITE TABLE dwd_fact_order_goods_info
PARTITION(dt='$do_date')
select
    oog.id,
    oog.order_id,
    oog.goods_id,
    oog.goods_name,
    oog.goods_sn,
    oog.product_id,
    oog.number,
    oog.price,
    oog.add_time,
    oo.user_id,
    oo.province,
    oo.city,
    oo.country
from
(
    select
        *
    from ods_order_goods
    where dt='$do_date'
)oog
join 
(
    select
        *
    from ods_order
    where dt='$do_date'
)oo
on oog.order_id=oo.id;

INSERT OVERWRITE TABLE dwd_fact_comment_info
PARTITION(dt='$do_date')
select
    id,
    value_id,
    type,
    user_id,
    star,
    add_time
from ods_comment
where dt='$do_date';

INSERT OVERWRITE TABLE dwd_fact_payment_info
PARTITION(dt='$do_date')
select
    id,
    user_id,
    order_sn,
    actual_price,
    pay_id,
    pay_time,
    add_time,
    province,
    city,
    country
from ods_order
where dt='$do_date' and date_format(pay_time,'yyyy-MM-dd')='$do_date';

INSERT OVERWRITE TABLE dwd_fact_refund_info
PARTITION(dt='$do_date')
select
    id,
    user_id,
    order_sn,
    refund_amount,
    refund_type,
    refund_content,
    refund_time,
    confirm_time,
    province,
    city,
    country
from ods_order
where dt='$do_date' and order_status=203 and date_format(confirm_time,'yyyy-MM-dd')='$do_date';

INSERT OVERWRITE TABLE dwd_fact_cart_info
PARTITION(dt='$do_date')
select
    id,
    user_id,
    goods_id,
    goods_sn,
    goods_name,
    product_id,
    price,
    number,
    checked,
    add_time
from ods_cart
where dt='$do_date';

INSERT OVERWRITE TABLE dwd_fact_collect_info
PARTITION(dt='$do_date')
select
    id,
    user_id,
    value_id,
    type,
    add_time,
    update_time,
    deleted
from ods_collect
where dt='$do_date';

-- 动态分区
set hive.exec.dynamic.partition.mode=nonstrict;

INSERT OVERWRITE TABLE dwd_fact_coupon_user_info
PARTITION(dt)
select
    nvl(new.id,his.id),
    nvl(new.user_id,his.user_id),
    nvl(new.coupon_id,his.coupon_id),
    nvl(new.status,his.status),
    nvl(new.order_id,his.order_id),
    nvl(new.get_time,his.get_time),
    nvl(new.used_time,his.used_time),
    nvl(new.start_time,his.start_time),
    nvl(new.end_time,his.end_time),
    date_format(nvl(new.get_time,his.get_time),'yyyy-MM-dd') as dt
from
(
    -- 获取历史分区中的数据
    select
        *
    from dwd_fact_coupon_user_info
    where dt in
        (
            select 
                date_format(add_time,'yyyy-MM-dd')
            from ods_coupon_user
            where dt='$do_date' 
        )
) his
full outer join
(
    select
        id,
        user_id,
        coupon_id,
        status,
        order_id,
        add_time as get_time,
        used_time,
        start_time,
        end_time
    from ods_coupon_user
    where dt='$do_date'
) new on his.id=new.id;

INSERT OVERWRITE TABLE dwd_fact_order_info
PARTITION(dt)
select
    nvl(new.id,his.id),
    nvl(new.user_id,his.user_id),
    nvl(new.order_sn,his.order_sn),
    nvl(new.order_status,his.order_status),
    nvl(new.goods_price,his.goods_price),
    nvl(new.freight_price,his.freight_price),
    nvl(new.coupon_price,his.coupon_price),
    nvl(new.integral_price,his.integral_price),
    nvl(new.groupon_id,his.groupon_id),
    nvl(new.groupon_price,his.groupon_price),
    nvl(new.order_price,his.order_price),
    nvl(new.actual_price,his.actual_price),
    nvl(new.add_time,his.add_time),
    nvl(new.pay_id,his.pay_id),
    nvl(new.pay_time,his.pay_time),
    nvl(new.ship_sn,his.ship_sn),
    nvl(new.ship_time,his.ship_time),
    nvl(new.refund_time,his.refund_time),
    nvl(new.confirm_time,his.confirm_time),
    nvl(new.province,his.province),
    nvl(new.city,his.city),
    nvl(new.country,his.country),
    date_format(nvl(new.add_time,his.add_time),'yyyy-MM-dd') as dt
from
(
    -- 获取历史分区中的数据
    select
        *
    from dwd_fact_order_info
    where dt in
        (
            select 
                date_format(add_time,'yyyy-MM-dd')
            from ods_order
            where dt='$do_date' 
        )
) his
full outer join
(
    select
        oo.id,
        oo.user_id,
        oo.order_sn,
        oo.order_status,
        oo.goods_price,
        oo.freight_price,
        oo.coupon_price,
        oo.integral_price,
        og.id as groupon_id,
        oo.groupon_price,
        oo.order_price,
        oo.actual_price,
        oo.add_time,
        oo.pay_id,
        oo.pay_time,
        oo.ship_sn,
        oo.ship_time,
        oo.refund_time,
        oo.confirm_time,
        oo.province,
        oo.city,
        oo.country
    from
    (
        select
            *
        from ods_order
        where dt='$do_date'
    ) oo 
    left join
    (
        select
            *
        from ods_groupon 
        where dt='$do_date'
    ) og on og.order_id=oo.id   
) new on his.id=new.id;

INSERT OVERWRITE TABLE dwd_dim_user_info_his_temp
select
    *
from
(
    -- 新增或变化的数据，添加开始和结束标示
    select
        id,
        username,
        gender,
        birthday,
        user_level,
        nickname,
        mobile,
        avatar,
        weixin_openid,
        status,
        add_time,
        update_time,
        '2020-11-23' as start_date,
        '9999-99-99' as end_date
    from ods_user
    where dt='$do_date'

    union all
    
    -- 处理变化的数据
    select
        du.id,
        du.username,
        du.gender,
        du.birthday,
        du.user_level,
        du.nickname,
        du.mobile,
        du.avatar,
        du.weixin_openid,
        du.status,
        du.add_time,
        du.update_time,
        du.start_date,
        if(ou.id is not null and du.end_date='9999-99-99', date_add(ou.dt,-1),du.end_date) end_date
    from dwd_dim_user_info_his_temp du
    left join
    (
        select 
            *
        from ods_user
        where dt='$do_date'
    )ou on du.id=ou.id
)his order by his.id, his.start_date;

INSERT OVERWRITE TABLE dwd_dim_user_info_his
select
    *
from dwd_dim_user_info_his_temp;
"

sql1="
use ${APP};
INSERT OVERWRITE TABLE dwd_dim_region_info
select
    id,
    pid,
    name,
    type,
    code
from ods_region;
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