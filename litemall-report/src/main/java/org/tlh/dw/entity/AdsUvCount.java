package org.tlh.dw.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 活跃设备数
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsUvCount implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 当日用户数量
     */
    private Long dayCount;

    /**
     * 当周用户数量
     */
    private Long wkCount;

    /**
     * 当月用户数量
     */
    private Long mnCount;

    /**
     * Y,N 是否是周末,用于得到本周最终结果
     */
    private String isWeekend;

    /**
     * Y,N 是否是月末,用于得到本月最终结果
     */
    private String isMonthend;


}
