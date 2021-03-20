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
 * 标签模型
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbTagModel implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签ID(四级标签)
     */
    private Long tagId;

    /**
     * 模型类型：1 匹配 2 统计 3 挖掘
     */
    private Integer modelType;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型driver全限定名
     */
    private String modelMain;

    /**
     * 模型在hdfs中的地址
     */
    private String modelPath;

    /**
     * 模型jar包文件名
     */
    private String modelJar;

    /**
     * 模型参数
     */
    private String modelArgs;

    /**
     * spark的执行参数
     */
    private String sparkOpts;

    /**
     * oozie的调度规则
     */
    private String scheduleRule;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态
     */
    private Integer state;


}
