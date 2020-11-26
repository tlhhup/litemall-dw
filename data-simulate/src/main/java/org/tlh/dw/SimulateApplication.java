package org.tlh.dw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.tlh.dw.config.SimulateProperty;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-19
 */
@EnableScheduling
@EnableTransactionManagement
@MapperScan("org.linlinjava.litemall.db.dao")
@EnableConfigurationProperties({SimulateProperty.class})
@SpringBootApplication(scanBasePackages = {"org.linlinjava.litemall.db", "org.tlh.dw"})
public class SimulateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimulateApplication.class, args);
    }

}
