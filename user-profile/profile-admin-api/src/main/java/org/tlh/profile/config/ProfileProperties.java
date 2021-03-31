package org.tlh.profile.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.tlh.profile.config.ProfileProperties.PROFILE_PREFIX;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-23
 */
@Data
@ConfigurationProperties(prefix = PROFILE_PREFIX)
public class ProfileProperties {

    public static final String PROFILE_PREFIX = "profile";

    private Admin admin;
    private HDfs hdfs;
    private Oozie oozie;

    @Data
    public static class Admin {
        private String host;
        private String authInfo;
    }

    @Data
    public static class HDfs {
        private String url;
        private String modelPath;
        private String ooziePath;
        private String user;
    }

    @Data
    public static class Oozie {
        private String url;
        private String user;
        private String resourceManager;
        private String nameNode;
        private String sparkOpts;
    }


}
