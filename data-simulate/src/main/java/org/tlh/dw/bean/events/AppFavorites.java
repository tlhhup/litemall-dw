package org.tlh.dw.bean.events;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
public class AppFavorites {

    private int id;//主键
    private int courseId;//商品id
    private int userId;//用户ID
    private String addTime;//创建时间

}
