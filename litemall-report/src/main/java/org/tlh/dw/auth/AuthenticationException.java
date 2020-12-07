package org.tlh.dw.auth;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-07
 */
public class AuthenticationException extends IllegalStateException {

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
