package org.tlh.profile.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tlh.profile.service.ITbTagModelService;
import org.tlh.profile.util.ResponseUtil;

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

}

