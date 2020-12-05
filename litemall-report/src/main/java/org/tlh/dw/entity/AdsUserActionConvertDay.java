package org.tlh.dw.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户行为漏斗分析
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsUserActionConvertDay implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 总访问人数
     */
    private Long totalVisitorMCount;

    /**
     * 加入购物车的人数
     */
    private Long cartUCount;

    /**
     * 访问到加入购物车转化率
     */
    private BigDecimal visitor2cartConvertRatio;

    /**
     * 下单人数
     */
    private Long orderUCount;

    /**
     * 加入购物车到下单转化率
     */
    private BigDecimal cart2orderConvertRatio;

    /**
     * 支付人数
     */
    private Long paymentUCount;

    /**
     * 下单到支付的转化率
     */
    private BigDecimal order2paymentConvertRatio;


}
