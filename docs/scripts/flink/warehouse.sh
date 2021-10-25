#!/bin/bash

flink=/opt/flink-1.13.2/bin/flink
APP_HOME=/home/hadoop/project/litemall/flink/data-warehouse-flink-1.0.0.jar

submit_task(){
$flink run \
  --detached \
  --class $1 \
  $APP_HOME
}

stop_task(){
$flink stop $1
}

start_maxwell(){
	submit_task org.tlh.warehouse.ods.MaxWellDispatcher
}

start_order(){
	submit_task org.tlh.warehouse.dwd.fact.DwdFactOrderApp
}

start_order_detail(){
	submit_task org.tlh.warehouse.dwd.fact.DwdFactOrderDetailApp
}

start_dwd_dim(){
	submit_task org.tlh.warehouse.dwd.dim.TransformDim2Redis
}

start_order_detail_cap(){
	submit_task org.tlh.warehouse.dws.DwsOrderDetailCapApp
}

case $1 in
	maxwell)
		start_maxwell
		;;
	dwd_dim)
		start_dwd_dim
		;;
	order)
		start_order
		;;
	order_detail)
		start_order_detail
		;;
	order_detail_cap)
		start_order_detail_cap
		;;
esac