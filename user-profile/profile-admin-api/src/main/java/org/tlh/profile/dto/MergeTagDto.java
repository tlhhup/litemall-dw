package org.tlh.profile.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-02
 */
@Data
public class MergeTagDto {

    private String name;
    private String condition;
    private String intro;
    private String purpose;

    //组合标签规则
    private List<MergeTagDetailDto> tags;

}
