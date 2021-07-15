package org.tlh.profile.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @JsonProperty("value")
    private long count;

    private List<BasicTagFacetVo> child;

    public BasicTagFacetVo(Long id, String name, long count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }
}
