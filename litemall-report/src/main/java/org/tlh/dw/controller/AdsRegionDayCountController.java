package org.tlh.dw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.entity.AdsRegionDayCount;
import org.tlh.dw.service.IAdsRegionDayCountService;
import org.tlh.dw.util.ResponseUtil;

import java.util.Date;
import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-10
 */
@RestController
@RequestMapping("/adsRegionDayCount")
public class AdsRegionDayCountController {

    @Autowired
    private IAdsRegionDayCountService adsRegionDayCountService;

    @GetMapping("/list")
    public Object list(String date) {
        if (StringUtils.isEmpty(date)) {
            date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        }
        QueryWrapper wrapper = new QueryWrapper<AdsRegionDayCount>().eq("date", date);
        List<AdsRegionDayCount> result = this.adsRegionDayCountService.list(wrapper);
        return ResponseUtil.ok(result);
    }

}
