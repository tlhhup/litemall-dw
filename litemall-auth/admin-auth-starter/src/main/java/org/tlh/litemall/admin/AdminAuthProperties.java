package org.tlh.litemall.admin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
@Data
@ConfigurationProperties(prefix = AdminAuthProperties.PREFIX)
public class AdminAuthProperties {

    public static final String PREFIX = "auth.admin";

    private String host;
    private String authInfo;


    public String getAuthInfoUrl() {
        return this.host.concat(this.authInfo);
    }

}
