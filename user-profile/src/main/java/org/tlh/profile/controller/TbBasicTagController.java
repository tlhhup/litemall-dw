package org.tlh.profile.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tlh.profile.dto.BasicTagDto;
import org.tlh.profile.dto.DeleteTagDto;
import org.tlh.profile.dto.ModelTagDto;
import org.tlh.profile.service.ITbBasicTagService;
import org.tlh.profile.util.ResponseUtil;
import org.tlh.profile.vo.BasicTagListVo;
import org.tlh.profile.vo.ElementTreeVo;

import java.util.List;

/**
 * <p>
 * 基础标签 前端控制器
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@RestController
@RequestMapping("/basicTag")
public class TbBasicTagController {

    @Autowired
    private ITbBasicTagService basicTagService;

    @GetMapping("/list")
    public Object list() {
        List<ElementTreeVo> tree = this.basicTagService.leftTree();
        return ResponseUtil.ok(tree);
    }

    @PostMapping("/primaryTag")
    public Object addPrimaryTag(@RequestBody BasicTagDto basicTag) {
        boolean flag = this.basicTagService.createPrimaryTag(basicTag);
        return ResponseUtil.ok(flag);
    }

    @GetMapping("primaryTagTree")
    public Object primaryTagTree() {
        List<ElementTreeVo> treeVos = this.basicTagService.queryPrimaryTree();
        return ResponseUtil.ok(treeVos);
    }

    @GetMapping("/childTags")
    public Object getChildren(@RequestParam(name = "pid", required = false) Long pId) {
        List<BasicTagListVo> tags = this.basicTagService.childTags(pId);
        return ResponseUtil.ok(tags);
    }

    @GetMapping("/searchTag/{name}")
    public Object searchTag(@PathVariable("name") String name) {
        List<BasicTagDto> tags = this.basicTagService.queryByTagName(name);
        return ResponseUtil.ok(tags);
    }

    @PostMapping("/modelTag")
    public Object createModelTag(@RequestBody ModelTagDto modelTag) {
        boolean flag = this.basicTagService.createModelTag(modelTag);
        return ResponseUtil.ok(flag);
    }

    @PostMapping("/saveModelRule")
    public Object saveModelRule(@RequestBody BasicTagDto basicTag) {
        boolean flag = this.basicTagService.saveModelRule(basicTag);
        return ResponseUtil.ok(flag);
    }

    @DeleteMapping("/deleteTag")
    public Object deleteTag(@RequestBody DeleteTagDto deleteTag) {
        boolean flag = this.basicTagService.deleteTag(deleteTag);
        return ResponseUtil.ok(flag);
    }

    @PutMapping("/updatePrimary")
    public Object updatePrimaryTag(@RequestBody BasicTagDto basicTag) {
        boolean flag = this.basicTagService.updatePrimaryTag(basicTag);
        return ResponseUtil.ok(flag);
    }

    @PutMapping("/updateModelTagRule")
    public Object updateModelTagRule(@RequestBody BasicTagDto basicTag){
        boolean flag = this.basicTagService.updateModelTagRule(basicTag);
        return ResponseUtil.ok(flag);
    }

    @PutMapping("/updateModelTag")
    public Object updateModelTag(@RequestBody ModelTagDto modelTag){
        boolean flag = this.basicTagService.updateModelTag(modelTag);
        return ResponseUtil.ok(flag);
    }

}

