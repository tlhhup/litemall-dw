package org.tlh.dw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.tlh.dw.entity.AdsDateTopic;

import java.util.List;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
public interface AdsDateTopicMapper extends BaseMapper<AdsDateTopic> {

    List<Map> findByDuration(int duration);

}
