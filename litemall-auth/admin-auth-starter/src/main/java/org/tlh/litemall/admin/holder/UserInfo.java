package org.tlh.litemall.admin.holder;

import lombok.Data;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-02
 */
@Data
public class UserInfo {

    private String userName;
    private List<String> roles;
    private List<String> perms;

}
