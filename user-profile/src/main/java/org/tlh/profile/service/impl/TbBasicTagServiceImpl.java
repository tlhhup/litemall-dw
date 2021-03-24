package org.tlh.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.profile.dto.BasicTagDto;
import org.tlh.profile.dto.ModelTagDto;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.entity.TbTagMetadata;
import org.tlh.profile.entity.TbTagModel;
import org.tlh.profile.enums.ModelTaskState;
import org.tlh.profile.enums.OozieScheduleType;
import org.tlh.profile.mapper.TbBasicTagMapper;
import org.tlh.profile.service.ITbBasicTagService;
import org.tlh.profile.service.ITbTagMetadataService;
import org.tlh.profile.service.ITbTagModelService;
import org.tlh.profile.util.ModelMetaDataParseUtil;
import org.tlh.profile.vo.BasicTagListVo;
import org.tlh.profile.vo.ElementTreeVo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 基础标签 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Service
@Transactional(readOnly = true)
public class TbBasicTagServiceImpl extends ServiceImpl<TbBasicTagMapper, TbBasicTag> implements ITbBasicTagService {

    @Autowired
    private TbBasicTagMapper basicTagMapper;

    @Autowired
    private ITbTagModelService modelService;

    @Autowired
    private ITbTagMetadataService metadataService;

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

    @Override
    public List<ElementTreeVo> leftTree() {
        return this.basicTagMapper.leftTree();
    }

    @Override
    public List<BasicTagListVo> childTags(Long pid) {
        List<BasicTagListVo> result = this.basicTagMapper.queryChildTagAndModelById(pid);
        result = result.stream().map(item -> {
            String schedule = item.getSchedule();
            if (StringUtils.isNotEmpty(schedule)) {
                String[] rules = schedule.split(",");
                StringBuilder builder = new StringBuilder();
                builder.append(OozieScheduleType.convert(Integer.parseInt(rules[0])).getName())
                        .append("#")
                        .append(rules[1])
                        .append("~")
                        .append(rules[2]);
                item.setSchedule(builder.toString());
            }
            return item;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<BasicTagDto> queryByTagName(String name) {
        QueryWrapper<TbBasicTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        List<TbBasicTag> tags = this.list(queryWrapper);
        List<BasicTagDto> result = tags.stream().map(item -> {
            BasicTagDto target = new BasicTagDto();
            BeanUtils.copyProperties(item, target);
            return target;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    @Transactional
    public boolean createModelTag(ModelTagDto modelTag) {
        //1.保存业务标签数据
        TbBasicTag target = new TbBasicTag();
        BeanUtils.copyProperties(modelTag, target);
        target.setLevel(4);
        //设置标签状态
        target.setState(ModelTaskState.SUBMIT.getState());
        boolean tag = this.save(target);
        //2.保存模型数据
        TbTagModel tagModel = new TbTagModel();
        tagModel.setTagId(target.getId());
        tagModel.setModelArgs(modelTag.getModelArgs());
        String modelPath = modelTag.getModelJar();
        String modelJar = modelPath.substring(modelPath.lastIndexOf("/") + 1);
        tagModel.setModelJar(modelJar);
        tagModel.setModelMain(modelTag.getModelMain());
        tagModel.setModelName(modelTag.getModelName());
        tagModel.setModelPath(modelPath);
        //设置定时规则
        String rule = modelTag.getSchedule() + "," + StringUtils.join(modelTag.getStarEnd(), ",");
        tagModel.setScheduleRule(rule);
        boolean model = modelService.save(tagModel);
        //3.保存元数据
        boolean metaData = this.saveTagMetaData(target.getId(), modelTag.getRule());
        return tag && model && metaData;
    }

    private boolean saveTagMetaData(Long tagId, String metaData) {
        //1.判断数据有效性
        if (StringUtils.isEmpty(metaData) || tagId == null) {
            throw new IllegalArgumentException("TagId or rule is null!");
        }
        //2.解析数据
        TbTagMetadata metadata = ModelMetaDataParseUtil.parse(metaData);
        metadata.setTagId(tagId);
        return metadataService.save(metadata);
    }


    @Override
    @Transactional
    public boolean saveModelRule(BasicTagDto basicTag) {
        try {
            TbBasicTag target = new TbBasicTag();
            BeanUtils.copyProperties(basicTag, target);
            target.setLevel(5);
            return this.save(target);
        } catch (BeansException e) {
            log.error("save model rule error", e);
        }
        return false;
    }
}
