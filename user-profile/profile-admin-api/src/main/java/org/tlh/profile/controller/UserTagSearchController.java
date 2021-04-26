package org.tlh.profile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.profile.service.IUserTagSearchService;
import org.tlh.profile.util.ResponseUtil;
import org.tlh.profile.vo.EChartsGraphVo;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
@RestController
@RequestMapping("/userTag")
public class UserTagSearchController {

    @Autowired
    private IUserTagSearchService userTagSearchService;

    @GetMapping("/search")
    public Object searchTag(@RequestParam("userId") int userId) {
        EChartsGraphVo graphVo = this.userTagSearchService.searchUserTagById(userId);
        return ResponseUtil.ok(graphVo);
    }

}
