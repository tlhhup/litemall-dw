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
INSERT OVERWRITE TABLE ${APP}.dwd_start_log
PARTITION(dt='$do_date')
select
    get_json_object(line,'$.mid'),
    get_json_object(line,'$.uid'),
    get_json_object(line,'$.g'),
    get_json_object(line,'$.vc'),
    get_json_object(line,'$.vn'),
    get_json_object(line,'$.l'),
    get_json_object(line,'$.sr'),
    get_json_object(line,'$.os'),
    get_json_object(line,'$.ar'),
    get_json_object(line,'$.md'),
    get_json_object(line,'$.ba'),
    get_json_object(line,'$.sv'),
    get_json_object(line,'$.hw'),
    get_json_object(line,'$.t'),
    get_json_object(line,'$.action'),
    get_json_object(line,'$.loadingTime'),
    get_json_object(line,'$.detail'),
    get_json_object(line,'$.extend1')
from ${APP}.ods_start_log
where dt='$do_date';
"

# 执行导入
$hive -e "$sql"