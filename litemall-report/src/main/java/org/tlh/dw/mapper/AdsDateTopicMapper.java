package org.tlh.dw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.tlh.dw.entity.AdsDateTopic;

import java.util.List;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
public interface AdsDateTopicMapper extends BaseMapper<AdsDateTopic> {

    /**
     * 通过类型汇总
     *
     * @param year  年份
     * @param type  0 天 1 周 2 月
     * @param value 查询条件值
     * @return
     */
    AdsDateTopic findSummaryByType(@Param("year") int year, @Param("type") int type, @Param("v") Object value);

    List<Map> findByDuration(@Param("type") int type, @Param("year") int year, @Param("v") Object currentValue);

}
