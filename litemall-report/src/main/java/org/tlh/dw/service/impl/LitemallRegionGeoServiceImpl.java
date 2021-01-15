package org.tlh.dw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.dw.entity.LitemallRegionGeo;
import org.tlh.dw.mapper.LitemallRegionGeoMapper;
import org.tlh.dw.service.LitemallRegionGeoService;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-15
 */
@Service
@Transactional(readOnly = true)
public class LitemallRegionGeoServiceImpl extends ServiceImpl<LitemallRegionGeoMapper, LitemallRegionGeo> implements LitemallRegionGeoService {
}
