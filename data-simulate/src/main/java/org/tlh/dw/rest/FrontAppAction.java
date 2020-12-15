package org.tlh.dw.rest;

import com.github.lianjiatech.retrofit.spring.boot.annotation.Intercept;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import org.linlinjava.litemall.db.domain.LitemallCart;
import org.tlh.dw.dto.*;
import org.tlh.dw.mock.interceptor.TokenInterceptor;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

/**
 * app 前端行为模拟，用于后端埋点，进行实时计算
 *
 * @author 离歌笑
 * @desc
 * @date 2020-12-14
 */
@RetrofitClient(baseUrl = "${simulate.front-app.base-url}")
@Intercept(handler = TokenInterceptor.class, exclude = {"/wx/auth/**"})
public interface FrontAppAction {

    /**
     * 用户注册
     *
     * @param userRegister
     */
    @POST("/wx/auth/register")
    Response<Void> register(@Body UserRegister userRegister);

    /**
     * 用户登录
     *
     * @param userBase
     * @return 返回 获取token
     */
    @POST("/wx/auth/login")
    Response<Map<String, Object>> login(@Body UserBase userBase);

    /**
     * 加购
     *
     * @param cart
     */
    @POST("/wx/cart/add")
    Response<Void> addCart(@Body LitemallCart cart);

    /**
     * 下单
     *
     * @param orderSubmit
     */
    @POST("/wx/order/submit")
    Response<Void> order(@Body OrderSubmit orderSubmit);

    /**
     * 支付
     *
     * @param orderConfirm
     */
    @POST("/wx/order/simulatePay")
    Response<Void> payment(@Body OrderConfirm orderConfirm);

    /**
     * 确认收货
     *
     * @param orderConfirm
     */
    @POST("/wx/order/confirm")
    Response<Void> confirm(@Body OrderConfirm orderConfirm);

    /**
     * 退货
     *
     * @param orderConfirm
     */
    @POST("/wx/order/refund")
    Response<Void> refund(@Body OrderConfirm orderConfirm);

    /**
     * 订单评论
     *
     * @param comment
     */
    @POST("/wx/order/comment")
    Response<Void> comment(@Body OrderComment comment);

    /**
     * 收藏
     *
     * @param collect
     */
    @POST("/wx/collect/addordelete")
    Response<Void> collect(@Body CollectDto collect);

}
