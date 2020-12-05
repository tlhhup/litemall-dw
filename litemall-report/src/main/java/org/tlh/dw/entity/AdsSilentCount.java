package org.tlh.dw.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 沉默用户数
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdsSilentCount implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 沉默设备数
     */
    private Long silentCount;


}
