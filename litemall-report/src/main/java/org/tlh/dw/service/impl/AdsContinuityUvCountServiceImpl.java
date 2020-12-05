package org.tlh.dw.service.impl;

import org.tlh.dw.entity.AdsContinuityUvCount;
import org.tlh.dw.mapper.AdsContinuityUvCountMapper;
import org.tlh.dw.service.IAdsContinuityUvCountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 最近七天连续三天活跃用户数 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Service
public class AdsContinuityUvCountServiceImpl extends ServiceImpl<AdsContinuityUvCountMapper, AdsContinuityUvCount> implements IAdsContinuityUvCountService {

}
