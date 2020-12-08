package org.tlh.dw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.service.DashBoardService;
import org.tlh.dw.util.ResponseUtil;
import org.tlh.dw.vo.DashBoardHeader;

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
    public Object list(String date,int type) {
        DashBoardHeader result = this.dashBoardService.queryByDate(date, type);
        return ResponseUtil.ok(result);
    }

}
