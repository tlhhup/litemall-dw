package org.tlh.dw.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-05
 */
public interface Constants {

    // 实时计算
    // 用户
    String USER_LOGIN_COUNT = "user:login:count:";
    String USER_ORDER_COUNT = "user:order:count:";
    String USER_ORDER_AMOUNT = "user:order:amount:";
    String USER_PAY_COUNT = "user:pay:count:";
    String USER_PAY_AMOUNT = "user:pay:amount:";
    String USER_REFUND_COUNT = "user:refund:count:";
    String USER_REFUND_AMOUNT = "user:refund:amount:";

    //订单
    String ORDER_SPEED = "order:speed:";
    String ORDER_COUNT = ":order:count";
    String ORDER_AMOUNT = ":order:amount";
    String PAY_COUNT = ":pay:count";
    String PAY_AMOUNT = ":pay:amount";
    String REFUND_COUNT = ":refund:count";
    String REFUND_AMOUNT = ":refund:amount";
    String CONFIRM_COUNT = ":confirm:count";
    String CONFIRM_AMOUNT = ":confirm:amount";

    //商品
    String GOODS_CART = ":goods:cart";
    String GOODS_COLLECT = ":goods:collect";
    String GOODS_ORDER = ":goods:order";
    String GOODS_COMMENT_GOOD = ":goods:comment:good";
    String GOODS_COMMENT_BAD = ":goods:comment:bad";

    // 特殊地区
    List<String> SPECIAL_REGIONS = Arrays.asList(
            "内蒙古", "广西", "西藏", "宁夏", "新疆",
            "北京", "天津", "天津", "重庆");

    //区域
    String REGION_ORDER_COUNT = ":region:order:count";
    int REGION_ORDER_COUNT_TOP_N = 10;

}
