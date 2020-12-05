package org.tlh.dw.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 本周回流用户数
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsBackCount implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 统计日期所在周
     */
    private String wkDt;

    /**
     * 回流设备数
     */
    private Long wastageCount;


}
