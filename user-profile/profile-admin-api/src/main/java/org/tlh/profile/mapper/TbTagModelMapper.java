package org.tlh.profile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.tlh.profile.entity.TbTagModel;
import org.tlh.profile.vo.BasicTagListVo;

import java.util.List;

/**
 * <p>
 * 标签模型 Mapper 接口
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
public interface TbTagModelMapper extends BaseMapper<TbTagModel> {

    List<BasicTagListVo> querySubmitModel();

}
