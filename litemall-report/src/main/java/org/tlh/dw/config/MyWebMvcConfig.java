package org.tlh.dw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.tlh.dw.support.PageArgumentResolver;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-06
 */
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageArgumentResolver());
    }
}
