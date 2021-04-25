package org.tlh.profile.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.profile.UserProfileApplication;
import org.tlh.profile.vo.EChartsGraphVo;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserProfileApplication.class)
public class IUserTagSearchServiceTest {

    @Autowired
    private IUserTagSearchService userTagSearchService;

    @Test
    public void searchUserTagById() {
        EChartsGraphVo graphVo = this.userTagSearchService.searchUserTagById(10);
        System.out.println(graphVo);
    }
}