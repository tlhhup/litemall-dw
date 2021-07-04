package org.tlh.profile.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicTagFacetVo {

    private Long id;
    private String name;
    private long count;

}
