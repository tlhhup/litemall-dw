package org.tlh.dw.mock.interceptor;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-14
 */
public final class TokenHolder {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    private TokenHolder() {
    }

    public static void setToken(String token) {
        holder.set(token);
    }

    public static String getToken() {
        return holder.get();
    }

    public static void removeToken() {
        holder.remove();
    }

}
