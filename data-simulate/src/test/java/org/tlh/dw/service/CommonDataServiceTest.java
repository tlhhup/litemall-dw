package org.tlh.dw.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.dw.bean.RegionInfo;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-21
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonDataServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void region(){
        String sql="select\n" +
                "\tprovince.id as pId,\n" +
                "\tprovince.name as pName,\n" +
                "\tcity.id as cId,\n" +
                "\tcity.name as cName,\n" +
                "\tcountry.id as tId,\n" +
                "\tcountry.name as tName,\n" +
                "\tcountry.code as code\n" +
                "from\n" +
                "(\n" +
                "\tselect\n" +
                "\t\tid,\n" +
                "\t\tname\n" +
                "\tfrom litemall_region\n" +
                "\twhere type=1\n" +
                ") as province\n" +
                "join \n" +
                "(\n" +
                "\tselect\n" +
                "\t\tpid,\n" +
                "\t\tid,\n" +
                "\t\tname\n" +
                "\tfrom litemall_region\n" +
                "\twhere type=2\n" +
                ") as city\n" +
                "on province.id=city.pid\n" +
                "join \n" +
                "(\n" +
                "\tselect\n" +
                "\t\tpid,\n" +
                "\t\tid,\n" +
                "\t\tname,\n" +
                "\t\tcode\n" +
                "\tfrom litemall_region\n" +
                "\twhere type=3\n" +
                ") as country\n" +
                "on city.id=country.pid";
        List<RegionInfo> regions = this.jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RegionInfo.class));
        System.out.println(regions.size());
    }

}