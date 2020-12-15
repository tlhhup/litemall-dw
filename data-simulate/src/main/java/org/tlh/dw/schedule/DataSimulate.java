package org.tlh.dw.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tlh.dw.mock.FrontAppActionService;
import org.tlh.dw.mock.UserActionDataMock;
import org.tlh.dw.mock.BusinessService;
import org.tlh.dw.service.CommonDataService;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-19
 */
@Component
public class DataSimulate {

    @Autowired
    private UserActionDataMock userActionDataMock;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private FrontAppActionService frontAppActionService;

    @Scheduled(cron = "${simulate.schedule.action}")
    public void action() {
        userActionDataMock.process();
    }

    @Value("${simulate.front-app.enable}")
    private boolean frondEnable;

    @Value("${simulate.offline-enable}")
    private boolean offLineEnable;

    @Scheduled(cron = "${simulate.schedule.business}")
    public void business() {
        if (offLineEnable) {
            businessService.process();
        }
    }

    @Scheduled(cron = "${simulate.schedule.frond}")
    public void appAction() {
        if (frondEnable) {
            this.frontAppActionService.process();
        }
    }

    @Value("${simulate.schedule.reload.enable}")
    private boolean reload;

    @Scheduled(cron = "${simulate.schedule.reload.cron}")
    public void reload() {
        if (reload) {
            this.commonDataService.reloadCommonData();
        }
    }

}
