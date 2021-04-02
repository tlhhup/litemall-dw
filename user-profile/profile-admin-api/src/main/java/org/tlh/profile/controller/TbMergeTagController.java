package org.tlh.profile.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.tlh.profile.dto.MergeTagDto;
import org.tlh.profile.service.ITbMergeTagService;
import org.tlh.profile.util.ResponseUtil;
import org.tlh.profile.vo.MergeTagListVo;

import java.util.List;

/**
 * <p>
 * 组合标签 前端控制器
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@RestController
@RequestMapping("/mergeTag")
public class TbMergeTagController {

    @Autowired
    private ITbMergeTagService mergeTagService;

    @GetMapping("/list")
    public Object list(@RequestParam(name = "name", required = false) String name) {
        List<MergeTagListVo> result = this.mergeTagService.queryTags(name);
        return ResponseUtil.ok(result);
    }

    @PostMapping("/create")
    public Object saveMergeTag(@RequestBody MergeTagDto mergeTag) {
        boolean flag = this.mergeTagService.createMergeTag(mergeTag);
        return ResponseUtil.ok(flag);
    }

    @DeleteMapping("/remove/{id}")
    public Object removeMergeTag(@PathVariable("id") long id){
        boolean flag=this.mergeTagService.removeMergeTag(id);
        return ResponseUtil.ok(flag);
    }

    @GetMapping("/detail/{id}")
    public Object detail(@PathVariable("id") long id){
        MergeTagListVo result=this.mergeTagService.getMergeTagDetail(id);
        return ResponseUtil.ok(result);
    }

    @PutMapping("/update")
    public Object update(@RequestBody MergeTagListVo mergeTag){
        boolean flag=this.mergeTagService.updateMergeTag(mergeTag);
        return ResponseUtil.ok(flag);
    }


}

