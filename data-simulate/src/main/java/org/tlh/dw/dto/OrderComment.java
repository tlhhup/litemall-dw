package org.tlh.dw.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-14
 */
@Data
public class OrderComment {

    private int orderGoodsId;//订单详情ID
    private String content;
    private int star;
    private boolean hasPicture;
    private List<String> picUrls;

}
