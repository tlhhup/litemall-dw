package org.tlh.dw.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.dw.entity.AdsAppraiseBadTopn;

import java.util.List;


/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-05
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class IAdsAppraiseBadTopnServiceTest {

    @Autowired
    private IAdsAppraiseBadTopnService adsAppraiseBadTopnService;

    @Test
    public void list(){
        List<AdsAppraiseBadTopn> list = this.adsAppraiseBadTopnService.list();
        System.out.println(list.size());
    }

}