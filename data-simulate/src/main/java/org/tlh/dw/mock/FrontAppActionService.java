package org.tlh.dw.mock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.linlinjava.litemall.db.dao.*;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.dto.*;
import org.tlh.dw.mock.interceptor.TokenHolder;
import org.tlh.dw.rest.FrontAppAction;
import org.tlh.dw.service.CommonDataService;
import org.tlh.dw.service.UserInfoService;
import org.tlh.dw.util.RanOpt;
import org.tlh.dw.util.RandomEmail;
import org.tlh.dw.util.RandomNumString;
import org.tlh.dw.util.RandomOptionGroup;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-14
 */
@Slf4j
@Service
public class FrontAppActionService {

    @Autowired
    private FrontAppAction frontAppAction;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private LitemallUserMapper userMapper;

    @Autowired
    private LitemallOrderMapper orderMapper;

    @Autowired
    private LitemallOrderGoodsMapper orderGoodsMapper;

    @Autowired
    private LitemallAddressMapper addressMapper;

    @Autowired
    private LitemallCartMapper cartMapper;

    @Autowired
    private LitemallCouponUserMapper couponUserMapper;

    @Autowired
    private LitemallGrouponRulesMapper grouponRulesMapper;

    @Autowired
    private SimulateProperty simulateProperty;

    @Value("${simulate.front-app.core-pool-size:20}")
    private int corePoolSize;

    @Value("${simulate.front-app.maximum-pool-size:100}")
    private int maximumPoolSize;

    @Value("${simulate.front-app.keep-alive-time:5000}")
    private long keepAliveTime;

    @Value("${simulate.front-app.requests:50}")
    private int requests;

    private Map<Integer, String> tokens;
    private DelayQueue<UserTokenCache> tokenCaches;
    private ThreadPoolExecutor threadPool;

    @PostConstruct
    public void init() {
        this.tokens = new ConcurrentHashMap<>();
        this.tokenCaches = new DelayQueue<>();
        //线程池
        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 用户注册
     */
    private void register() {
        //1. 是否需要注册新用户
        if (RandomUtils.nextBoolean()) {
            String email = RandomEmail.getEmail(6, 12);
            String username = email.split("@")[0];
            UserRegister user = new UserRegister();
            user.setUsername(username);
            user.setMobile("13" + RandomNumString.getRandNumString(1, 9, 9, ""));
            user.setPassword(UserInfoService.PASSWORD);
            //2. 注册
            Response<Map<String, Object>> response = this.frontAppAction.register(user);
            if (response.isSuccessful()) {
                Map<String, Object> body = response.body();
                if (body != null && body.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    if (data != null && data.containsKey("token")) {
                        String token = data.get("token").toString();
                        int userId = Integer.parseInt(((Map<String, Object>) data.get("userInfo")).get("userId").toString());
                        //3. 存储
                        this.tokens.put(userId, token);
                        this.tokenCaches.add(new UserTokenCache(userId, UserTokenCache.TOKEN_EXPIRE_SECONDS));
                        log.info("user [{}] register success!",userId);
                    }
                }
            }
        }
    }

    /**
     * 用户登录
     */
    private void login(int userId) {
        //1. 用户登录
        //2. 判断token是否存在且没有过期
        boolean needReLogin = false;
        //2.1 判断token是否存在
        if (this.tokens.containsKey(userId)) {
            //2.2 判断token是否过期
            UserTokenCache peek = this.tokenCaches.peek();
            if (peek != null && userId == peek.getUserId()) {
                needReLogin = true;
            }
        } else {
            needReLogin = true;
        }
        //3.判断是否需要重新获取token
        if (needReLogin) {
            LitemallUser litemallUser = this.userMapper.selectByPrimaryKey(userId);
            if (litemallUser != null) {
                //4. 解析得到 userId和token
                UserBase userBase = new UserBase();
                userBase.setUsername(litemallUser.getUsername());
                userBase.setPassword(userId == 1 ? "user123" : UserInfoService.PASSWORD);
                Response<Map<String, Object>> response = this.frontAppAction.login(userBase);
                if (response.isSuccessful()) {
                    Map<String, Object> body = response.body();
                    if (body != null && body.containsKey("data")) {
                        Map<String, Object> data = (Map<String, Object>) body.get("data");
                        if (data != null && data.containsKey("token")) {
                            String token = data.get("token").toString();
                            //5. 存储
                            this.tokens.put(userId, token);
                            this.tokenCaches.add(new UserTokenCache(userId, UserTokenCache.TOKEN_EXPIRE_SECONDS));
                        }
                    }
                }
            }
        }
    }

    /**
     * 加购
     *
     * @param userId
     */
    private void addCart(int userId) {
        //1.是否要加购
        if (RandomUtils.nextBoolean()) {
            //2.添加token
            if (addRequestToken(userId)) {
                LitemallGoodsProduct product = this.commonDataService.randomSku();
                //构建购物车
                LitemallCart cart = new LitemallCart();
                cart.setGoodsId(product.getGoodsId());
                cart.setProductId(product.getId());
                int number = RandomUtils.nextInt(0, this.simulateProperty.getCart().getSkuMaxCountPerCart()) + 1;
                cart.setNumber((short) number);
                Response<Map<String, Object>> response = this.frontAppAction.addCart(cart);
                if (response.isSuccessful()) {
                    Map<String, Object> body = response.body();
                    if (body != null && body.get("errno").equals(0)) {
                        log.info("add cart success userId:[{}]", userId);
                    }
                }
            }
        }
    }

    /**
     * 下单
     *
     * @param userId
     */
    private void order(int userId) {
        //1.是否下单
        int userRate = this.simulateProperty.getOrder().getUserRate();

        boolean joinActivity = this.simulateProperty.getOrder().isJoinActivity();
        int joinActivityRate = this.simulateProperty.getOrder().getJoinActivityRate();
        boolean useCoupon = this.simulateProperty.getOrder().isUseCoupon();
        int useCouponRate = this.simulateProperty.getOrder().getUseCouponRate();

        RandomOptionGroup<Boolean> isOrderUserOptionGroup = new RandomOptionGroup<>(userRate, 100 - userRate);
        RandomOptionGroup<Boolean> joinActivityRateOptionGroup = new RandomOptionGroup<>(joinActivityRate, 100 - joinActivityRate);
        RandomOptionGroup<Boolean> useCouponRateOptionGroup = new RandomOptionGroup<>(useCouponRate, 100 - useCouponRate);

        if (isOrderUserOptionGroup.getRandBoolValue()) {
            //2.添加token
            if (addRequestToken(userId)) {
                //3. 校验地址
                LitemallAddressExample addExample = new LitemallAddressExample();
                addExample.createCriteria().andUserIdEqualTo(userId);
                LitemallAddress litemallAddress = this.addressMapper.selectOneByExample(addExample);
                if (litemallAddress == null) {
                    return;
                }
                //4. 购物车id
                LitemallCartExample cartExample = new LitemallCartExample();
                cartExample.createCriteria().andUserIdEqualTo(userId).andDeletedEqualTo(false);
                List<LitemallCart> litemallCarts = this.cartMapper.selectByExample(cartExample);
                if (!ObjectUtils.isEmpty(litemallCarts)) {
                    OrderSubmit orderSubmit = new OrderSubmit();
                    orderSubmit.setAddressId(litemallAddress.getId());

                    int cartId = litemallCarts.get(RandomUtils.nextInt(0, litemallCarts.size())).getId();
                    orderSubmit.setCartId(cartId);

                    //5. 获取优惠卷
                    if (useCoupon) {
                        LitemallCouponUserExample couponExample = new LitemallCouponUserExample();
                        couponExample.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo((short) 0);
                        List<LitemallCouponUser> litemallCouponUsers = this.couponUserMapper.selectByExample(couponExample);
                        if (!ObjectUtils.isEmpty(litemallCouponUsers)) {
                            for (LitemallCouponUser litemallCouponUser : litemallCouponUsers) {
                                if (useCouponRateOptionGroup.getRandBoolValue()) {
                                    orderSubmit.setCouponId(litemallCouponUser.getCouponId());
                                    orderSubmit.setUserCouponId(litemallCouponUser.getId());
                                    break;
                                }
                            }
                        }
                    }

                    //6. 活动
                    if (joinActivity) {
                        List<LitemallGrouponRules> grouponRules = this.grouponRulesMapper.selectByExample(null);
                        if (!ObjectUtils.isEmpty(grouponRules)) {
                            for (LitemallGrouponRules grouponRule : grouponRules) {
                                if (joinActivityRateOptionGroup.getRandBoolValue()) {
                                    orderSubmit.setGrouponRulesId(grouponRule.getId());
                                    break;
                                }
                            }
                        }
                    }

                    //7. 下单
                    Response<Map<String, Object>> response = this.frontAppAction.order(orderSubmit);
                    if (response.isSuccessful()) {
                        Map<String, Object> body = response.body();
                        if (body != null && body.get("errno").equals(0)) {
                            log.info("order success userId:[{}]", userId);
                        }
                    }
                }
            }
        }
    }

    /**
     * 支付
     *
     * @param userId
     */
    private void payment(int userId) {
        //1.是否支付
        int rate = this.simulateProperty.getPayment().getRate();
        RandomOptionGroup<Boolean> ifPay = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, rate), new RanOpt(false, 100 - rate)});
        if (ifPay.getRandBoolValue()) {
            //2.添加token
            if (addRequestToken(userId)) {
                //3. 获取用户的订单
                LitemallOrderExample example = new LitemallOrderExample();
                // 未支付的订单
                example.createCriteria()
                        .andUserIdEqualTo(userId)
                        .andOrderStatusEqualTo(OrderUtil.STATUS_CREATE);
                List<LitemallOrder> litemallOrders = this.orderMapper.selectByExample(example);
                if (!ObjectUtils.isEmpty(litemallOrders)) {
                    int orderId = litemallOrders.get(RandomUtils.nextInt(0, litemallOrders.size())).getId();
                    //4. 确认支付
                    OrderConfirm confirm = new OrderConfirm();
                    confirm.setOrderId(orderId);
                    Response<Map<String, Object>> response = this.frontAppAction.payment(confirm);
                    if (response.isSuccessful()) {
                        Map<String, Object> body = response.body();
                        if (body != null && body.get("errno").equals(0)) {
                            log.info("payment success userId:[{}]", userId);
                        }
                    }
                }
            }
        }
    }

    /**
     * 确认收货
     *
     * @param userId
     */
    private void confirm(int userId) {
        //1.是否确认收货
        int rate = this.simulateProperty.getConfirm().getRate();
        RandomOptionGroup<Boolean> ifConfirm = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, rate), new RanOpt(false, 100 - rate)});
        if (ifConfirm.getRandBoolValue()) {
            //2.添加token
            if (addRequestToken(userId)) {
                //3. 获取用户的订单
                LitemallOrderExample example = new LitemallOrderExample();
                // 发货的订单
                example.createCriteria()
                        .andUserIdEqualTo(userId)
                        .andOrderStatusEqualTo(OrderUtil.STATUS_SHIP);
                List<LitemallOrder> litemallOrders = this.orderMapper.selectByExample(example);
                if (!ObjectUtils.isEmpty(litemallOrders)) {
                    int orderId = litemallOrders.get(RandomUtils.nextInt(0, litemallOrders.size())).getId();
                    //4. 确认收货
                    OrderConfirm confirm = new OrderConfirm();
                    confirm.setOrderId(orderId);
                    Response<Map<String, Object>> response = this.frontAppAction.confirm(confirm);
                    if (response.isSuccessful()) {
                        Map<String, Object> body = response.body();
                        if (body != null && body.get("errno").equals(0)) {
                            log.info("confirm success userId:[{}]", userId);
                        }
                    }
                }
            }
        }
    }

    /**
     * 退货
     *
     * @param userId
     */
    private void refund(int userId) {
        //1.是否退款
        int rate = this.simulateProperty.getRefund().getRate();
        RandomOptionGroup<Boolean> ifRefund = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, rate), new RanOpt(false, 100 - rate)});
        if (ifRefund.getRandBoolValue()) {
            //2.添加token
            if (addRequestToken(userId)) {
                //3. 获取用户的订单
                LitemallOrderExample example = new LitemallOrderExample();
                // 支付或发货的订单
                example.createCriteria()
                        .andUserIdEqualTo(userId)
                        .andOrderStatusIn(Arrays.asList(OrderUtil.STATUS_PAY, OrderUtil.STATUS_SHIP));
                List<LitemallOrder> litemallOrders = this.orderMapper.selectByExample(example);
                if (!ObjectUtils.isEmpty(litemallOrders)) {
                    int orderId = litemallOrders.get(RandomUtils.nextInt(0, litemallOrders.size())).getId();
                    //4. 申请退款
                    OrderConfirm refund = new OrderConfirm();
                    refund.setOrderId(orderId);
                    Response<Map<String, Object>> response = this.frontAppAction.refund(refund);
                    if (response.isSuccessful()) {
                        Map<String, Object> body = response.body();
                        if (body != null && body.get("errno").equals(0)) {
                            log.info("refund success userId:[{}]", userId);
                        }
                    }
                }
            }
        }
    }

    /**
     * 订单评论
     *
     * @param userId
     */
    private void comment(int userId) {
        //1.是否评论
        int rate = this.simulateProperty.getComment().getRate();
        RandomOptionGroup<Boolean> ifComment = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, rate), new RanOpt(false, 100 - rate)});
        if (ifComment.getRandBoolValue()) {
            //2.添加token
            if (addRequestToken(userId)) {
                //3.获取订单
                LitemallOrderExample example = new LitemallOrderExample();
                // 确认收货订单
                example.createCriteria()
                        .andUserIdEqualTo(userId)
                        .andOrderStatusIn(Arrays.asList(OrderUtil.STATUS_CONFIRM, OrderUtil.STATUS_AUTO_CONFIRM))
                        .andCommentsGreaterThan((short) 0);
                List<LitemallOrder> litemallOrders = this.orderMapper.selectByExample(example);
                if (!ObjectUtils.isEmpty(litemallOrders)) {
                    int orderId = litemallOrders.get(RandomUtils.nextInt(0, litemallOrders.size())).getId();
                    //4. 获取订单详情
                    LitemallOrderGoodsExample condition = new LitemallOrderGoodsExample();
                    condition.createCriteria().andOrderIdEqualTo(orderId);
                    List<LitemallOrderGoods> litemallOrderGoods = this.orderGoodsMapper.selectByExample(condition);
                    if (!ObjectUtils.isEmpty(litemallOrderGoods)) {
                        //5. 评论
                        int orderGoodsId = litemallOrderGoods.get(RandomUtils.nextInt(0, litemallOrderGoods.size())).getId();
                        OrderComment comment = new OrderComment();
                        comment.setOrderGoodsId(orderGoodsId);
                        comment.setStar(RandomUtils.nextInt(1, 6));
                        comment.setContent("评论内容" + RandomNumString.getRandNumString(1, 9, 50, ""));

                        Response<Map<String, Object>> response = this.frontAppAction.comment(comment);
                        if (response.isSuccessful()) {
                            Map<String, Object> body = response.body();
                            if (body != null && body.get("errno").equals(0)) {
                                log.info("comment success userId:[{}]", userId);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 收藏
     *
     * @param userId
     */
    private void collect(int userId) {
        //1.获取用户ID
        //2.添加token
        if (addRequestToken(userId)) {
            int type = RandomUtils.nextInt(0, 2);
            int valueId = type == 0 ? this.commonDataService.randomGoodId() : this.commonDataService.randomTopicId();
            CollectDto collectDto = new CollectDto();
            collectDto.setType(type);
            collectDto.setValueId(valueId);
            Response<Map<String, Object>> response = this.frontAppAction.collect(collectDto);
            if (response.isSuccessful()) {
                Map<String, Object> body = response.body();
                if (body != null && body.get("errno").equals(0)) {
                    log.info("collect success userId:[{}]", userId);
                }
            }
        }
    }

    //添加请求头
    private boolean addRequestToken(int userId) {
        String token = this.tokens.get(userId);
        if (StringUtils.hasText(token)) {
            TokenHolder.setToken(token);
            return true;
        }
        return false;
    }

    public void process() {
        log.info("Frond Action Simulate start");
        for (int i = 0; i < this.requests; i++) {
            this.threadPool.submit(() -> {
                int userId = commonDataService.randomUserId();
                log.info("current userId:[{}]", userId);
                this.register();
                this.login(userId);
                this.addCart(userId);
                this.order(userId);
                this.payment(userId);
                this.confirm(userId);
                this.refund(userId);
                this.comment(userId);
                this.collect(userId);
            });
        }
        log.info("Frond Action Simulate end");
    }

}
