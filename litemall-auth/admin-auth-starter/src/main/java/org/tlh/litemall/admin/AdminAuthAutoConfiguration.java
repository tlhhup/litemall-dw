package org.tlh.litemall.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.tlh.litemall.admin.filter.AuthenticationFilter;
import org.tlh.litemall.admin.interceptor.AuthorizationInterceptor;
import org.tlh.litemall.admin.service.AuthService;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
@Configuration
@ConditionalOnClass(DispatcherServlet.class)
@EnableConfigurationProperties(AdminAuthProperties.class)
@ConditionalOnProperty(name = "auth.admin.enable", havingValue = "true")
public class AdminAuthAutoConfiguration {

    @Bean
    public AuthService authService() {
        return new AuthService();
    }

    //注册过滤器
    @Bean
    public FilterRegistrationBean filterRegistrationBean(@Autowired AuthService authService) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setAuthService(authService);
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("authenticationFilter");
        registration.setOrder(1);
        return registration;
    }

    @Slf4j
    @Order(100)
    @Configuration
    public static class PermissionConfig implements WebMvcConfigurer {

        public PermissionConfig(){
            log.info("init Permission.");
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            log.info("register permission interceptor.");
            registry.addInterceptor(new AuthorizationInterceptor()).addPathPatterns("/**");
        }
    }

}
