package org.tlh.profile.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 1 RDBMS 2 File 3 Hbase 4 Hive
 *
 * @author 离歌笑
 * @desc
 * @date 2021-03-24
 */
@Getter
public enum MetaDataType {

    MYSQL(1, "mysql"),
    HDFS(2, "hdfs"),
    HBASE(3, "hbase"),
    HIVE(4, "hive");

    private int type;
    private String name;

    MetaDataType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static MetaDataType convert(String type) {
        Optional<MetaDataType> first = Arrays.stream(values()).filter(item -> item.name.equalsIgnoreCase(type)).findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            throw new IllegalArgumentException("Not Support Type" + type);
        }
    }

}
