package org.tlh.profile.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.tlh.profile.util.ResponseUtil;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-25
 */
@Slf4j
@Order
@RestControllerAdvice
public class UserProfileExceptionHandler {

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    public Object handleIllegalState(IllegalStateException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.fail(507,e.getMessage());
    }

}
