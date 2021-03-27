package org.tlh.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.tlh.profile.config.ProfileProperties;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-20
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties(ProfileProperties.class)
public class UserProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProfileApplication.class, args);
    }

}
