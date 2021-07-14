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
 * 基础标签
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbBasicTag implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 行业、子行业、业务类型、标签、属性
     */
    private String industry;

    /**
     * 标签规则: 四级 metadata表中数据 五级 值域
     */
    private String rule;

    /**
     * 业务描述
     */
    private String business;

    /**
     * 标签等级
     */
    private Integer level;

    /**
     * 父标签ID
     */
    private Long pid;

    /**
     * 子标签排序字段
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态：1申请中、2开发中、3开发完成、4已上线、5已下线、6已禁用
     */
    private Integer state;

    /**
     * 备注
     */
    private String remark;

    /**
     * hbase 中字段名
     */
    private String hbaseFields;

}
