package org.tlh.dw.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.dw.entity.AdsDateTopic;
import org.tlh.dw.mapper.AdsDateTopicMapper;
import org.tlh.dw.service.DashBoardService;
import org.tlh.dw.vo.DashBoardHeader;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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

    @Override
    public DashBoardHeader queryByDate(int type) {
        DashBoardHeader result = new DashBoardHeader();
        //1.校验数据
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        Object value = "";
        switch (type) {
            case 1://周
                value = calendar.get(Calendar.WEEK_OF_YEAR);
                break;
            case 2://月
                value = calendar.get(Calendar.MONTH)+1;
                break;
            default:
                type = 0;
                //前一天
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                value = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
                break;
        }
        //2.查询汇总数据
        AdsDateTopic adsDateTopic = this.adsDateTopicMapper.findSummaryByType(year, type, value);
        if (adsDateTopic != null) {
            result.setOrderCount(adsDateTopic.getOrderCount());
            result.setOrderAmount(adsDateTopic.getOrderTotalAmount());
            result.setPaymentAmount(adsDateTopic.getPaymentTotalAmount());
            result.setRefundAmount(adsDateTopic.getRefundTotalAmount());

            result.setUvCount(adsDateTopic.getUvCount());
            result.setRegisterCount(adsDateTopic.getRegisterCount());

            //3.计算客单价
            if (adsDateTopic.getPayoffUserCount() != 0) {
                result.setPrePrice(adsDateTopic.getPayoff().divide(
                        new BigDecimal(adsDateTopic.getPayoffUserCount()),
                        2,
                        BigDecimal.ROUND_HALF_UP));
            }
            //4.计算支付转化率
            if (adsDateTopic.getPaymentUserCount() != 0) {
                result.setPayConvertRate(((double) adsDateTopic.getPaymentUserCount()) / adsDateTopic.getUvCount());
            }
        }
        return result;
    }

    @Override
    public List<Map> queryByDuration(int duration) {
        List<Map> result = this.adsDateTopicMapper.findByDuration(duration);
        return result;
    }
}
