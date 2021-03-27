package org.tlh.profile.dto;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-21
 */
@Data
public class BasicTagDto {

    private Long id;
    private String name;
    private String industry;
    private String rule;
    private String business;
    private Integer level = 1;
    private Long pid;
    private Integer order = 0;

}
