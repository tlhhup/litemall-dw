package org.tlh.dw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
@Data
@TableName("ads_date_topic")
public class AdsDateTopic {

    @TableId
    private String date;
    private int weekId;
    private int weekDay;
    private int day;
    private int month;
    private int quarter;
    private int year;
    private int isWorkday;
    private int holidayId;
    private long uvCount;
    private long registerCount;
    private long cartCount;
    private long commentCount;
    private long collectCount;
    private long orderCount;
    private BigDecimal orderTotalAmount;
    private BigDecimal paymentCount;
    private BigDecimal paymentTotalAmount;
    private BigDecimal refundCount;
    private BigDecimal refundTotalAmount;
    private long couponCount;

}
