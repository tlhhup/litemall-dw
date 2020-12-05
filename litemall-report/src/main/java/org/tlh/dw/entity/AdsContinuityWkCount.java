package org.tlh.dw.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 最近连续三周活跃用户数
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsContinuityWkCount implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期,一般用结束周周日日期,如果每天计算一次,可用当天日期
     */
    private String dt;

    /**
     * 持续时间
     */
    private String wkDt;

    /**
     * 活跃次数
     */
    private Long continuityCount;


}
