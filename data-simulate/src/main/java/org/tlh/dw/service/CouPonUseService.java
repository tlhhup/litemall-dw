package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallCouponUserMapper;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.ParamUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 领劵
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-22
 */
@Slf4j
@Service
public class CouPonUseService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private LitemallCouponUserMapper couponUserMapper;

    public void genCouPonUse() {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int userCount = this.simulateProperty.getCoupon().getUserCount();
        for (int i = 0; i < userCount; i++) {
            int userId = commonDataService.randomUserId();
            Integer couPonId = commonDataService.randomCouPonId();
            LitemallCouponUser couponUser = new LitemallCouponUser();
            // todo 记录优惠卷的规则信息
            couponUser.setUserId(userId);
            couponUser.setCouponId(couPonId);
            couponUser.setStatus((short) 0);
            couponUser.setAddTime(localDateTime);
            couponUser.setDeleted(false);
            this.couponUserMapper.insert(couponUser);
        }
        log.info("共生成{}个用户领劵", userCount);
    }

}
