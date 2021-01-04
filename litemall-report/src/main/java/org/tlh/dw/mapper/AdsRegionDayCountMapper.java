package org.tlh.dw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.tlh.dw.entity.AdsRegionDayCount;

import java.util.List;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-10
 */
public interface AdsRegionDayCountMapper extends BaseMapper<AdsRegionDayCount> {

    /**
     * 省份汇总
     * @param date
     * @return
     */
    List<AdsRegionDayCount> provinceSummary(String date);

    /**
     * 市
     * @param date
     * @param provinceName 省份名
     * @return
     */
    List<AdsRegionDayCount> citySummary(@Param("date") String date,@Param("name") String provinceName);

    /**
     * 区/县
     * @param date
     * @param name 城市名称
     * @return
     */
    List<AdsRegionDayCount> countrySummary(@Param("date") String date,@Param("name") String name);

}
