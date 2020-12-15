package org.tlh.dw.dto;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-14
 */
@Data
public class OrderSubmit {

    private int cartId;
    private int addressId;
    private int couponId;
    private int userCouponId;
    private String message;
    private int grouponRulesId;
    private int grouponLinkId;

}
