package org.tlh.profile.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 状态：1申请中、2开发中、3开发完成、4已上线、5已下线、6已禁用
 *
 * @author 离歌笑
 * @desc
 * @date 2021-03-24
 */
@Getter
public enum ModelTaskState {

    SUBMIT(1, "申请中"),
    DEVELOPING(2, "开发中"),
    DEVELOPED(3, "开发完成"),
    ONLINE(4, "已上线"),
    OFFLINE(5, "已下线"),
    DISABLED(6, "已禁用"),
    REJECT(7, "不通过"),
    STOPPED(8, "暂停");

    private int state;
    private String desc;

    ModelTaskState(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public static ModelTaskState convert(int state) {
        Optional<ModelTaskState> first = Arrays.stream(values()).filter(item -> item.state == state).findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            throw new IllegalArgumentException("Not Support Type" + state);
        }
    }

}
