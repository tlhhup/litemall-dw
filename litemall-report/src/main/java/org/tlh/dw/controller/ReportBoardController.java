package org.tlh.dw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.service.ReportBoardService;
import org.tlh.dw.util.ResponseUtil;
import org.tlh.dw.vo.EchartBarVo;

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
    public Object saleTopN(String date){
        List<EchartBarVo> result = this.reportBoardService.saleTopN(date);
        if (result==null){
            return ResponseUtil.fail();
        }
        return ResponseUtil.ok(result);
    }

}
