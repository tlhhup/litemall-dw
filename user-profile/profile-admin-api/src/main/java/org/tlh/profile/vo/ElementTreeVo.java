package org.tlh.profile.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-21
 */
@Data
public class ElementTreeVo {

    private long id;
    private String label;
    private boolean leaf;
    private List<ElementTreeVo> children;

    public boolean isLeaf() {
        return children == null || children.size() == 0;
    }
}
