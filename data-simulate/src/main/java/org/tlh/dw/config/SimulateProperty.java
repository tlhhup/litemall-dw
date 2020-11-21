package org.tlh.dw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
@Component
@ConfigurationProperties(prefix = "simulate")
public class SimulateProperty {

    private String date;

    private UserProps user;

    private FavorProps favor;

    private CartProps cart;

    private OrderProps order;

    private CouponProps coupon;

    private PaymentProps payment;

    private CommentProps comment;

    private RefundProps refund;

    @Data
    public static class UserProps{
        private int count;
        private int maleRate;
        private int updateRate;
    }

    @Data
    public static class FavorProps{
        private int count;
        private int cancelRate;
    }

    @Data
    public static class CartProps{
        private int count;
        private int skuMaxCountPerCart;
    }

    @Data
    public static class OrderProps{
        private int userRate;
        private int skuRate;
        private boolean joinActivity;
        private boolean useCoupon;
    }

    @Data
    public static class CouponProps{
        private int userCount;
    }

    @Data
    public static class PaymentProps{
        private int rate;
        private List<Integer> paymentType;
    }

    @Data
    public static class CommentProps{
        private List<Integer> appraiseRate;
    }

    @Data
    public static class RefundProps{
        private List<Integer> reasonRate;
    }

}
