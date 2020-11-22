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

    private ShipProps ship;

    private PaymentProps payment;

    private CommentProps comment;

    private RefundProps refund;

    private ConfirmProps confirm;

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
        private int joinActivityRate;
        private boolean useCoupon;
        private int useCouponRate;
    }

    @Data
    public static class CouponProps{
        private int userCount;
    }

    @Data
    public static class ShipProps{
        private int rate;
        private List<String> shipChannel;
    }

    @Data
    public static class PaymentProps{
        private int rate;
        private List<Integer> paymentType;
    }

    @Data
    public static class CommentProps{
        private int rate;
        private List<Integer> appraiseRate;
    }

    @Data
    public static class RefundProps{
        private int rate;
        private List<Integer> reasonRate;
    }

    @Data
    public static class ConfirmProps{
        private int rate;
    }

}
