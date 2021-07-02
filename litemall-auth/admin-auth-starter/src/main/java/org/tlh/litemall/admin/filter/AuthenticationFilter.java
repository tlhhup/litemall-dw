package org.tlh.litemall.admin.filter;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tlh.litemall.admin.service.AuthService;
import org.tlh.litemall.admin.exception.AuthenticationException;
import org.tlh.litemall.admin.holder.UserInfo;
import org.tlh.litemall.admin.holder.UserInfoHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String LOGIN_TOKEN_KEY = "X-Litemall-Admin-Token";

    private static final int USER_STATUS_ERROR = 501;
    private static final int SYSTEM_INTERNAL_ERROR = 502;

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
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
                sendErrorResponse(response, USER_STATUS_ERROR);
            }
        } catch (AuthenticationException e) {
            sendErrorResponse(response, SYSTEM_INTERNAL_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int code) throws IOException {
        String result = String.format("{\"errno\":%d}", code);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(result);
    }

}
