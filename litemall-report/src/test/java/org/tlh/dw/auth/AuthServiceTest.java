package org.tlh.dw.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.dw.auth.holder.UserInfo;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-06
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    public void getUserInfo() {
        String token="4d5a0e91-c1fd-4ca8-9797-618796e76cab";
        UserInfo userInfo = this.authService.getUserInfo(token);

        System.out.println(userInfo);
    }
}