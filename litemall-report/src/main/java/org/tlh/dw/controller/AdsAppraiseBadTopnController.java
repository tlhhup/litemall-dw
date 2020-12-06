package org.tlh.dw.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.dw.auth.RequiresPermissions;
import org.tlh.dw.entity.AdsAppraiseBadTopn;
import org.tlh.dw.service.IAdsAppraiseBadTopnService;
import org.tlh.dw.support.PageParam;
import org.tlh.dw.support.annotations.PageRequest;
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

    @RequiresPermissions("report:appraise:list")
    @GetMapping("/list")
    public Object list(@RequestParam(name = "date", required = false) String date,
                       @PageRequest PageParam pageParam) {
        //分页
        Page<AdsAppraiseBadTopn> p = new Page<>(pageParam.getPage(), pageParam.getLimit());
        //排序
        OrderItem item = new OrderItem();
        item.setColumn(pageParam.getSort());
        item.setAsc(pageParam.isAsc());
        p.addOrder(item);
        //查询
        IPage<AdsAppraiseBadTopn> result = this.badTopnService.queryByDate(p, date);
        return ResponseUtil.ok(result);
    }

}

