package org.tlh.dw.entity.ck;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-08-31
 */
@Data
@TableName("dws_order_wide_all")
public class OrderWide implements Serializable {

    private long orderId;
    private long userId;
    private long province;
    private long city;
    private long country;
    private double actualPrice;
    private double orderPrice;
    private double goodsPrice;
    private double freightPrice;
    private double couponPrice;
    private double integralPrice;
    private Date addTime;
    private boolean isFirstOrder;
    private String provinceName;
    private int provinceCode;
    private String cityName;
    private int cityCode;
    private int countryCode;
    private String countryName;
    private String userAgeGroup;
    private String userGender;
    private long orderDetailId;
    private long goodsId;
    private String goodsName;
    private int number;
    private double price;
    private int categoryId;
    private String categoryName;
    private int brandId;
    private String brandName;
    private double capitationPrice;

}
