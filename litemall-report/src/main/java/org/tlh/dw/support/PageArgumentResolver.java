package org.tlh.dw.support;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.tlh.dw.support.annotations.PageRequest;

import java.util.Optional;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-06
 */
public class PageArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PageRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //1.获取注解信息
        PageRequest pageRequest = parameter.getParameterAnnotation(PageRequest.class);
        if (!pageRequest.require()) {
            return null;
        }
        //2.解析分页参数
        String order = webRequest.getParameter(pageRequest.order());
        if (!PageParam.isValidateOrder(order)) {
            throw new IllegalArgumentException(String.format("Except order parameter is desc or asc, but get [%s]", order));
        }
        String currentPage = webRequest.getParameter(pageRequest.page());
        String limit = webRequest.getParameter(pageRequest.limit());
        String sort = webRequest.getParameter(pageRequest.sort());

        PageParam arg = new PageParam();
        arg.setAsc(PageParam.asc(order));
        arg.setSort(sort);
        arg.setLimit(Integer.parseInt(Optional.of(limit).orElse("20")));
        arg.setPage(Integer.parseInt(Optional.of(currentPage).orElse("1")));

        return arg;
    }

}
