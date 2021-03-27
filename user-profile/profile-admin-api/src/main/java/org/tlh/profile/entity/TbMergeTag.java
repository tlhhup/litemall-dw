package org.tlh.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 组合标签
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbMergeTag implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组合标签名称
     */
    private String name;

    /**
     * 组合标签条件
     */
    private String condition;

    /**
     * 组合标签含义
     */
    private String intro;

    /**
     * 组合用途
     */
    private String purpose;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 状态：1申请中、2开发中、3开发完成、4已上线、5已下线、6已禁用
     */
    private Integer state;


}
