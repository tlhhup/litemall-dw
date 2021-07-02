package org.tlh.litemall.admin.exception;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
public class AuthenticationException extends IllegalStateException{

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
