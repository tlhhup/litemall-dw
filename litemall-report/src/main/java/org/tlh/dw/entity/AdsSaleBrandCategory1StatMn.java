package org.tlh.dw.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 品牌复购率统计
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsSaleBrandCategory1StatMn implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 品牌 id
     */
    private Integer brandId;

    /**
     * 1级品类id 
     */
    private Integer category1Id;

    /**
     * 1 级品类名称 
     */
    private String category1Name;

    /**
     * 购买人数
     */
    private Long buycount;

    /**
     * 两次以上购买人数
     */
    private Long buyTwiceLast;

    /**
     * 单次复购率
     */
    private BigDecimal buyTwiceLastRatio;

    /**
     * 三次以上购买人数
     */
    private Long buy3timesLast;

    /**
     * 多次复购率
     */
    private BigDecimal buy3timesLastRatio;

    /**
     * 统计月份
     */
    private String statMn;

    /**
     * 统计日期
     */
    private String statDate;


}
