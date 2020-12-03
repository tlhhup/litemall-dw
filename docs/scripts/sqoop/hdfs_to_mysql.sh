#!/bin/bash

sqoop=/opt/sqoop-1.4.7/bin/sqoop

# 通用方法
export_data(){
# 导入数据
$sqoop export \
--connect jdbc:mysql://storage:3306/litemall_report \
--username root \
--password 123456 \
--export-dir /warehouse/litemall/ads/$1 \
--num-mappers 1 \
--table $1 \
--update-key $2 \
--update-mode allowinsert \
--input-fields-terminated-by '\t' \
--input-null-string '\\N' \
--input-null-non-string '\\N'
}
export_ads_uv_count(){
    export_data 'ads_uv_count' 'dt'
}

export_ads_new_mid_count(){
    export_data 'ads_new_mid_count' 'dt'
}

export_ads_silent_count(){
    export_data 'ads_silent_count' 'dt'
}

export_ads_back_count(){
    export_data 'ads_back_count' 'dt'
}

export_ads_wastage_count(){
    export_data 'ads_wastage_count' 'dt'
}

export_ads_user_retention_day_rate(){
    export_data 'ads_user_retention_day_rate' 'stat_date'
}

export_ads_continuity_wk_count(){
    export_data 'ads_continuity_wk_count' 'dt'
}

export_ads_continuity_uv_count(){
    export_data 'ads_continuity_uv_count' 'dt'
}

export_ads_user_topic(){
    export_data 'ads_user_topic' 'dt'
}

export_ads_user_action_convert_day(){
    export_data 'ads_user_action_convert_day' 'dt'
}

export_ads_product_info(){
    export_data 'ads_product_info' 'dt'
}

export_ads_product_sale_topN(){
    export_data 'ads_product_sale_topN' 'dt'
}

export_ads_product_favor_topN(){
    export_data 'ads_product_favor_topN' 'dt'
}

export_ads_product_cart_topN(){
    export_data 'ads_product_cart_topN' 'dt'
}

export_ads_product_refund_topN(){
    export_data 'ads_product_refund_topN' 'dt'
}

export_ads_appraise_bad_topN(){
    export_data 'ads_appraise_bad_topN' 'dt'
}

export_ads_order_daycount(){
    export_data 'ads_order_daycount' 'dt'
}

export_ads_payment_daycount(){
    export_data 'ads_payment_daycount' 'dt'
}

export_ads_sale_brand_category1_stat_mn(){
    export_data 'ads_sale_brand_category1_stat_mn' 'stat_date'
}

case $1 in
    ads_uv_count)
        export_ads_uv_count
        ;;
    ads_new_mid_count)
        export_ads_new_mid_count
        ;;
    ads_silent_count)
        export_ads_silent_count
        ;;
    ads_back_count)
        export_ads_back_count
        ;;
    ads_wastage_count)
        export_ads_wastage_count
        ;;
    ads_user_retention_day_rate)
        export_ads_user_retention_day_rate
        ;;
    ads_continuity_wk_count)
        export_ads_continuity_wk_count
        ;;
    ads_continuity_uv_count)
        export_ads_continuity_uv_count
        ;;
    ads_user_topic)
        export_ads_user_topic
        ;;
    ads_user_action_convert_day)
        export_ads_user_action_convert_day
        ;;
    ads_product_info)
        export_ads_product_info
        ;;
    ads_product_sale_topN)
        export_ads_product_sale_topN
        ;;
    ads_product_favor_topN)
        export_ads_product_favor_topN
        ;;
    ads_product_cart_topN)
        export_ads_product_cart_topN
        ;;
    ads_product_refund_topN)
        export_ads_product_refund_topN
        ;;
    ads_appraise_bad_topN)
        export_ads_appraise_bad_topN;
        ;;
    ads_order_daycount)
        export_ads_order_daycount
        ;;
    ads_payment_daycount)
        export_ads_payment_daycount
        ;;
    ads_sale_brand_category1_stat_mn)
        export_ads_sale_brand_category1_stat_mn
        ;;
    all)
        export_ads_uv_count

        export_ads_new_mid_count

        export_ads_silent_count

        export_ads_back_count

        export_ads_wastage_count

        export_ads_user_retention_day_rate

        export_ads_continuity_wk_count

        export_ads_continuity_uv_count

        export_ads_user_topic

        export_ads_user_action_convert_day

        export_ads_product_info

        export_ads_product_sale_topN

        export_ads_product_favor_topN

        export_ads_product_cart_topN

        export_ads_product_refund_topN

        export_ads_appraise_bad_topN

        export_ads_order_daycount

        export_ads_payment_daycount

        export_ads_sale_brand_category1_stat_mn
        ;;
esac