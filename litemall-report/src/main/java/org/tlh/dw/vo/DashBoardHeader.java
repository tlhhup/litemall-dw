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
    private long registerCount;
    private long orderCount;
    private BigDecimal orderAmount;
    private BigDecimal paymentAmount;
    private BigDecimal refundAmount;
    private double payConvertRate;
    private BigDecimal prePrice;

}
