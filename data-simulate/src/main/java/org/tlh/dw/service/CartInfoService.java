package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallCartMapper;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallCart;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoodsExample;
import org.linlinjava.litemall.db.domain.LitemallGoodsProduct;
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
    private LitemallGoodsMapper goodsMapper;

    private Random random = new Random();

    public void genCartInfo() {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int count = this.simulateProperty.getCart().getCount();
        int realCount = 0;
        for (int i = 0; i < count; i++) {
            int userId = this.commonDataService.randomUserId();
            boolean b = addCart(userId, this.commonDataService.randomSku(), localDateTime);
            if (b) {
                realCount++;
            }
        }

        log.info("共生成购物车{}条", realCount);
    }

    private boolean addCart(int userId, LitemallGoodsProduct sku, LocalDateTime dateTime) {
        //1.校验商品是否可以购买
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(sku.getGoodsId()).andDeletedEqualTo(false);
        LitemallGoods litemallGoods = goodsMapper.selectOneByExample(example);
        if (litemallGoods == null || !litemallGoods.getIsOnSale()) {
            return false;
        }
        //2.加入购物车
        LitemallCart cart = new LitemallCart();
        //设置用户
        cart.setUserId(userId);
        //设置sku信息
        cart.setProductId(sku.getId());
        cart.setPrice(sku.getPrice());
        cart.setSpecifications(sku.getSpecifications());
        //设置spu信息
        cart.setGoodsName(litemallGoods.getName());
        cart.setPicUrl(litemallGoods.getPicUrl());
        cart.setGoodsId(litemallGoods.getId());
        cart.setGoodsSn(litemallGoods.getGoodsSn());

        //设置数量
        cart.setNumber((short) (random.nextInt(this.simulateProperty.getCart().getSkuMaxCountPerCart()) + 1));
        //设置是否选中
        cart.setChecked(random.nextBoolean());

        //设置时间
        cart.setAddTime(dateTime);

        this.cartMapper.insert(cart);
        return true;
    }
}
