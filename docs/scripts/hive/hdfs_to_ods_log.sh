#!/bin/bash

# 获取日期
if [ -n "$1" ];then
   do_date=$1
else
   do_date=`date -d '-1 day' +%F`
fi

echo "导入数据日期 $do_date"

# 定义变量
APP=litemall
hive=/opt/apache-hive/bin/hive

# 定义sql
sql="
LOAD DATA  INPATH '/original_data/litemall/log/start/$do_date' OVERWRITE INTO TABLE ${APP}.ods_start_log PARTITION (dt='$do_date');

LOAD DATA  INPATH '/original_data/litemall/log/event/$do_date' OVERWRITE INTO TABLE ${APP}.ods_event_log PARTITION (dt='$do_date')
"
# 执行导入
$hive -e "$sql"

# 创建索引
hadoop jar /opt/hadoop/share/hadoop/common/hadoop-lzo-0.4.21.jar com.hadoop.compression.lzo.DistributedLzoIndexer /warehouse/litemall/ods/ods_start_log/dt=$do_date

hadoop jar /opt/hadoop/share/hadoop/common/hadoop-lzo-0.4.21.jar com.hadoop.compression.lzo.DistributedLzoIndexer /warehouse/litemall/ods/ods_event_log/dt=$do_date
