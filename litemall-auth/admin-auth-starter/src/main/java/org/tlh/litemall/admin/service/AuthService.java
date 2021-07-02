package org.tlh.litemall.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.tlh.litemall.admin.AdminAuthProperties;
import org.tlh.litemall.admin.exception.AuthenticationException;
import org.tlh.litemall.admin.holder.UserInfo;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
@Slf4j
public class AuthService {


    @Autowired
    private AdminAuthProperties adminAuthProperties;

    private RestTemplate restTemplate;

    private String url;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.url = this.adminAuthProperties.getAuthInfoUrl();
    }

    public UserInfo getUserInfo(String token) {
        try {
            //设置请求参数
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("X-Litemall-Admin-Token", token);
            //设置get请求参数
            Map<String, String> params = new HashMap<>();
            params.put("token", token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(params, requestHeaders);
            //发送请求
            ResponseEntity<Map> exchange = this.restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = exchange.getBody();
                if (body != null && (int) body.get("errno") == 0) {
                    return parseUserInfo(body.get("data"));
                }
            }
        } catch (Exception e) {
            log.info("get user info error: {}", e.getMessage());
            throw new AuthenticationException("Query User Info error", e);
        }
        return null;
    }

    private UserInfo parseUserInfo(Object data) {
        if (data != null && data instanceof Map) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(((Map) data).get("name").toString());
            userInfo.setRoles((List<String>) ((Map) data).get("roles"));
            userInfo.setPerms((List<String>) ((Map) data).get("perms"));
            return userInfo;
        } else {
            return null;
        }
    }

}
