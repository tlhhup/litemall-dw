package org.tlh.dw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-03
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "org.tlh.dw.mapper")
public class DwApplication {

    public static void main(String[] args) {
        SpringApplication.run(DwApplication.class, args);
    }

}
