package org.tlh.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-20
 */
@SpringBootApplication
@EnableTransactionManagement
public class UserProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProfileApplication.class,args);
    }

}
