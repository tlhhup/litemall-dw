package org.tlh.profile.dto;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-27
 */
@Data
public class ApproveModelDto {

    private long tagId;
    private long modelId;
    private int state;
    private String remake;

}
