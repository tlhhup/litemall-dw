package org.tlh.dw.support;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-06
 */
@Data
public class PageParam {

    private static final String ASC = "asc";
    private static final String DESC = "desc";

    private int page;
    private int limit;
    private String sort;
    private boolean asc;

    public static boolean asc(String order) {
        return ASC.equals(order);
    }

    public static boolean isValidateOrder(String order) {
        if (ASC.equals(order) || DESC.equals(order)) {
            return true;
        } else {
            return false;
        }
    }

}
