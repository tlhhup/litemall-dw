package org.tlh.profile.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-24
 */
@Getter
public enum OozieScheduleType {

    DAILY(1, "每天","0 0 * * ?"),
    WEEKLY(2, "每周","0 0 ? 1-12 7"),
    MONTHLY(3, "每月","0 0 1 * ?"),
    YEARLY(4, "每年","0 0 1 1 ?");

    private int type;
    private String name;
    private String frequency;

    OozieScheduleType(int type, String name, String frequency) {
        this.type = type;
        this.name = name;
        this.frequency = frequency;
    }

    public static OozieScheduleType convert(int type) {
        Optional<OozieScheduleType> first = Arrays.stream(values()).filter(item -> item.type == type).findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            throw new IllegalArgumentException("Not Support Type" + type);
        }
    }

}
