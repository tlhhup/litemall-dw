package org.tlh.dw.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对请求中的分页/排序参数是否采取封装
 * <br>
 * page、limit、order、sort
 * @author 离歌笑
 * @desc
 * @date 2020-12-06
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageRequest {

    /**
     * 是否是必须的
     *
     * @return
     */
    boolean require() default true;

    /**
     * 当前页参数名
     * @return
     */
    String page() default "page";

    /**
     * 分页大小参数名
     * @return
     */
    String limit() default "limit";

    /**
     * 排序参数名
     * @return
     */
    String sort() default "sort";

    /**
     * 排序方式参数名
     * @return
     */
    String order() default "order";

}

