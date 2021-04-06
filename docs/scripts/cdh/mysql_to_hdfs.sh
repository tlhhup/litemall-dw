
import_data(){
sqoop import \
--hive-import \
--hive-drop-import-delims \
--create-hive-table \
--hive-overwrite \
--hive-table litemall.$1 \
--connect jdbc:mysql://storage:3306/litemall \
--username root \
--password 123456 \
--delete-target-dir \
--target-dir /user/admin/hive/litemall \
--table $1 \
--num-mappers 1 \
--compress \
--compression-codec lzop \
--null-string '\\N' \
--null-non-string '\\N'
}

import_user(){
	import_data litemall_user
}

import_orders(){
	import_data litemall_order
}

import_order_goods(){
	import_data litemall_order_goods
}

import_user
import_orders
import_order_goods