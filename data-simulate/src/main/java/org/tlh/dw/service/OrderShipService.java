package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.ParamUtil;
import org.tlh.dw.util.RanOpt;
import org.tlh.dw.util.RandomOptionGroup;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 发货
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-21
 */
@Slf4j
@Service
public class OrderShipService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private LitemallOrderMapper orderMapper;

    public void genShip() {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int rate = this.simulateProperty.getShip().getRate();
        RandomOptionGroup<Boolean> ifShip = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, rate), new RanOpt(false, 100 - rate)});
        List<String> shipChannel = this.simulateProperty.getShip().getShipChannel();

        //1.查询支付的订单
        LitemallOrderExample example = new LitemallOrderExample();
        example.createCriteria().andOrderStatusEqualTo(OrderUtil.STATUS_PAY);
        List<LitemallOrder> litemallOrders = this.orderMapper.selectByExample(example);
        if (ObjectUtils.isEmpty(litemallOrders)) {
            log.info("没有需要发货的订单 ");
            return;
        }
        //2.发货
        Random random = new Random();
        int shipCount = 0;
        for (LitemallOrder order : litemallOrders) {
            if (ifShip.getRandBoolValue()) {
                //发货信息
                order.setShipSn(UUID.randomUUID().toString());
                order.setShipChannel(shipChannel.get(random.nextInt(shipChannel.size())));
                order.setShipTime(localDateTime);
                //状态
                order.setOrderStatus(OrderUtil.STATUS_SHIP);
                order.setUpdateTime(localDateTime);
                order.setDeleted(false);

                this.orderMapper.updateByPrimaryKey(order);
                shipCount++;
            }
        }

        log.info("共生成发货{}条", shipCount);
    }

}
