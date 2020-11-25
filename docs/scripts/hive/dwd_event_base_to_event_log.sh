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
INSERT OVERWRITE TABLE dwd_event_ad_log
PARTITION(dt='$do_date')
    select
    mid,
    uid,
    mail,
    version_code,
    version_name,
    language,
    source,
    os,
    area,
    model,
    brand,
    sdk_version,
    hw,
    get_json_object(event_json,'$.ett'),
    get_json_object(event_json,'$.kv.activityId'),
    get_json_object(event_json,'$.kv.displayMills'),
    get_json_object(event_json,'$.kv.entry'),
    get_json_object(event_json,'$.kv.itemId'),
    get_json_object(event_json,'$.kv.action'),
    get_json_object(event_json,'$.kv.contentType')
from ${APP}.dwd_event_base_log
where dt='$do_date' and event_type='ad';

INSERT OVERWRITE TABLE dwd_event_addCar_log
PARTITION(dt='$do_date')
select
    mid,
    uid,
    mail,
    version_code,
    version_name,
    language,
    source,
    os,
    area,
    model,
    brand,
    sdk_version,
    hw,
    app_time,
    get_json_object(event_json,'$.kv.addTime'),
    get_json_object(event_json,'$.kv.goodsId'),
    get_json_object(event_json,'$.kv.num'),
    get_json_object(event_json,'$.kv.userId')
from ${APP}.dwd_event_base_log
where dt='$do_date' and event_type='addCar';

INSERT OVERWRITE TABLE dwd_event_comment_log
PARTITION(dt='$do_date')
select
    mid,
    uid,
    mail,
    version_code,
    version_name,
    language,
    source,
    os,
    area,
    model,
    brand,
    sdk_version,
    hw,
    app_time,
    get_json_object(event_json,'$.kv.valueId'),
    get_json_object(event_json,'$.kv.addTime'),
    get_json_object(event_json,'$.kv.star'),
    get_json_object(event_json,'$.kv.type'),
    get_json_object(event_json,'$.kv.userId'),
    get_json_object(event_json,'$.kv.content')
from ${APP}.dwd_event_base_log
where dt='$do_date' and event_type='comment';

INSERT OVERWRITE TABLE dwd_event_display_log
PARTITION(dt='$do_date')
select
    mid,
    uid,
    mail,
    version_code,
    version_name,
    language,
    source,
    os,
    area,
    model,
    brand,
    sdk_version,
    hw,
    app_time,
    get_json_object(event_json,'$.kv.goodsId'),
    get_json_object(event_json,'$.kv.action'),
    get_json_object(event_json,'$.kv.extend1'),
    get_json_object(event_json,'$.kv.place'),
    get_json_object(event_json,'$.kv.category')
from ${APP}.dwd_event_base_log
where dt='$do_date' and event_type='display';

INSERT OVERWRITE TABLE dwd_event_favorites_log
PARTITION(dt='$do_date')
    select
    mid,
    uid,
    mail,
    version_code,
    version_name,
    language,
    source,
    os,
    area,
    model,
    brand,
    sdk_version,
    hw,
    app_time,
    get_json_object(event_json,'$.kv.addTime'),
    get_json_object(event_json,'$.kv.courseId'),
    get_json_object(event_json,'$.kv.userId')
from ${APP}.dwd_event_base_log
where dt='$do_date' and event_type='favorites';

INSERT OVERWRITE TABLE dwd_event_loading_log
PARTITION(dt='$do_date')
    select
    mid,
    uid,
    mail,
    version_code,
    version_name,
    language,
    source,
    os,
    area,
    model,
    brand,
    sdk_version,
    hw,
    app_time,
    get_json_object(event_json,'$.kv.loadingTime'),
    get_json_object(event_json,'$.kv.extend2'),
    get_json_object(event_json,'$.kv.loadingWay'),
    get_json_object(event_json,'$.kv.action'),
    get_json_object(event_json,'$.kv.extend1'),
    get_json_object(event_json,'$.kv.type'),
    get_json_object(event_json,'$.kv.type1')
from ${APP}.dwd_event_base_log
where dt='$do_date' and event_type='loading';

INSERT OVERWRITE TABLE dwd_event_praise_log
PARTITION(dt='$do_date')
    select
    mid,
    uid,
    mail,
    version_code,
    version_name,
    language,
    source,
    os,
    area,
    model,
    brand,
    sdk_version,
    hw,
    app_time,
    get_json_object(event_json,'$.kv.addTime'),
    get_json_object(event_json,'$.kv.targetId'),
    get_json_object(event_json,'$.kv.type'),
    get_json_object(event_json,'$.kv.userId')
from ${APP}.dwd_event_base_log
where dt='$do_date' and event_type='praise';
"

# 执行导入
$hive -e "$sql"