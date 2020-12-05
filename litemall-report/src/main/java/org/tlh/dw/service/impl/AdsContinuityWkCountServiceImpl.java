package org.tlh.dw.service.impl;

import org.tlh.dw.entity.AdsContinuityWkCount;
import org.tlh.dw.mapper.AdsContinuityWkCountMapper;
import org.tlh.dw.service.IAdsContinuityWkCountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 最近连续三周活跃用户数 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Service
public class AdsContinuityWkCountServiceImpl extends ServiceImpl<AdsContinuityWkCountMapper, AdsContinuityWkCount> implements IAdsContinuityWkCountService {

}
