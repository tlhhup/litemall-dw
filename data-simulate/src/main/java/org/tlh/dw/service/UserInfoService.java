package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.CouponAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class UserInfoService {

    @Autowired
    private LitemallUserMapper userMapper;

    @Autowired
    private CouponAssignService couponAssignService;

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private CommonDataService commonDataService;

    public void genUserInfo() {
        Date date = ParamUtil.checkDate(this.simulateProperty.getDate());
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // 更新用户
        updateUsers(localDateTime);

        for (int i = 0; i < simulateProperty.getUser().getCount(); i++) {
            LitemallUser user = initUserInfo(localDateTime);
            Date birthday = DateUtils.addMonths(date, -1 * RandomNum.getRandInt(15, 55) * 12);
            user.setBirthday(birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            this.userMapper.insertSelective(user);

            // 给新用户发送注册优惠券
            couponAssignService.assignForRegister(user.getId());
            // 记录新增用户的ID
            this.commonDataService.updateUserId(user.getId());
        }
        log.info("共生成{}名用户", simulateProperty.getUser().getCount());
    }

    private LitemallUser initUserInfo(LocalDateTime localDateTime) {
        Integer maleRateWeight = ParamUtil.checkRatioNum(this.simulateProperty.getUser().getMaleRate());

        String email = RandomEmail.getEmail(6, 12);
        String username = email.split("@")[0];

        LitemallUser user = new LitemallUser();
        user.setUsername(username);
        user.setPassword("asdfaw3esdfasdf");
        user.setMobile("13" + RandomNumString.getRandNumString(1, 9, 9, ""));
        user.setWeixinOpenid(UUID.randomUUID().toString());
        user.setAvatar("https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64");
        user.setGender((byte) (new RandomOptionGroup(new RanOpt[]{new RanOpt(0, maleRateWeight.intValue()), new RanOpt(1, 100 - maleRateWeight.intValue())})).getRandIntValue());
        String lastName = RandomName.insideLastName(user.getGender());
        user.setNickname(RandomName.getNickName(user.getGender(), lastName));
        user.setUserLevel((byte) (new RandomOptionGroup(new RanOpt[]{new RanOpt(1, 7), new RanOpt(2, 2), new RanOpt(3, 1)})).getRandIntValue());
        user.setStatus((byte) 0);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(RandomIp.getIp());

        user.setAddTime(LocalDateTime.from(localDateTime));
        user.setUpdateTime(localDateTime);

        return user;
    }

    private void updateUsers(LocalDateTime date) {
        Integer updateRateWeight = ParamUtil.checkRatioNum(this.simulateProperty.getUser().getUpdateRate());
        if (updateRateWeight.intValue() == 0) {
            return;
        }

        Set<Integer> userInfoList = this.commonDataService.randomUserId(updateRateWeight);
        for (int id : userInfoList) {
            LitemallUser userInfo = this.userMapper.selectByPrimaryKey(id);
            if (userInfo == null) {
                continue;
            }
            int randInt = RandomNum.getRandInt(2, 7);
            if (randInt % 2 == 0) {
                String lastName = RandomName.insideLastName(userInfo.getGender());
                userInfo.setNickname(RandomName.getNickName(userInfo.getGender(), lastName));
            }
            if (randInt % 3 == 0) {
                userInfo.setUserLevel((byte) (new RandomOptionGroup(new RanOpt[]{new RanOpt(1, 7), new RanOpt(2, 2), new RanOpt(3, 1)})).getRandIntValue());
            }
            if (randInt % 7 == 0) {
                userInfo.setMobile("13" + RandomNumString.getRandNumString(1, 9, 9, ""));
            }
            //设置更新时间
            userInfo.setUpdateTime(date);
            //更新用户
            this.userMapper.updateByPrimaryKey(userInfo);
        }
        log.info("共有{}名用户发生变更", Integer.valueOf(userInfoList.size()));

    }

}
