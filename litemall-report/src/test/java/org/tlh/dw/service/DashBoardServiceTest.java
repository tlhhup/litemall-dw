package org.tlh.dw.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.dw.vo.DashBoardHeader;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-09
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DashBoardServiceTest {

    @Autowired
    private DashBoardService dashBoardService;

    @Test
    public void queryByDate() {
        DashBoardHeader dashBoardHeader = this.dashBoardService.queryByDate(1);
        System.out.println(dashBoardHeader);
    }

    @Test
    public void queryByDuration() {
    }
}