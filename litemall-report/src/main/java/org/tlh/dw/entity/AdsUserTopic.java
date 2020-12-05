package org.tlh.dw.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员主题信息表
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsUserTopic implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 活跃会员数
     */
    private Long dayUsers;

    /**
     * 新增会员数
     */
    private Long dayNewUsers;

    /**
     * 新增消费会员数
     */
    private Long dayNewPaymentUsers;

    /**
     * 总付费会员数
     */
    private Long paymentUsers;

    /**
     * 总会员数
     */
    private Long users;

    /**
     * 会员活跃率
     */
    private BigDecimal dayUsers2users;

    /**
     * 会员付费率
     */
    private BigDecimal paymentUsers2users;

    /**
     * 会员新鲜度
     */
    private BigDecimal dayNewUsers2users;


}
