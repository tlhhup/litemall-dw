package org.tlh.dw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.tlh.dw.auth.AuthService;
import org.tlh.dw.auth.filter.AuthenticationFilter;
import org.tlh.dw.auth.interceptor.AuthorizationInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor()).addPathPatterns("/**");
    }

    @Autowired
    private AuthService authService;

    //注册过滤器
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setAuthService(authService);
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("authenticationFilter");
        registration.setOrder(1);
        return registration;
    }

}
