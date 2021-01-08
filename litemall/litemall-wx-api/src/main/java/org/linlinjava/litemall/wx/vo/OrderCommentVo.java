package org.linlinjava.litemall.wx.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单评价
 *
 * @author 离歌笑
 * @desc
 * @date 2021-01-07
 */
public class OrderCommentVo {

    private Integer id;
    private Integer orderId;
    private Integer goodsId;
    private String goodsName;
    private String goodsSn;
    private Integer productId;
    private String picUrl;
    private Integer comment;
    // 评价信息
    private String message = "";
    private int star = 5;
    private List<VantUploaderVo> commentPics = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public List<VantUploaderVo> getCommentPics() {
        return commentPics;
    }

    public void setCommentPics(List<VantUploaderVo> commentPics) {
        this.commentPics = commentPics;
    }
}
