package org.tlh.dw.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.tlh.dw.entity.*;
import org.tlh.dw.mapper.AdsRegionDayCountMapper;
import org.tlh.dw.service.*;
import org.tlh.dw.util.Constants;
import org.tlh.dw.util.JsonUtil;
import org.tlh.dw.vo.EchartBarVo;
import org.tlh.dw.vo.OrderSpeedVo;
import org.tlh.dw.vo.RealTimeVo;
import org.tlh.dw.vo.RegionOrderVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-04
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReportBoardServiceImpl implements ReportBoardService {

    @Autowired
    private IAdsUserActionConvertDayService adsUserActionConvertDayService;

    @Autowired
    private IAdsProductSaleTopnService productSaleTopnService;

    @Autowired
    private AdsRegionDayCountMapper regionDayCountMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IAdsProductFavorTopnService favorTopnService;

    @Autowired
    private LitemallRegionGeoService litemallRegionGeoService;

    @Override
    public List<Map<String, Object>> uaConvert(String date) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (StringUtils.isEmpty(date)) {
            date = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
        }
        QueryWrapper wrapper = new QueryWrapper<AdsUserActionConvertDay>().eq("dt", date);
        AdsUserActionConvertDay convertDay = this.adsUserActionConvertDayService.getOne(wrapper);
        if (convertDay != null) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "访问");
            item.put("value", 100);
            result.add(item);

            item = new HashMap<>();
            item.put("name", "加购");
            double addCartRate = convertDay.getVisitor2cartConvertRatio().doubleValue();
            item.put("value", addCartRate * 100);
            result.add(item);

            item = new HashMap<>();
            item.put("name", "订单");
            double orderRate = convertDay.getCart2orderConvertRatio().doubleValue() * addCartRate;
            item.put("value", orderRate * 100);
            result.add(item);

            item = new HashMap<>();
            item.put("name", "支付");
            double paymentRate = convertDay.getOrder2paymentConvertRatio().doubleValue() * orderRate;
            item.put("value", paymentRate * 100);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<EchartBarVo> saleTopN(String date) {
        if (StringUtils.isEmpty(date)) {
            date = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
        }
        Wrapper<AdsProductSaleTopn> wrapper = new QueryWrapper<AdsProductSaleTopn>().eq("dt", date);
        List<AdsProductSaleTopn> data = this.productSaleTopnService.list(wrapper);
        if (!ObjectUtils.isEmpty(data)) {
            List<EchartBarVo> result = data.stream()
                    .map(item -> new EchartBarVo(item.getSpuName() + "", item.getPaymentCount()))
                    .collect(Collectors.toList());
            return result;
        }
        return null;
    }

    @Override
    public List<RegionOrderVo> regionOrder(String date, int type, String name) {
        if (StringUtils.isEmpty(date)) {
            date = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
        }
        List<AdsRegionDayCount> temp = null;
        switch (type) {
            case 0:
                temp = this.regionDayCountMapper.provinceSummary(date);
                break;
            case 1:
                if (Constants.SPECIAL_REGIONS.contains(name)) {
                    temp = this.regionDayCountMapper.countrySummary(date, name);
                } else {
                    temp = this.regionDayCountMapper.citySummary(date, name);
                }
                break;
            case 2:
                temp = this.regionDayCountMapper.countrySummary(date, name);
                break;
        }
        if (temp == null) {
            return null;
        }
        List<RegionOrderVo> result = temp.stream()
                .map(item -> new RegionOrderVo(
                        item.getProvinceName(),
                        item.getOrderDayCount(),
                        item.getOrderDayAmount().doubleValue())
                ).collect(Collectors.toList());
        return result;
    }

    @Override
    public RealTimeVo realTime() {
        RealTimeVo result = new RealTimeVo();
        String prefix = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        // 订单
        String value = ops.get(prefix + Constants.ORDER_COUNT);
        if (StringUtils.hasText(value)) {
            result.setOrderCount(Long.parseLong(value));
        }
        value = ops.get(prefix + Constants.ORDER_AMOUNT);
        if (StringUtils.hasText(value)) {
            result.setOrderAmount(Double.parseDouble(value));
        }

        // 支付
        value = ops.get(prefix + Constants.PAY_COUNT);
        if (StringUtils.hasText(value)) {
            result.setPayCount(Long.parseLong(value));
        }
        value = ops.get(prefix + Constants.PAY_AMOUNT);
        if (StringUtils.hasText(value)) {
            result.setPayAmount(Double.parseDouble(value));
        }
        return result;
    }

    @Override
    public List<OrderSpeedVo> orderSpeed() {
        Set<String> keys = this.redisTemplate.keys(Constants.ORDER_SPEED + "*");
        if (ObjectUtils.isEmpty(keys)) {
            return null;
        }
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        List<OrderSpeedVo> result = new ArrayList<>();
        Set<String> sortSet = new TreeSet<>();
        sortSet.addAll(keys);
        OrderSpeedVo item = null;
        for (String key : sortSet) {
            String value = ops.get(key);
            String time = key.split(":")[2];
            time = DateFormatUtils.format(Long.parseLong(time), "HH:mm:ss");

            item = new OrderSpeedVo(time, Long.parseLong(value));
            result.add(item);
            item = null;
        }
        return result;
    }

    @Override
    public List<EchartBarVo> favorTopN(String date) {
        if (StringUtils.isEmpty(date)) {
            date = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
        }
        Wrapper<AdsProductFavorTopn> wrapper = new QueryWrapper<AdsProductFavorTopn>().eq("dt", date);
        List<AdsProductFavorTopn> data = this.favorTopnService.list(wrapper);
        if (!ObjectUtils.isEmpty(data)) {
            List<EchartBarVo> result = data.stream()
                    .map(item -> new EchartBarVo(item.getSpuName() + "", item.getFavorCount()))
                    .collect(Collectors.toList());
            return result;
        }
        return null;
    }

    @Override
    public List<Object[]> realTimeRegionOrder() {
        // 1. 获取redis topN 数据
        String prefix = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        ZSetOperations<String, String> ops = this.redisTemplate.opsForZSet();
        // 区域订单
        Set<ZSetOperations.TypedTuple<String>> regions = ops.rangeWithScores(prefix + Constants.REGION_ORDER_COUNT, 0, Constants.REGION_ORDER_COUNT_TOP_N-1);
        if (ObjectUtils.isEmpty(regions)){
            return null;
        }
        Set<String> regionIds = regions.stream().map(item -> item.getValue()).collect(Collectors.toSet());
        // 2. 查询数据获取geo
        Wrapper<LitemallRegionGeo> wrapper=new QueryWrapper<LitemallRegionGeo>().in("region_id",regionIds);
        List<LitemallRegionGeo> litemallRegionGeos = this.litemallRegionGeoService.list(wrapper);
        if (ObjectUtils.isEmpty(litemallRegionGeos)){
            return null;
        }
        Map<Integer,String> regionMap=new HashMap<>();
        litemallRegionGeos.forEach(item->{
            regionMap.put(item.getRegionId(),item.getGeo());
        });
        // 3. 封装数据
        List<Object[]> result=new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> region : regions) {
            //3.1 geo数据
            String geoJson = regionMap.get(Integer.parseInt(region.getValue()));
            if (StringUtils.hasText(geoJson)) {
                //3.2 转换为数据
                List<String> geo = JsonUtil.toList(geoJson, String.class);
                //3.3 添加订单数
                geo.add(region.getScore().toString());
                result.add(geo.toArray());
            }
        }
        return result;
    }
}
