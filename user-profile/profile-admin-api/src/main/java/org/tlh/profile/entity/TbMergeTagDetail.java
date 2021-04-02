package org.tlh.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 组合标签规则详情
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbMergeTagDetail implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组合标签ID
     */
    private Long mergeTagId;

    /**
     * 基础标签ID（1级行业 or 5级属性）
     */
    private Long basicTagId;

    /**
     * 条件间关系： 1 and 2 or 3 not
     */
    @TableField("`condition`")
    private Integer condition;

    /**
     * 条件顺序
     */
    private Integer conditionOrder;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
