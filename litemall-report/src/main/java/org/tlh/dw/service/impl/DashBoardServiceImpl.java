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
        //1.校验数据
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int currentYear = 0;
        int lastYear = 0;
        Object currentValue = "";
        Object lastValue = "";
        switch (type) {
            case 1://周
                currentYear = calendar.get(Calendar.YEAR);
                currentValue = calendar.get(Calendar.WEEK_OF_YEAR);

                //对比数据
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                lastYear = calendar.get(Calendar.YEAR);
                lastValue = calendar.get(Calendar.WEEK_OF_YEAR);
                break;
            case 2://月
                currentYear = calendar.get(Calendar.YEAR);
                currentValue = calendar.get(Calendar.MONTH) + 1;

                //对比数据
                calendar.add(Calendar.MONTH, -1);
                lastYear = calendar.get(Calendar.YEAR);
                lastValue = calendar.get(Calendar.MONTH) + 1;
                break;
            default:
                type = 0;
                //前一天
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                currentYear = calendar.get(Calendar.YEAR);
                currentValue = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");

                //前两天
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                lastYear = calendar.get(Calendar.YEAR);
                lastValue = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
                break;
        }
        //2.查询汇总数据
        //2.1当前数据
        DashBoardHeader current = queryDateTopic(type, currentYear, currentValue);
        if (current != null) {
            //2.2对比数据
            DashBoardHeader last = this.queryDateTopic(type, lastYear, lastValue);
            //2.3计算增量
            if (last != null) {
                if (last.getUvCount() != 0) {
                    current.setUvRate((current.getUvCount() - last.getUvCount()) / (double) last.getUvCount());
                    BigDecimal temp = new BigDecimal(current.getUvRate());
                    current.setUvRate(temp.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
                }
                if (last.getOrderCount() != 0) {
                    current.setOrderRate((current.getOrderCount() - last.getOrderCount()) / (double) last.getOrderCount());
                    BigDecimal temp = new BigDecimal(current.getOrderRate());
                    current.setOrderRate(temp.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
                }
                if (!last.getPaymentAmount().equals(BigDecimal.ZERO)) {
                    current.setPaymentRate(current.getPaymentAmount().subtract(last.getPaymentAmount()).divide(
                            last.getPaymentAmount(),
                            4,
                            BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
                }
                if (!last.getRefundAmount().equals(BigDecimal.ZERO)) {
                    current.setRefundRate(current.getRefundAmount().subtract(last.getRefundAmount()).divide(
                            last.getRefundAmount(),
                            4,
                            BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
                }
                if (last.getPayConvertRate() != 0) {
                    current.setPayConvertRateRate((current.getPayConvertRate() - last.getPayConvertRate()) / last.getPayConvertRate());
                    BigDecimal temp = new BigDecimal(current.getPayConvertRateRate());
                    current.setPayConvertRateRate(temp.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
                }
                if (!last.getPrePrice().equals(BigDecimal.ZERO)) {
                    current.setPrePriceRate(current.getPrePrice().subtract(last.getPrePrice()).divide(
                            last.getPrePrice(),
                            4,
                            BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
                }
            } else {
                current.setUvRate(100);
                current.setOrderRate(100);
                current.setPaymentRate(100);
                current.setRefundRate(100);
                current.setPayConvertRateRate(100);
                current.setPrePriceRate(100);
            }
        }
        return current;
    }

    private DashBoardHeader queryDateTopic(int type, int year, Object currentValue) {
        AdsDateTopic adsDateTopic = this.adsDateTopicMapper.findSummaryByType(year, type, currentValue);
        if (adsDateTopic != null) {
            DashBoardHeader result = new DashBoardHeader();
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
                result.setPayConvertRate(((double) adsDateTopic.getPaymentUserCount()) * 100 / adsDateTopic.getUvCount());
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public List<Map> queryByDuration(int type, int duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int currentYear = 0;
        Object currentValue = "";
        switch (type) {
            case 1://周
                currentYear = calendar.get(Calendar.YEAR);
                currentValue = calendar.get(Calendar.WEEK_OF_YEAR);
                break;
            case 2://月
                currentYear = calendar.get(Calendar.YEAR);
                currentValue = calendar.get(Calendar.MONTH) + 1;
                break;
            default:
                type = 0;
                currentValue = duration;
                break;
        }
        List<Map> result = this.adsDateTopicMapper.findByDuration(type, currentYear, currentValue);
        return result;
    }
}
