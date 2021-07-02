package org.tlh.litemall.admin.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.tlh.litemall.admin.annotation.RequiresPermissions;
import org.tlh.litemall.admin.holder.UserInfo;
import org.tlh.litemall.admin.holder.UserInfoHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            //1.获取权限注解信息
            RequiresPermissions requiresPermissions = ((HandlerMethod) handler).getMethodAnnotation(RequiresPermissions.class);
            if (requiresPermissions != null) {
                //2.获取需要的权限
                String[] permissions = requiresPermissions.value();
                //3.获取登录用户的权限
                UserInfo userInfo = UserInfoHolder.getUserInfo();
                //4.校验是否具有该权限
                return checkPermissions(permissions, userInfo);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserInfoHolder.clean();
    }

    private boolean checkPermissions(String[] permissions, UserInfo userInfo) {
        boolean isValid = false;
        //1.用户权限
        List<String> perms = userInfo.getPerms();
        if (!ObjectUtils.isEmpty(perms)) {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            //2.验证是否拥有该权限
            for (String perm : perms) {
                for (String permission : permissions) {
                    if (antPathMatcher.match(perm, permission)) {
                        isValid = true;
                        break;
                    }
                }
                //3.一旦验证成功则结束
                if (isValid) {
                    break;
                }
            }
        }
        return isValid;
    }

}
