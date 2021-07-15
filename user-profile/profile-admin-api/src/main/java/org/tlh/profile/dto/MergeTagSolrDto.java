package org.tlh.profile.dto;

import lombok.Data;

/**
 * 构建组合标签solr的条件查询
 *
 * @author 离歌笑
 * @desc
 * @date 2021-07-15
 */
@Data
public class MergeTagSolrDto {

    private int id;
    private String filedName;
    private int filedTagId;
    private String tagName;
    private int condition;
    private int order;

}
