package org.tlh.profile.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.profile.UserProfileApplication;
import org.tlh.profile.service.ITbMergeTagService;
import org.tlh.profile.vo.MergeTagListVo;

import static org.junit.Assert.*;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-02
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserProfileApplication.class)
public class TbMergeTagServiceImplTest {

    @Autowired
    private ITbMergeTagService mergeTagService;

    @Test
    public void getMergeTagDetail() {
        MergeTagListVo detail = this.mergeTagService.getMergeTagDetail(8);
        System.out.println(detail);
    }
}