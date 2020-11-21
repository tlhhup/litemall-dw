package org.tlh.dw.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tlh.dw.service.*;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-19
 */
@Slf4j
@Component
public class BusinessService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private FavorInfoService favorInfoService;

    @Autowired
    private CartInfoService cartInfoService;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private OrderRefundInfoService orderRefundInfoService;

    @Autowired
    private CommentInfoService commentInfoService;

    public void process(){
        log.info("business simulate process .....");
        log.info("--------开始生成用户数据--------");
        this.userInfoService.genUserInfo();
        log.info("--------开始生成收藏数据--------");
        this.favorInfoService.genFavors();
        log.info("--------开始生成购物车数据--------");
        this.cartInfoService.genCartInfo();
        log.info("--------开始生成订单数据--------");
        this.orderInfoService.genOrderInfo();
        log.info("--------开始生成支付数据--------");
        this.paymentInfoService.genPayments();
        log.info("--------开始生成退单数据--------");
        this.orderRefundInfoService.genRefundsOrFinish();
        log.info("--------开始生成评论数据--------");
        this.commentInfoService.genComments();
    }

}
