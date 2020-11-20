package org.tlh.dw.bean.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppPraise {

    private int id; //主键id
    private int userId;//用户id
    private int targetId;//点赞的对象id
    private int type;//点赞类型1 问答点赞2 问答评论点赞3 文章点赞数4 评论点赞
    private String addTime;//添加时间

}
