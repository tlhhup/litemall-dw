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
INSERT OVERWRITE TABLE dwd_event_base_log
PARTITION(dt='$do_date')
select
    parse_json_object(line,"mid"),
    parse_json_object(line,"uid"),
    parse_json_object(line,"g"),
    parse_json_object(line,"vc"),
    parse_json_object(line,"vn"),
    parse_json_object(line,"l"),
    parse_json_object(line,"sr"),
    parse_json_object(line,"os"),
    parse_json_object(line,"ar"),
    parse_json_object(line,"md"),
    parse_json_object(line,"ba"),
    parse_json_object(line,"sv"),
    parse_json_object(line,"hw"),
    parse_json_object(line,"st"),
    event_type,
    event_json
from ${APP}.ods_event_log lateral view extract_event_type(parse_json_object(line,"et")) tmp_flat as event_type,event_json
where dt='$do_date' and parse_json_object(line,"et")<>'';
"

# 执行导入
$hive -e "$sql"