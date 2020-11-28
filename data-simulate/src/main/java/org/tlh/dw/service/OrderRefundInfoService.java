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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 申请退款
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class OrderRefundInfoService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private LitemallOrderMapper orderMapper;

    public void genRefundsOrFinish() {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int rate = this.simulateProperty.getRefund().getRate();
        List<Integer> reasonRate = this.simulateProperty.getRefund().getReasonRate();
        RandomOptionGroup<Boolean> ifRefund = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, rate), new RanOpt(false, 100 - rate)});

        RandomOptionGroup<String> refundReasonOptionGroup = new RandomOptionGroup<>(new RanOpt[]{
                new RanOpt("1301", reasonRate.get(0)),
                new RanOpt("1304", reasonRate.get(1)),
                new RanOpt("1303", reasonRate.get(2)),
                new RanOpt("1305", reasonRate.get(3)),
                new RanOpt("1302", reasonRate.get(4)),
                new RanOpt("1306", reasonRate.get(5)),
                new RanOpt("1307", reasonRate.get(6))});

        //1.查询所有支付/发货了的订单
        LitemallOrderExample example = new LitemallOrderExample();
        example.createCriteria().andOrderStatusIn(Arrays.asList(OrderUtil.STATUS_PAY, OrderUtil.STATUS_SHIP));
        List<LitemallOrder> litemallOrders = this.orderMapper.selectByExample(example);
        if (ObjectUtils.isEmpty(litemallOrders)) {
            log.info("没有需要退款的订单 ");
            return;
        }
        int refundCount = 0;
        for (LitemallOrder order : litemallOrders) {
            //2.随机退单
            if (ifRefund.getRandBoolValue()) {
                order.setOrderStatus(OrderUtil.STATUS_REFUND);
                //申请退款金额和申请时间
                order.setRefundTime(localDateTime);
                order.setRefundAmount(order.getActualPrice());
                order.setRefundType(refundReasonOptionGroup.getRandStringValue());
                order.setRefundContent(refundReasonOptionGroup.getRandStringValue());
                order.setDeleted(false);
                order.setUpdateTime(localDateTime);
                this.orderMapper.updateByPrimaryKey(order);
                refundCount++;
            }
        }
        log.info("共生成退款{}条", refundCount);
    }

}
