package org.tlh.dw.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.dw.bean.GoodsComment;
import org.tlh.dw.bean.GoodsFavorites;
import org.tlh.dw.bean.ResultMsg;
import retrofit2.Response;

import java.util.Date;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsActionTest {

    @Autowired
    private GoodsAction goodsAction;

    @Test
    public void goodsList() throws Exception {
        Response<ResultMsg> result = this.goodsAction.goodsList();
        System.out.println(result.body().getMsg());
    }

    @Test
    public void goodsComment(){
        GoodsComment goodsComment=new GoodsComment();
        goodsComment.setAddTime(new Date());
        goodsComment.setCommentId(1);
        goodsComment.setOtherId(1);
        goodsComment.setPCommentId(2);
        goodsComment.setPraiseCount(3);
        goodsComment.setReplyCount(4);
        goodsComment.setUserId(2);
        Response<ResultMsg> result = this.goodsAction.goodsComment(goodsComment);
        System.out.println(result.body().getMsg());
    }

    @Test
    public void goodsFavor() {
        GoodsFavorites goodsFavorites = new GoodsFavorites(1, 1, 1, new Date());
        Response<ResultMsg> result = this.goodsAction.goodsFavor(goodsFavorites);
        System.out.println(result.body().getMsg());
    }

}