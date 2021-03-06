#!/bin/sh

hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_appraise_bad_topN/dt*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_back_count/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_continuity_uv_count/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_continuity_wk_count/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_new_mid_count/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_order_daycount/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_payment_daycount/000000_*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_product_cart_topN/dt*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_product_favor_topN/dt*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_product_info/000000_*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_product_refund_topN/dt*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_product_sale_topN/dt*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_sale_brand_category1_stat_mn/dt*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_silent_count/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_user_action_convert_day/000000_*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_user_retention_day_rate/dt*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_user_topic/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_uv_count/000000_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_wastage_count/000000_*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_user_retention_day_rate/HIVE_UNION_SUBDIR_*
hdfs dfs -rm -f /warehouse/litemall/ads/ads_date_topic/000000_*
hdfs dfs -rm -f -r /warehouse/litemall/ads/ads_region_order_daycount/dt*

hdfs dfs -rm -f /warehouse/litemall/dwt/dwt_coupon_topic/000000_*
hdfs dfs -rm -f /warehouse/litemall/dwt/dwt_groupon_topic/000000_*
hdfs dfs -rm -f /warehouse/litemall/dwt/dwt_region_topic/000000_*
hdfs dfs -rm -f /warehouse/litemall/dwt/dwt_sku_topic/000000_*
hdfs dfs -rm -f /warehouse/litemall/dwt/dwt_user_topic/000000_*
hdfs dfs -rm -f /warehouse/litemall/dwt/dwt_uv_topic/000000_*