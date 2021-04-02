package org.tlh.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.tlh.profile.dto.MergeTagDetailDto;
import org.tlh.profile.dto.MergeTagDto;
import org.tlh.profile.entity.TbMergeTag;
import org.tlh.profile.entity.TbMergeTagDetail;
import org.tlh.profile.enums.ModelTaskState;
import org.tlh.profile.enums.QueryCondition;
import org.tlh.profile.mapper.TbMergeTagMapper;
import org.tlh.profile.service.ITbMergeTagDetailService;
import org.tlh.profile.service.ITbMergeTagService;
import org.tlh.profile.vo.MergeTagListVo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 组合标签 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class TbMergeTagServiceImpl extends ServiceImpl<TbMergeTagMapper, TbMergeTag> implements ITbMergeTagService {

    @Autowired
    private ITbMergeTagDetailService mergeTagDetailService;

    @Autowired
    private TbMergeTagMapper mergeTagMapper;

    @Override
    @Transactional
    public boolean createMergeTag(MergeTagDto mergeTag) {
        try {
            //1.保存基本信息
            TbMergeTag target = new TbMergeTag();
            BeanUtils.copyProperties(mergeTag, target);
            target.setState(ModelTaskState.DEVELOPED.getState());
            boolean c1 = this.save(target);
            //2.保存规则信息
            List<TbMergeTagDetail> details = new ArrayList<>();
            TbMergeTagDetail detail = null;
            for (int i = 0; i < mergeTag.getTags().size(); i++) {
                MergeTagDetailDto item = mergeTag.getTags().get(i);
                detail = new TbMergeTagDetail();
                detail.setBasicTagId(item.getTagId());
                detail.setMergeTagId(target.getId());
                detail.setCondition(QueryCondition.Operator.convert(item.getCondition()).getType());
                detail.setConditionOrder(i + 1);
                details.add(detail);
                detail = null;
            }
            boolean c2 = this.mergeTagDetailService.saveBatch(details);
            return c1 && c2;
        } catch (BeansException e) {
            log.error("create mergeTag error", e);
        }
        return false;
    }

    @Override
    public List<MergeTagListVo> queryTags(String name) {
        QueryWrapper<TbMergeTag> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.eq("name", name);
        }
        List<TbMergeTag> list = this.list(wrapper);
        List<MergeTagListVo> collect = list.stream().map(item -> {
            MergeTagListVo result = new MergeTagListVo();
            BeanUtils.copyProperties(item, result);
            return result;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    @Transactional
    public boolean removeMergeTag(long id) {
        try {
            //1.删除关系表
            QueryWrapper<TbMergeTagDetail> wrapper = new QueryWrapper<>();
            wrapper.eq("merge_tag_id", id);
            boolean c1 = this.mergeTagDetailService.remove(wrapper);
            //2.删除组合标签
            boolean c2 = this.removeById(id);
            return c1 && c2;
        } catch (Exception e) {
            log.error("remove merge tag error", e);
        }
        return false;
    }

    @Override
    public MergeTagListVo getMergeTagDetail(long id) {
        MergeTagListVo result = this.mergeTagMapper.queryDetailById(id);
        return result;
    }

    @Override
    @Transactional
    public boolean updateMergeTag(MergeTagListVo mergeTag) {
        try {
            //1.更新基本信息
            TbMergeTag target = new TbMergeTag();
            BeanUtils.copyProperties(mergeTag, target);
            target.setUpdateTime(LocalDateTime.now());
            boolean c1 = this.updateById(target);
            //2.删除规则
            QueryWrapper<TbMergeTagDetail> wrapper = new QueryWrapper<>();
            wrapper.eq("merge_tag_id", mergeTag.getId());
            boolean c2 = this.mergeTagDetailService.remove(wrapper);
            //3.保存规则信息
            List<TbMergeTagDetail> details = new ArrayList<>();
            TbMergeTagDetail detail = null;
            for (int i = 0; i < mergeTag.getTags().size(); i++) {
                MergeTagDetailDto item = mergeTag.getTags().get(i);
                detail = new TbMergeTagDetail();
                detail.setBasicTagId(item.getTagId());
                detail.setMergeTagId(target.getId());
                detail.setCondition(QueryCondition.Operator.convert(item.getCondition()).getType());
                detail.setConditionOrder(i + 1);
                details.add(detail);
                detail = null;
            }
            boolean c3 = this.mergeTagDetailService.saveBatch(details);
            return c1 && c2 && c3;
        } catch (BeansException e) {
            log.error("create mergeTag error", e);
        }
        return false;
    }
}
