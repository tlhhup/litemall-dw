package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.dw.config.SimulateProperty;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class CartInfoService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private CommonDataService commonDataService;

    public void genCartInfo() {

    }
}
