package org.tlh.profile.dto;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-22
 */
@Data
public class ModelTagDto extends BasicTagDto {

    private Long modelId;
    private int schedule;//调度规则
    private String[] starEnd;//起止时间
    private String modelMain;//spark主类
    private String modelName;//模型名称
    private String modelPath;//jar包在hdfs中的地址
    private String modelArgs;//模型参数
    private String sparkOpts;//spark执行参数
    private int modelType;

}
