package org.tlh.litemall.admin.holder;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
public final class UserInfoHolder {

    private static final ThreadLocal<UserInfo> holder = new ThreadLocal<>();

    private UserInfoHolder() {

    }

    public static void setUserInfo(UserInfo userInfo) {
        holder.set(userInfo);
    }

    public static UserInfo getUserInfo() {
        return holder.get();
    }

    public static void clean() {
        holder.remove();
    }

}
