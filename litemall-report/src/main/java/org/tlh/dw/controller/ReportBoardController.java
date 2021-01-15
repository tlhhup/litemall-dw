package org.tlh.dw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.service.ReportBoardService;
import org.tlh.dw.util.ResponseUtil;
import org.tlh.dw.vo.EchartBarVo;
import org.tlh.dw.vo.OrderSpeedVo;
import org.tlh.dw.vo.RealTimeVo;
import org.tlh.dw.vo.RegionOrderVo;

import java.util.List;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-04
 */
@RestController
@RequestMapping("/report")
public class ReportBoardController {

    @Autowired
    private ReportBoardService reportBoardService;

    @GetMapping("/convert")
    public Object uaConvert(String date) {
        List<Map<String, Object>> result = this.reportBoardService.uaConvert(date);
        return ResponseUtil.ok(result);
    }

    @GetMapping("/sale")
    public Object saleTopN(String date) {
        List<EchartBarVo> result = this.reportBoardService.saleTopN(date);
        if (result == null) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.ok(result);
    }

    @GetMapping("/region")
    public Object regionOrder(String date, int type, String name) {
        List<RegionOrderVo> result = this.reportBoardService.regionOrder(date, type, name);
        if (ObjectUtils.isEmpty(result)) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.ok(result);
    }

    @GetMapping("/realTime")
    public Object realTime() {
        RealTimeVo result = this.reportBoardService.realTime();
        if (result != null) {
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.fail();
    }

    @GetMapping("/orderSpeed")
    public Object orderSpeed() {
        List<OrderSpeedVo> speed = this.reportBoardService.orderSpeed();
        if (ObjectUtils.isEmpty(speed)) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.ok(speed);
    }

    @GetMapping("/favor")
    public Object favorTopN(String date) {
        List<EchartBarVo> result = this.reportBoardService.favorTopN(date);
        if (result == null) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.ok(result);
    }

    @GetMapping("/regionOrderRealTime")
    public Object regionOrderRealTime() {
        List<Object[]> result = this.reportBoardService.realTimeRegionOrder();
        if (result == null) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.ok(result);
    }

}
