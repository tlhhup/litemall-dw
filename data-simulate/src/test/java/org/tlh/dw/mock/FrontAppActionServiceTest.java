package org.tlh.dw.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-15
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FrontAppActionServiceTest {

    @Autowired
    private FrontAppActionService frontAppActionService;

    @Test
    public void register(){
        this.frontAppActionService.process();
    }

    @Test
    public void order(){
        this.frontAppActionService.process();
    }

    @Test
    public void payment(){
        this.frontAppActionService.process();
    }

    @Test
    public void confirm(){
        this.frontAppActionService.process();
    }

    @Test
    public void refund(){
        this.frontAppActionService.process();
    }

    @Test
    public void comment(){
        this.frontAppActionService.process();
    }

    @Test
    public void collect(){
        this.frontAppActionService.process();
    }

}