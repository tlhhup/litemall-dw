package org.tlh.dw.auth.filter;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tlh.dw.auth.AuthService;
import org.tlh.dw.auth.holder.UserInfo;
import org.tlh.dw.auth.holder.UserInfoHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-06
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String LOGIN_TOKEN_KEY = "X-Litemall-Admin-Token";

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isValid = false;
        String token = request.getHeader(LOGIN_TOKEN_KEY);
        //1.校验是否携带token
        if (!StringUtils.isEmpty(token)) {
            //2.校验token是否有效
            UserInfo userInfo = this.authService.getUserInfo(token);
            if (userInfo != null) {
                //3.将数据缓存
                UserInfoHolder.setUserInfo(userInfo);
                isValid = true;
            }
        }
        //4.分发结果
        if (isValid) {
            filterChain.doFilter(request, response);
        } else {
            // todo 处理失败的数据格式
        }
    }

}
