package org.tlh.profile.mapper;

import org.tlh.profile.entity.TbBasicTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.tlh.profile.vo.BasicTagListVo;
import org.tlh.profile.vo.ElementTreeVo;

import java.util.List;

/**
 * <p>
 * 基础标签 Mapper 接口
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
public interface TbBasicTagMapper extends BaseMapper<TbBasicTag> {

    List<ElementTreeVo> queryPrimaryTree();

    List<ElementTreeVo> leftTree();

    List<BasicTagListVo> queryChildTagAndModelById(Long id);
}
