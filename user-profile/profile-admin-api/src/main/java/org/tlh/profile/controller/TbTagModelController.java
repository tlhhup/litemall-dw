package org.tlh.profile.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tlh.profile.dto.ApproveModelDto;
import org.tlh.profile.service.ITbTagModelService;
import org.tlh.profile.util.ResponseUtil;
import org.tlh.profile.vo.BasicTagListVo;

import java.util.List;

/**
 * <p>
 * 标签模型 前端控制器
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@RestController
@RequestMapping("/tagModel")
public class TbTagModelController {

    @Autowired
    private ITbTagModelService modelService;

    @PostMapping("/uploadModel")
    public Object uploadModelFile(@RequestParam("file") MultipartFile jar) {
        String path = this.modelService.uploadFile(jar);
        return ResponseUtil.ok(path);
    }

    @GetMapping("/submitModelList")
    public Object submitModelList(@RequestParam(name = "modelName",required = false) String modelName){
        List<BasicTagListVo> models= this.modelService.querySubmitModel(modelName);
        return ResponseUtil.ok(models);
    }

    @PutMapping("/approveModel")
    public Object updateModelState(@RequestBody ApproveModelDto approveModel){
        boolean flag=this.modelService.approveModel(approveModel);
        return ResponseUtil.ok(flag);
    }

}

