package org.tlh.profile.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.tlh.profile.dto.BasicTagDto;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.mapper.TbBasicTagMapper;
import org.tlh.profile.service.ITbBasicTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.tlh.profile.vo.ElementTreeVo;

import java.util.List;

/**
 * <p>
 * 基础标签 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Service
public class TbBasicTagServiceImpl extends ServiceImpl<TbBasicTagMapper, TbBasicTag> implements ITbBasicTagService {

    @Autowired
    private TbBasicTagMapper basicTagMapper;

    @Override
    public boolean createPrimaryTag(BasicTagDto basicTag) {
        int level = 1;
        //1.计算当前节点的level
        if (basicTag.getPid() != null) {
            TbBasicTag tag = this.getById(basicTag.getPid());
            level = tag.getLevel() + 1;
        }
        //2.保存数据
        TbBasicTag target = new TbBasicTag();
        BeanUtils.copyProperties(basicTag, target);
        target.setLevel(level);

        return this.save(target);
    }

    @Override
    public List<ElementTreeVo> queryPrimaryTree() {
        return basicTagMapper.queryPrimaryTree();
    }
}
