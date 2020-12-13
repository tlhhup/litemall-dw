package org.tlh.dw.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.entity.AdsUserActionConvertDay;
import org.tlh.dw.service.IAdsUserActionConvertDayService;
import org.tlh.dw.util.ResponseUtil;

import java.util.*;

/**
 * <p>
 * 用户行为漏斗分析 前端控制器
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@RestController
@RequestMapping("/adsUserActionConvertDay")
public class AdsUserActionConvertDayController {

    @Autowired
    private IAdsUserActionConvertDayService adsUserActionConvertDayService;

    @GetMapping("/list")
    public Object list(String date){
        List<Map<String,Object>> result=new ArrayList<>();
        if (StringUtils.isEmpty(date)) {
            date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        }
        QueryWrapper wrapper = new QueryWrapper<AdsUserActionConvertDay>().eq("dt", date);
        AdsUserActionConvertDay convertDay = this.adsUserActionConvertDayService.getOne(wrapper);
        if (convertDay!=null) {
            Map<String, Object> item = new HashMap<>();
            item.put("name","访问");
            item.put("value",100);
            result.add(item);

            item = new HashMap<>();
            item.put("name","加购");
            item.put("value",convertDay.getCart2orderConvertRatio().doubleValue()*100);
            result.add(item);

            item = new HashMap<>();
            item.put("name","订单");
            item.put("value",convertDay.getCart2orderConvertRatio().doubleValue()*100);
            result.add(item);

            item = new HashMap<>();
            item.put("name","支付");
            item.put("value",convertDay.getOrder2paymentConvertRatio().doubleValue()*100);
            result.add(item);
        }
        return ResponseUtil.ok(result);
    }

}

