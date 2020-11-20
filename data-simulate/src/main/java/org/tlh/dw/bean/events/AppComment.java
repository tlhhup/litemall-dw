package org.tlh.dw.bean.events;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
public class AppComment {

    private int userId;//用户 id
    private int type;//评论类型，如果type=0，则是商品评论；如果是type=1，则是专题评论；
    private int valueId;//如果type=0，则是商品评论；如果是type=1，则是专题评论。
    private String content;//评论内容
    private int star;//评分， 1-5
    private String addTime;//创建时间

}
