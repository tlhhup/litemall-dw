package org.tlh.dw.mock.interceptor;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-14
 */
@Component
public class TokenInterceptor extends BasePathMatchInterceptor {

    private static final String LOGIN_TOKEN_KEY = "X-Litemall-Token";

    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        Request request = chain.request();
        // 添加请求头
        String token = TokenHolder.getToken();
        if (StringUtils.hasText(token)) {
            request = request.newBuilder().addHeader(LOGIN_TOKEN_KEY, token).build();
        }
        // 执行请求
        Response proceed = chain.proceed(request);
        // 清空
        TokenHolder.removeToken();
        return proceed;
    }

}
