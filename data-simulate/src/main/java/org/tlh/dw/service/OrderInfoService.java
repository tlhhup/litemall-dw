package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.linlinjava.litemall.db.dao.*;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.util.CouponConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 订单
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class OrderInfoService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private LitemallOrderMapper orderMapper;

    @Autowired
    private LitemallCartMapper cartMapper;

    @Autowired
    private LitemallOrderGoodsMapper orderGoodsMapper;

    @Autowired
    private LitemallAddressMapper addressMapper;

    @Autowired
    private LitemallGrouponRulesMapper grouponRulesMapper;

    @Autowired
    private LitemallGrouponMapper grouponMapper;

    @Autowired
    private LitemallCouponMapper couponMapper;

    @Autowired
    private LitemallCouponUserMapper couponUserMapper;

    @Autowired
    private LitemallGoodsService goodsService;

    @Transactional
    public void genOrderInfo() {
        int count = RandomUtils.nextInt(12, 40);
        for (int i = 0; i < count; i++) {
            int userId = this.commonDataService.randomUserId();
            this.createOrder(userId);
        }
        log.info("共生成订单{}条", count);
    }

    public void createOrder(int userId) {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        int userRate = this.simulateProperty.getOrder().getUserRate();
        int skuRate = this.simulateProperty.getOrder().getSkuRate();

        boolean joinActivity = this.simulateProperty.getOrder().isJoinActivity();
        int joinActivityRate = this.simulateProperty.getOrder().getJoinActivityRate();
        boolean useCoupon = this.simulateProperty.getOrder().isUseCoupon();
        int useCouponRate = this.simulateProperty.getOrder().getUseCouponRate();

        RandomOptionGroup<Boolean> isOrderUserOptionGroup = new RandomOptionGroup<>(userRate, 100 - userRate);

        RandomOptionGroup<Boolean> isOrderSkuOptionGroup = new RandomOptionGroup<>(skuRate, 100 - skuRate);

        RandomOptionGroup<Boolean> joinActivityRateOptionGroup = new RandomOptionGroup<>(joinActivityRate, 100 - joinActivityRate);

        RandomOptionGroup<Boolean> useCouponRateOptionGroup = new RandomOptionGroup<>(useCouponRate, 100 - useCouponRate);

        //用户是否下单
        if (!isOrderUserOptionGroup.getRandBoolValue()) {
            return;
        }

        //校验地址
        LitemallAddressExample addExample = new LitemallAddressExample();
        addExample.createCriteria().andUserIdEqualTo(userId);
        LitemallAddress litemallAddress = this.addressMapper.selectOneByExample(addExample);
        if (litemallAddress == null) {
            return;
        }

        //1.构建基础信息
        LitemallOrder order = new LitemallOrder();
        order.setUserId(userId);
        order.setOrderSn(RandomNumString.getRandNumString(1, 9, 15, ""));
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setConsignee(RandomName.genName());
        order.setMobile("13" + RandomNumString.getRandNumString(0, 9, 9, ""));
        order.setAddress(litemallAddress.getAddressDetail());
        order.setMessage("描述" + RandomNumString.getRandNumString(1, 9, 6, ""));
        order.setFreightPrice(BigDecimal.valueOf(RandomNum.getRandInt(5, 20)));
        order.setDeleted(false);
        //设置地址信息：用于数仓统计
        order.setProvince(Integer.parseInt(litemallAddress.getProvince()));
        order.setCity(Integer.parseInt(litemallAddress.getCity()));
        order.setCountry(Integer.parseInt(litemallAddress.getCounty()));

        //记录活动优惠
        BigDecimal grouponPrice = new BigDecimal(0);//活动优惠金额
        Map<Integer, LitemallGrouponRules> litemallGrouponRules = new HashMap<>();
        List<LitemallGrouponRules> updateGrouponRules = new ArrayList<>();
        if (joinActivity) {
            //获取活动的商品
            List<LitemallGrouponRules> grouponRules = this.grouponRulesMapper.selectByExample(null);
            if (!ObjectUtils.isEmpty(grouponRules)) {
                for (LitemallGrouponRules item : grouponRules) {
                    litemallGrouponRules.put(item.getGoodsId(), item);
                }
            }
        }

        //2. 商品信息
        //2.1 查询该用户购物车信息
        BigDecimal goodsPrice = new BigDecimal(0);//商品总金额
        List<Integer> cartIdForRemove = new ArrayList<>();
        LitemallCartExample example = new LitemallCartExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<LitemallCart> litemallCarts = this.cartMapper.selectByExample(example);
        //如果购物车为空
        if (ObjectUtils.isEmpty(litemallCarts)) {
            return;
        }
        List<LitemallOrderGoods> orderDetailList = new ArrayList<>();
        for (LitemallCart cartInfo : litemallCarts) {
            if (isOrderSkuOptionGroup.getRandBoolValue()) {
                LitemallOrderGoods orderDetail = new LitemallOrderGoods();
                orderDetail.setGoodsId(cartInfo.getGoodsId());
                orderDetail.setGoodsName(cartInfo.getGoodsName());
                orderDetail.setGoodsSn(cartInfo.getGoodsSn());
                orderDetail.setProductId(cartInfo.getProductId());
                orderDetail.setNumber(cartInfo.getNumber());
                orderDetail.setPrice(cartInfo.getPrice());
                orderDetail.setSpecifications(cartInfo.getSpecifications());
                orderDetail.setPicUrl(cartInfo.getPicUrl());
                orderDetail.setAddTime(localDateTime);
                orderDetail.setDeleted(false);
                orderDetail.setComment(0);// 可以评价

                // 判断该商品是否参加活动
                if (joinActivity && joinActivityRateOptionGroup.getRandBoolValue()) {
                    //2.1.1 计算活动优惠
                    LitemallGrouponRules grouponRule = litemallGrouponRules.get(cartInfo.getGoodsId());
                    if (grouponRule != null) {
                        grouponPrice = grouponPrice.add(grouponRule.getDiscount());
                        //记录需要更新的团购规则
                        updateGrouponRules.add(grouponRule);
                    }
                }

                orderDetailList.add(orderDetail);
                //记录金额
                goodsPrice = goodsPrice.add(cartInfo.getPrice().multiply(new BigDecimal(cartInfo.getNumber())));
                //记录需要从购物车中删除的数据
                cartIdForRemove.add(cartInfo.getId());
            }
        }
        //2.2 处理订单详情空的情况
        if (ObjectUtils.isEmpty(orderDetailList)) {
            return;
        }
        //3.计算价格
        //3.1判断是否使用卷
        BigDecimal couponPrice = new BigDecimal(0);
        List<LitemallCouponUser> updateCouponUsers = new ArrayList<>();
        if (useCoupon) {
            //3.1.1获取该用户没有使用的卷
            LitemallCouponUserExample couponExample = new LitemallCouponUserExample();
            couponExample.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo((short) 0);
            //3.1.2计算卷的优惠
            List<LitemallCouponUser> litemallCouponUsers = this.couponUserMapper.selectByExample(couponExample);
            for (LitemallCouponUser couponUser : litemallCouponUsers) {
                //3.1.3 是否使用该优惠卷
                if (useCouponRateOptionGroup.getRandBoolValue()) {
                    LitemallCoupon litemallCoupon = this.couponMapper.selectByPrimaryKey(couponUser.getCouponId());
                    // 3.1.4 校验该优惠卷是否可用
                    if (litemallCoupon != null && isValid(litemallCoupon, couponUser, goodsPrice, orderDetailList)) {
                        couponPrice = couponPrice.add(litemallCoupon.getDiscount());
                        //记录需要更新的卷信息
                        updateCouponUsers.add(couponUser);
                    }
                }
            }
        }
        //3.2 金额汇总
        // '订单费用， order_price= goods_price + freight_price - coupon_price-groupon_price'
        //实付费用， actual_price= order_price - integral_price
        BigDecimal orderPrice = goodsPrice.add(order.getFreightPrice()).subtract(grouponPrice).subtract(couponPrice);
        BigDecimal integralPrice = new BigDecimal(RandomUtils.nextInt(0, 12));
        BigDecimal actualPrice = orderPrice.subtract(integralPrice);

        order.setGoodsPrice(goodsPrice);
        order.setCouponPrice(couponPrice);
        order.setIntegralPrice(integralPrice);
        order.setGrouponPrice(grouponPrice);
        order.setOrderPrice(orderPrice);
        order.setActualPrice(actualPrice);
        order.setAddTime(localDateTime);
        // 设置待评论数量
        order.setComments((short) orderDetailList.size());

        //4.保存订单
        this.orderMapper.insert(order);
        //5.保存订单详情
        for (LitemallOrderGoods litemallOrderGoods : orderDetailList) {
            litemallOrderGoods.setOrderId(order.getId());
            this.orderGoodsMapper.insert(litemallOrderGoods);
        }

        //6.更新附加数据
        //6.1移除购物车数据,执行软删除，复用deleted作为是否下单标示
        for (Integer cartId : cartIdForRemove) {
            LitemallCart record = new LitemallCart();
            record.setId(cartId);
            //标示为已经下单
            record.setDeleted(true);
            record.setUpdateTime(localDateTime);
            this.cartMapper.updateByPrimaryKeySelective(record);
        }
        //6.2更新参加活动信息
        for (LitemallGrouponRules updateGrouponRule : updateGrouponRules) {
            LitemallGroupon litemallGroupon = new LitemallGroupon();
            litemallGroupon.setOrderId(order.getId());
            litemallGroupon.setRulesId(updateGrouponRule.getId());
            litemallGroupon.setUserId(userId);
            litemallGroupon.setAddTime(localDateTime);
            this.grouponMapper.insert(litemallGroupon);
        }
        //6.3更新优惠卷使用
        for (LitemallCouponUser updateCouponUser : updateCouponUsers) {
            updateCouponUser.setOrderId(order.getId());
            updateCouponUser.setStatus((short) 1);
            updateCouponUser.setUsedTime(localDateTime);
            updateCouponUser.setUpdateTime(localDateTime);
            this.couponUserMapper.updateByPrimaryKey(updateCouponUser);
        }
    }

    /**
     * 校验优惠卷是否可用
     *
     * @param litemallCoupon
     * @param couponUser
     * @param goodsPrice
     * @param orderDetailList
     * @return
     */
    private boolean isValid(LitemallCoupon litemallCoupon, LitemallCouponUser couponUser, BigDecimal goodsPrice, List<LitemallOrderGoods> orderDetailList) {
        //校验使用的优惠卷和规则ID是否相等
        if (!litemallCoupon.getId().equals(couponUser.getCouponId())) {
            return false;
        }

        // 检测优惠卷状态
        Short status = litemallCoupon.getStatus();
        if (!status.equals(CouponConstant.STATUS_NORMAL)) {
            return false;
        }

        // 检查是否超期
        Short timeType = litemallCoupon.getTimeType();
        Short days = litemallCoupon.getDays();
        LocalDateTime now = LocalDateTime.now();
        if (timeType.equals(CouponConstant.TIME_TYPE_TIME)) {
            if (now.isBefore(litemallCoupon.getStartTime()) || now.isAfter(litemallCoupon.getEndTime())) {
                couponUser.setStatus((short) 1);
                return false;
            }
        } else if (timeType.equals(CouponConstant.TIME_TYPE_DAYS)) {
            LocalDateTime expired = couponUser.getAddTime().plusDays(days);
            if (now.isAfter(expired)) {
                couponUser.setStatus((short) 1);
                return false;
            }
        } else {
            return false;
        }

        // 检测商品是否符合
        Map<Integer, List<LitemallOrderGoods>> cartMap = new HashMap<>();
        //可使用优惠券的商品或分类
        List<Integer> goodsValueList = new ArrayList<>(Arrays.asList(litemallCoupon.getGoodsValue()));
        Short goodType = litemallCoupon.getGoodsType();

        if (goodType.equals(CouponConstant.GOODS_TYPE_CATEGORY) ||
                goodType.equals((CouponConstant.GOODS_TYPE_ARRAY))) {
            for (LitemallOrderGoods cart : orderDetailList) {
                // 商品ID或分类ID
                Integer key = goodType.equals(CouponConstant.GOODS_TYPE_ARRAY) ? cart.getGoodsId() : goodsService.findById(cart.getGoodsId()).getCategoryId();
                List<LitemallOrderGoods> carts = cartMap.get(key);
                if (carts == null) {
                    carts = new LinkedList<>();
                }
                carts.add(cart);
                cartMap.put(key, carts);
            }
            //购物车中可以使用优惠券的商品或分类
            goodsValueList.retainAll(cartMap.keySet());
            //可使用优惠券的商品的总价格
            BigDecimal total = new BigDecimal(0);

            for (Integer goodsId : goodsValueList) {
                List<LitemallOrderGoods> carts = cartMap.get(goodsId);
                for (LitemallOrderGoods cart : carts) {
                    total = total.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
                }
            }
            //是否达到优惠券满减金额
            if (total.compareTo(litemallCoupon.getMin()) == -1) {
                return false;
            }
        }

        // 检测是否满足最低消费
        if (goodsPrice.compareTo(litemallCoupon.getMin()) == -1) {
            return false;
        }
        return true;
    }

}
