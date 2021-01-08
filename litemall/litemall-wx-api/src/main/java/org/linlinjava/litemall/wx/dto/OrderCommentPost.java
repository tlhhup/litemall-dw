package org.linlinjava.litemall.wx.dto;

import org.linlinjava.litemall.wx.vo.OrderCommentVo;

import java.io.Serializable;
import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-08
 */
public class OrderCommentPost implements Serializable {

    // 商品评价信息
    private List<OrderCommentVo> orderGoods;

    // 物流评价信息
    private int shipStar;
    private int shipSpeedStar;
    private int shiperStar;

    // 订单编号
    private int orderId;

    public List<OrderCommentVo> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(List<OrderCommentVo> orderGoods) {
        this.orderGoods = orderGoods;
    }

    public int getShipStar() {
        return shipStar;
    }

    public void setShipStar(int shipStar) {
        this.shipStar = shipStar;
    }

    public int getShipSpeedStar() {
        return shipSpeedStar;
    }

    public void setShipSpeedStar(int shipSpeedStar) {
        this.shipSpeedStar = shipSpeedStar;
    }

    public int getShiperStar() {
        return shiperStar;
    }

    public void setShiperStar(int shiperStar) {
        this.shiperStar = shiperStar;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
