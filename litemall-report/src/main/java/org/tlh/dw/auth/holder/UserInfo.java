package org.tlh.dw.auth.holder;

import lombok.Data;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-06
 */
@Data
public class UserInfo {

    private String userName;
    private List<String> roles;
    private List<String> perms;

}
