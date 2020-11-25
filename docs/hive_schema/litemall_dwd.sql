use litemall;

-- dwd_start_log
drop table if exists dwd_start_log;
create external table dwd_start_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`action` string comment '状态',
	`loadingTime` string comment '加载时长',
	`detail` string comment '失败码',
	`extend1` string comment '失败的message'
) comment '启动日志表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_start_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_base_log
drop table if exists dwd_event_base_log;
create external table dwd_event_base_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`event_type` string comment '事件类型',
	`event_json` string comment '事件原始数据'
)comment '事件日志基础表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_base_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_ad_log
drop table if exists dwd_event_ad_log;
create external table dwd_event_ad_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`activity_id` string comment '活动id',
	`display_mills` string comment '展示时长',
	`entry` string comment '入口',
	`item_id` string,
	`action` string,
	`content_type` string
)comment '广告事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_ad_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_addCar_log
drop table if exists dwd_event_addCar_log;
create external table dwd_event_addCar_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`add_time` string comment '加入购物车时间',
	`goods_id` int comment '商品id',
	`num` int comment '商品数量',
	`user_id` int comment '用户id'
)comment '加购事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_addCar_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_comment_log
drop table if exists dwd_event_comment_log;
create external table dwd_event_comment_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`value_id` int comment '数据id',
	`add_time` string comment '评论时间',
	`star` int comment '评分',
	`type` int comment '数据类型 1 主题 0 商品',
	`user_id` int comment '用户id',
	`content` string comment '评论内容'
)comment '评论事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_comment_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_display_log
drop table if exists dwd_event_display_log;
create external table dwd_event_display_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`goods_id` int comment '商品id',
	`action` string,
	`extend1` string,
	`place` string,
	`category` string comment '商品分类'
)comment '展示事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_display_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_favorites_log
drop table if exists dwd_event_favorites_log;
create external table dwd_event_favorites_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`add_time` string comment '收藏时间',
	`goods_id` int comment '商品id',
	`user_id` int comment '用户id'
)comment '收藏事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_favorites_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_loading_log
drop table if exists dwd_event_loading_log;
create external table dwd_event_loading_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`loading_time` string comment '耗时',
	`extend2` string,
	`loadingWay` string,
	`action` string,
	`extend1` string,
	`type` string,
	`type1` string
)comment '加载事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_loading_log'
TBLPROPERTIES ('parquet.compression'='lzo');

-- dwd_event_praise_log
drop table if exists dwd_event_praise_log;
create external table dwd_event_praise_log(
	`mid` string comment '设备id',
	`uid` string comment '用户id',
	`mail` string comment '邮箱',
	`version_code` string comment '程序版本号',
	`version_name` string comment '程序版本名',
	`language` string comment '系统语言',
	`source` string comment '应用从那个渠道来的',
	`os` string comment '系统版本',
	`area` string comment '区域',
	`model` string comment '手机型号',
	`brand` string comment '手机品牌',
	`sdk_version` string,
	`hw` string comment '屏幕宽高',
	`app_time` string comment '时间戳',
	`add_time` string comment '点赞时间',
	`target_id` int comment '数据id',
	`type` int comment '数据类型',
	`user_id` int comment '用户id'
)comment '点赞事件表'
PARTITIONED BY (dt string)
STORED AS PARQUET
LOCATION '/warehouse/litemall/dwd/dwd_event_praise_log'
TBLPROPERTIES ('parquet.compression'='lzo');