package org.tlh.dw.rest;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import okhttp3.RequestBody;
import org.tlh.dw.dto.ResultMsg;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@RetrofitClient(baseUrl = "${simulate.baseUrl}")
public interface UserAction {

    @POST("/process")
    Response<ResultMsg> postAction(@Body RequestBody data);

}
