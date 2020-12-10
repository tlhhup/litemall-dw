package org.tlh.dw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.dw.entity.AdsRegionDayCount;
import org.tlh.dw.mapper.AdsRegionDayCountMapper;
import org.tlh.dw.service.IAdsRegionDayCountService;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-10
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AdsRegionDayCountServiceImpl extends ServiceImpl<AdsRegionDayCountMapper, AdsRegionDayCount> implements IAdsRegionDayCountService {
}
