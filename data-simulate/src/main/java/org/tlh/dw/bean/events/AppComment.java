package org.tlh.dw.bean.events;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
public class AppComment {

    private int commentId;//评论表
    private int userId;//用户 id
    private int pCommentId;//父级评论id(为0则是一级评论,不为0则是回复) private String content;//评论内容
    private String content;//评论内容
    private String addTime;//创建时间
    private int otherId;//评论的相关 id
    private int praiseCount;//点赞数量
    private int replyCount;//回复数量

}
