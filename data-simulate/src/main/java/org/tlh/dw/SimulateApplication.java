package org.tlh.dw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-19
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"org.linlinjava.litemall.db", "org.linlinjava.litemall.core", "org.tlh.dw"})
@MapperScan("org.linlinjava.litemall.db.dao")
@EnableTransactionManagement
public class SimulateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimulateApplication.class, args);
    }

}
