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

    DAILY(1, "每天","${coord:endOfDays(1)}"),
    WEEKLY(2, "每周","${coord:endOfWeeks(1)}"),
    MONTHLY(3, "每月","${coord:endOfMonths(1)}"),
    YEARLY(4, "每年","${coord:endOfMonths(12)}");

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
