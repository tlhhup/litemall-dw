package org.tlh.dw.rest;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import org.tlh.dw.bean.GoodsComment;
import org.tlh.dw.bean.GoodsFavorites;
import org.tlh.dw.bean.ResultMsg;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@RetrofitClient(baseUrl = "${simulate.baseUrl}")
public interface GoodsAction {

    @POST("/process")
    Response<ResultMsg> goodsList();

    @POST("/process")
    Response<ResultMsg> goodsDetail();

    @POST("/process")
    Response<ResultMsg> goodsComment(@Body GoodsComment goodsComment);

    @POST("/process")
    Response<ResultMsg> goodsFavor(@Body GoodsFavorites goodsFavorites);

}
