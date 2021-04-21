package org.tlh.profile.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.profile.entity.TbTagMetadata;
import org.tlh.profile.mapper.TbTagMetadataMapper;
import org.tlh.profile.service.ITbTagMetadataService;

import java.time.LocalDateTime;

/**
 * <p>
 * 标签数据元数据信息 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Service
public class TbTagMetadataServiceImpl extends ServiceImpl<TbTagMetadataMapper, TbTagMetadata> implements ITbTagMetadataService {

    @Autowired
    private TbTagMetadataMapper metadataMapper;

    @Override
    public boolean updateByTagId(TbTagMetadata metadata) {
        //1.设置更新时间
        metadata.setUpdateTime(LocalDateTime.now());
        //2.更新数据
        return this.metadataMapper.updateByTagId(metadata)>0;
    }
}
