package org.tlh.dw.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 每日新增设备信息数量
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsNewMidCount implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 创建时间
     */
    private String dt;

    /**
     * 新增设备数量
     */
    private Long newMidCount;


}
