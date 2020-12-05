package org.tlh.dw.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 最近七天连续三天活跃用户数
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsContinuityUvCount implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 最近 7 天日期
     */
    private String wkDt;

    private Long continuityCount;


}
