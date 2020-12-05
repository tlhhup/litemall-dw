package org.tlh.dw.service.impl;

import org.tlh.dw.entity.AdsSilentCount;
import org.tlh.dw.mapper.AdsSilentCountMapper;
import org.tlh.dw.service.IAdsSilentCountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 沉默用户数 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Service
public class AdsSilentCountServiceImpl extends ServiceImpl<AdsSilentCountMapper, AdsSilentCount> implements IAdsSilentCountService {

}
