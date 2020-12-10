create table ads_region_order_daycount(
    `date` varchar(20) comment '统计日期',
    `province_id` int comment '省份ID',
    `province_name` varchar(50) comment '省份名称',
    `city_id` int comment '城市ID',
    `city_name` varchar(50) comment '城市名称',
    `country_id` int comment '乡镇ID',
    `country_name` varchar(100) comment '乡镇名称',
    `order_date_first` varchar(20) comment '首次下单时间',
    `order_date_last` varchar(20) comment '最近下单时间',
    `order_count` bigint comment '累计下单总数',
    `order_amount` decimal(10,2) comment '累计下单金额',
    `order_day_count` bigint comment '当日下单总数',
    `order_day_amount` decimal(10,2) comment '当日下单金额'
);