package org.tlh.dw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.service.DashBoardService;
import org.tlh.dw.util.ResponseUtil;
import org.tlh.dw.vo.DashBoardHeader;
import org.tlh.dw.vo.StatVo;

import java.util.List;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
@RestController
@RequestMapping("/dashBoard")
public class DashBoardController {

    @Autowired
    private DashBoardService dashBoardService;

    @GetMapping("/list")
    public Object list(int type) {
        DashBoardHeader result = this.dashBoardService.queryByDate(type);
        return ResponseUtil.ok(result);
    }

    @GetMapping("/chart")
    public Object chart(@RequestParam(name = "duration", required = false, defaultValue = "30") int duration,
                        @RequestParam(name = "type") int type) {
        List<Map> rows = this.dashBoardService.queryByDuration(type,duration);
        String[] columns = new String[]{"day", "orderCount", "paymentCount", "refundCount", "newUserCount"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return ResponseUtil.ok(statVo);
    }

}
