package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.ParamUtil;
import org.tlh.dw.util.RanOpt;
import org.tlh.dw.util.RandomOptionGroup;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 确认收货/退单
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-21
 */
@Slf4j
@Service
public class OrderConfirmService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private LitemallOrderMapper orderMapper;

    @Transactional
    public void genConfirm() {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int rate = this.simulateProperty.getConfirm().getRate();
        RandomOptionGroup<Boolean> ifConfirm = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, rate), new RanOpt(false, 100 - rate)});


        //1.查询所有发货/申请退款的订单
        LitemallOrderExample example = new LitemallOrderExample();
        example.createCriteria().andOrderStatusIn(Arrays.asList(OrderUtil.STATUS_REFUND, OrderUtil.STATUS_SHIP));
        List<LitemallOrder> litemallOrders = this.orderMapper.selectByExample(example);
        if (ObjectUtils.isEmpty(litemallOrders)) {
            log.info("没有需要完结的订单 ");
            return;
        }
        int confirmCount = 0;
        for (LitemallOrder order : litemallOrders) {
            //2.随机完成
            if (ifConfirm.getRandBoolValue()) {
                //3.完成订单
                if (OrderUtil.isRefundStatus(order)) {//如果是退单
                    order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
                    order.setRefundAmount(order.getActualPrice());
                } else {
                    order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
                }
                order.setConfirmTime(localDateTime);
                order.setEndTime(localDateTime);
                order.setUpdateTime(localDateTime);
                order.setDeleted(false);
                this.orderMapper.updateByPrimaryKey(order);
                confirmCount++;
            }
        }
        log.info("共生成确认收货/退单{}条", confirmCount);
    }

}
