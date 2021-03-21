package org.tlh.profile.service;

import org.tlh.profile.dto.BasicTagDto;
import org.tlh.profile.entity.TbBasicTag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 基础标签 服务类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
public interface ITbBasicTagService extends IService<TbBasicTag> {

    /**
     * 创建主分类标签(1,2,3级标签)
     *
     * @param basicTag
     * @return
     */
    boolean createPrimaryTag(BasicTagDto basicTag);
}
