package org.tlh.dw.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.entity.AdsAppraiseBadTopn;
import org.tlh.dw.service.IAdsAppraiseBadTopnService;
import org.tlh.dw.util.ResponseUtil;

/**
 * <p>
 * 商品差评率 TopN 前端控制器
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@RestController
@RequestMapping("/adsAppraiseBadTopn")
public class AdsAppraiseBadTopnController {

    @Autowired
    private IAdsAppraiseBadTopnService badTopnService;

    @GetMapping("/list")
    public Object list(@RequestParam(name = "date", required = false) String date,
                       @RequestParam(name = "page",defaultValue = "1") int page,
                       @RequestParam(name = "limit",defaultValue = "20") int limit,
                       @RequestParam("sort") String column,
                       @RequestParam("order") String order) {
        //分页
        Page<AdsAppraiseBadTopn> p = new Page<>(page, limit);
        //排序
        OrderItem item = new OrderItem();
        item.setColumn(column);
        item.setAsc("asc".equals(order));
        p.addOrder(item);
        //查询
        IPage<AdsAppraiseBadTopn> result = this.badTopnService.queryByDate(p, date);
        return ResponseUtil.ok(result);
    }

}

