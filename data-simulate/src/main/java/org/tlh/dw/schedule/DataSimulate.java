package org.tlh.dw.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tlh.dw.service.ActionService;
import org.tlh.dw.service.BusinessService;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-19
 */
@Component
public class DataSimulate {

    @Autowired
    private ActionService actionService;

    @Autowired
    private BusinessService businessService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void action(){
        actionService.process();
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void business(){
        businessService.process();
    }

}
