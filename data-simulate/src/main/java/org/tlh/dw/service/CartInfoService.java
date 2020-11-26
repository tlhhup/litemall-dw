package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallCartMapper;
import org.linlinjava.litemall.db.dao.LitemallGoodsProductMapper;
import org.linlinjava.litemall.db.domain.LitemallCart;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoodsProduct;
import org.linlinjava.litemall.db.domain.LitemallGoodsProductExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.ParamUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class CartInfoService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private LitemallCartMapper cartMapper;

    @Autowired
    private LitemallGoodsProductMapper goodsProductMapper;

    private Random random = new Random();

    public void genCartInfo() {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int count = this.simulateProperty.getCart().getCount();

        for (int i = 0; i < count; i++) {
            int userId = this.commonDataService.randomUserId();
            LitemallCart cart = init(userId, this.commonDataService.randomGoods(), localDateTime);
            this.cartMapper.insert(cart);
        }

        log.info("共生成购物车{}条", count);
    }

    private LitemallCart init(int userId, LitemallGoods goods, LocalDateTime dateTime) {
        LitemallCart cart = new LitemallCart();
        //设置用户
        cart.setUserId(userId);
        //设置商品
        cart.setGoodsId(goods.getId());
        cart.setGoodsSn(goods.getGoodsSn());
        cart.setGoodsName(goods.getName());
        cart.setPicUrl(goods.getPicUrl());
        //设置库存信息
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.createCriteria().andGoodsIdEqualTo(goods.getId());
        LitemallGoodsProduct litemallGoodsProduct = this.goodsProductMapper.selectOneByExample(example);
        if (litemallGoodsProduct != null) {
            cart.setProductId(litemallGoodsProduct.getId());
            cart.setSpecifications(litemallGoodsProduct.getSpecifications());
            cart.setPrice(litemallGoodsProduct.getPrice());
        }
        //设置数量
        cart.setNumber((short) (random.nextInt(this.simulateProperty.getCart().getSkuMaxCountPerCart()) + 1));
        //设置是否选中
        cart.setChecked(random.nextBoolean());

        //设置时间
        cart.setAddTime(dateTime);

        return cart;
    }
}
