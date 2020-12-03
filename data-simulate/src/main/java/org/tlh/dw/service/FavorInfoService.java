package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallCollectMapper;
import org.linlinjava.litemall.db.domain.LitemallCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.ParamUtil;
import org.tlh.dw.util.RanOpt;
import org.tlh.dw.util.RandomOptionGroup;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

/**
 * 收藏
 * 周期性快照表
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class FavorInfoService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private LitemallCollectMapper collectMapper;

    public void genFavors() {
        Date date = this.simulateProperty.isUseDate() ? ParamUtil.checkDate(this.simulateProperty.getDate()) : new Date();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Random random = new Random();
        int count = this.simulateProperty.getFavor().getCount();
        for (int i = 0; i < count; i++) {
            int type = random.nextInt(2);
            int userId = this.commonDataService.randomUserId();
            int valueId = type == 0 ? this.commonDataService.randomSkuId() : this.commonDataService.randomTopicId();
            LitemallCollect litemallCollect = init(userId, valueId, type, localDateTime);
            this.collectMapper.insert(litemallCollect);
        }

        log.info("共生成收藏{}条", count);
    }

    private LitemallCollect init(int userId, int valueId, int type, LocalDateTime date) {
        LitemallCollect collect = new LitemallCollect();
        collect.setUserId(userId);
        collect.setValueId(valueId);
        collect.setType((byte) type);
        collect.setAddTime(date);
        //是否取消
        int cancelRate = this.simulateProperty.getFavor().getCancelRate();
        RandomOptionGroup<Boolean> isCancelOptionGroup = new RandomOptionGroup<>(new RanOpt[]{new RanOpt(true, cancelRate), new RanOpt(false, 100 - cancelRate)});
        Boolean isCancel = isCancelOptionGroup.getRandBoolValue();
        collect.setDeleted(isCancel);
        if (isCancel) {
            collect.setUpdateTime(date);
        }

        return collect;
    }

}
