package org.tlh.dw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-10
 */
@Data
@TableName("ads_region_order_daycount")
public class AdsRegionDayCount {

    @TableId
    private String date;
    private int provinceId;
    private String provinceName;
    private int cityId;
    private String cityName;
    private int countryId;
    private String countryName;
    private String orderDateFirst;
    private String orderDateLast;
    private long orderCount;
    private BigDecimal orderAmount;
    private long orderDayCount;
    private BigDecimal orderDayAmount;
}
