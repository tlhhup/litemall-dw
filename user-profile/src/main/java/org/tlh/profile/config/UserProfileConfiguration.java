package org.tlh.profile.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tlh.profile.util.HDfsUtils;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-23
 */
@Configuration
public class UserProfileConfiguration {

    @Autowired
    private ProfileProperties profileProperties;

    @Bean(destroyMethod = "close")
    public HDfsUtils hDfsUtils() {
        ProfileProperties.HDfs hdfs = profileProperties.getHdfs();
        return new HDfsUtils(hdfs.getUrl(), hdfs.getUser());
    }

}
