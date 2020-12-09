package org.tlh.dw.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
@Data
public class DashBoardHeader {

    private long uvCount;
    private double uvRate;

    private long registerCount;

    private long orderCount;
    private double orderRate;

    private BigDecimal orderAmount;

    private BigDecimal paymentAmount;
    private double paymentRate;

    private BigDecimal refundAmount;
    private double refundRate;

    private double payConvertRate;
    private double payConvertRateRate;

    private BigDecimal prePrice;
    private double prePriceRate;

}
