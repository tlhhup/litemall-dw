package org.tlh.dw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.dw.entity.AdsDateTopic;
import org.tlh.dw.entity.AdsPaymentDaycount;
import org.tlh.dw.mapper.AdsDateTopicMapper;
import org.tlh.dw.mapper.AdsPaymentDaycountMapper;
import org.tlh.dw.service.DashBoardService;
import org.tlh.dw.vo.DashBoardHeader;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class DashBoardServiceImpl implements DashBoardService {

    @Autowired
    private AdsDateTopicMapper adsDateTopicMapper;

    @Autowired
    private AdsPaymentDaycountMapper adsPaymentDaycountMapper;

    @Override
    public DashBoardHeader queryByDate(String date, int type) {
        DashBoardHeader result = new DashBoardHeader();
        //1.校验数据
        if (StringUtils.isEmpty(date)) {
            //默认查询T+1的数据
            Date yesterday = DateUtils.addDays(new Date(), -1);
            date = DateFormatUtils.format(yesterday, "yyyy-MM-dd");
        }
        //2.查询汇总数据
        QueryWrapper wrapper = new QueryWrapper<AdsDateTopic>().eq("date", date);
        AdsDateTopic adsDateTopic = this.adsDateTopicMapper.selectOne(wrapper);
        if (adsDateTopic != null) {
            result.setOrderCount(adsDateTopic.getOrderCount());
            result.setOrderAmount(adsDateTopic.getOrderTotalAmount());
            result.setPaymentAmount(adsDateTopic.getPaymentTotalAmount());
            result.setRefundAmount(adsDateTopic.getRefundTotalAmount());

            result.setUvCount(adsDateTopic.getUvCount());
            result.setRegisterCount(adsDateTopic.getUvCount());
        }
        //3. todo 计算客单价
        //4.计算支付转化率
        wrapper = new QueryWrapper<AdsPaymentDaycount>().eq("dt", date);
        AdsPaymentDaycount adsPaymentDaycount = this.adsPaymentDaycountMapper.selectOne(wrapper);
        if (adsPaymentDaycount != null
                && adsPaymentDaycount.getPaymentUserCount() != 0
                && adsPaymentDaycount.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0) {
            result.setPayConvertRate(adsPaymentDaycount.getPaymentAmount().divide(
                    new BigDecimal(adsPaymentDaycount.getPaymentUserCount()),
                    2,
                    BigDecimal.ROUND_HALF_UP));
        }
        return result;
    }
}
