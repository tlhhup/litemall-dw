package org.tlh.dw.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 每日用户留存情况
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsUserRetentionDayRate implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String statDate;

    /**
     * 设备新增日期
     */
    private String createDate;

    /**
     * 截止当前日期留存天数
     */
    private Integer retentionDay;

    /**
     * 设备新增数量
     */
    private Long newMidCount;

    /**
     * 留存数量
     */
    private Long retentionCount;

    /**
     * 留存率
     */
    private BigDecimal retentionRatio;


}
