package org.tlh.dw.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 每日支付总计表
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsPaymentDaycount implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 单日支付笔数
     */
    private Long paymentCount;

    /**
     * 单日支付金额
     */
    private Long paymentAmount;

    /**
     * 单日支付人数
     */
    private Long paymentUserCount;

    /**
     * 单日支付商品数
     */
    private Long paymentSkuCount;

    /**
     * 下单到支付的平均时长，取分钟数
     */
    private Double paymentAvgTime;


}
