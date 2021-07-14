package org.tlh.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.profile.config.ProfileProperties;
import org.tlh.profile.dto.BasicTagDto;
import org.tlh.profile.dto.DeleteTagDto;
import org.tlh.profile.dto.ModelTagDto;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.entity.TbTagMetadata;
import org.tlh.profile.entity.TbTagModel;
import org.tlh.profile.enums.ModelTaskState;
import org.tlh.profile.enums.OozieScheduleType;
import org.tlh.profile.mapper.TbBasicTagMapper;
import org.tlh.profile.service.ITbBasicTagService;
import org.tlh.profile.service.ITbMergeTagService;
import org.tlh.profile.service.ITbTagMetadataService;
import org.tlh.profile.service.ITbTagModelService;
import org.tlh.profile.util.ModelMetaDataParseUtil;
import org.tlh.profile.vo.BasicTagListVo;
import org.tlh.profile.vo.ElementTreeVo;

import java.time.LocalDateTime;
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

    @Autowired
    private ProfileProperties profileProperties;

    @Autowired
    private ITbMergeTagService mergeTagService;

    @Override
    @Transactional
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
            String scheduleRule = item.getScheduleRule();
            if (StringUtils.isNotEmpty(scheduleRule)) {
                String[] rules = scheduleRule.split(",");
                item.setSchedule(Integer.parseInt(rules[0]));
                item.setStarEnd(new String[]{rules[1], rules[2]});

                //重新格式化rule
                StringBuilder builder = new StringBuilder();
                builder.append(OozieScheduleType.convert(Integer.parseInt(rules[0])).getName())
                        .append("#")
                        .append(rules[1])
                        .append("~")
                        .append(rules[2]);
                item.setScheduleRule(builder.toString());
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
        //0.解析元数据信息及校验合法性
        TbTagMetadata metadata = this.buildTagMetaData(modelTag.getRule());
        if (StringUtils.isEmpty(metadata.getOutFields())){
            throw new IllegalArgumentException(("outFields must not be null"));
        }

        //1.保存业务标签数据
        TbBasicTag target = new TbBasicTag();
        BeanUtils.copyProperties(modelTag, target);
        target.setLevel(4);
        //设置标签状态
        target.setState(ModelTaskState.SUBMIT.getState());
        //设置标签最后的输出
        target.setHbaseFields(metadata.getOutFields());
        boolean tag = this.save(target);
        //2.保存模型数据
        TbTagModel tagModel = new TbTagModel();
        tagModel.setTagId(target.getId());
        tagModel.setModelArgs(modelTag.getModelArgs());
        String modelPath = modelTag.getModelPath();
        String modelJar = modelPath.substring(modelPath.lastIndexOf("/") + 1);
        tagModel.setModelJar(modelJar);
        tagModel.setModelMain(modelTag.getModelMain());
        tagModel.setModelName(modelTag.getModelName());
        tagModel.setModelPath(modelPath);
        //设置定时规则
        String rule = modelTag.getSchedule() + "," + StringUtils.join(modelTag.getStarEnd(), ",");
        tagModel.setScheduleRule(rule);
        //设置spark参数
        if (StringUtils.isEmpty(modelTag.getSparkOpts())) {
            tagModel.setSparkOpts(this.profileProperties.getOozie().getSparkOpts());
        } else {
            tagModel.setSparkOpts(modelTag.getSparkOpts());
        }
        boolean model = modelService.save(tagModel);
        //3.保存元数据
        metadata.setTagId(target.getId());
        boolean metaData = this.metadataService.save(metadata);
        return tag && model && metaData;
    }

    private TbTagMetadata buildTagMetaData(String metaData) {
        //1.判断数据有效性
        if (StringUtils.isEmpty(metaData)) {
            throw new IllegalArgumentException("rule is null!");
        }
        //2.解析数据
        TbTagMetadata metadata = ModelMetaDataParseUtil.parse(metaData);
        return metadata;
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

    @Override
    @Transactional
    public boolean deleteTag(DeleteTagDto deleteTag) {
        boolean flag = false;
        //1.标签级别不同处理方式
        switch (deleteTag.getLevel()) {
            case 5:
                //判断父级标签是否是运行状态
                TbBasicTag parent = this.getById(deleteTag.getPId());
                if (parent != null && ModelTaskState.convert(parent.getState()) == ModelTaskState.ONLINE) {
                    throw new IllegalStateException("依赖的模型标签处于运行状态，请先停止模型:" + parent.getName());
                }
                // 判断是否有组合标签使用，且其状态为 上线
                if (!this.mergeTagService.checkUsingStatus(deleteTag.getId())) {
                    //删除组合标签引用
                    this.mergeTagService.removeMergeDetail(deleteTag.getId());
                    //删除自己
                    flag = this.removeById(deleteTag.getId());
                } else {
                    throw new IllegalStateException("有依赖该标签的组合标签处于运行状态，请先停止移除！");
                }
                break;
            case 4:
                //判断状态
                TbBasicTag tag = this.getById(deleteTag.getId());
                if (tag != null && ModelTaskState.convert(tag.getState()) == ModelTaskState.ONLINE) {
                    throw new IllegalStateException("依赖的模型标签处于运行状态，请先停止模型:" + tag.getName());
                }
                //删除子节点
                UpdateWrapper<TbBasicTag> wrapper = new UpdateWrapper<>();
                wrapper.eq("pid", deleteTag.getId());
                this.remove(wrapper);
                //删除元数据
                UpdateWrapper<TbTagMetadata> wrapper1 = new UpdateWrapper<>();
                wrapper1.eq("tag_id", deleteTag.getId());
                this.metadataService.remove(wrapper1);
                //删除模型数据
                UpdateWrapper<TbTagModel> wrapper2 = new UpdateWrapper<>();
                wrapper2.eq("tag_id", deleteTag.getId());
                this.modelService.remove(wrapper2);
                //删自己
                this.removeById(deleteTag.getId());
                flag = true;
                break;
            default:
                //判断是否有子节点
                int i = this.basicTagMapper.countChild(deleteTag.getId());
                if (i > 0) {
                    throw new IllegalStateException("当前节点非叶子节点!");
                }
                flag = this.removeById(deleteTag.getId());
                break;
        }
        return flag;
    }

    @Override
    @Transactional
    public boolean updatePrimaryTag(BasicTagDto basicTag) {
        TbBasicTag target = new TbBasicTag();
        target.setId(basicTag.getId());
        target.setName(basicTag.getName());
        target.setIndustry(basicTag.getIndustry());
        target.setPid(basicTag.getPid());
        target.setUpdateTime(LocalDateTime.now());
        //处理level
        if (basicTag.getPid() != null) {
            TbBasicTag parent = this.getById(basicTag.getPid());
            if (parent != null && parent.getLevel() != null) {
                target.setLevel(parent.getLevel() + 1);
            }
        }
        return this.updateById(target);
    }

    @Override
    @Transactional
    public boolean updateModelTagRule(BasicTagDto basicTag) {
        TbBasicTag target = new TbBasicTag();
        target.setId(basicTag.getId());
        target.setName(basicTag.getName());
        target.setRule(basicTag.getRule());
        target.setBusiness(basicTag.getBusiness());
        target.setUpdateTime(LocalDateTime.now());
        return this.updateById(target);
    }

    @Override
    @Transactional
    public boolean updateModelTag(ModelTagDto modelTag) {
        //0.解析元数据及校验合法性
        TbTagMetadata metadata = this.buildTagMetaData(modelTag.getRule());
        if (StringUtils.isEmpty(metadata.getOutFields())){
            throw new IllegalArgumentException(("outFields must not be null"));
        }
        //1.更新业务标签数据
        TbBasicTag target = new TbBasicTag();
        BeanUtils.copyProperties(modelTag, target);
        target.setUpdateTime(LocalDateTime.now());
        //设置输出字段名
        target.setHbaseFields(metadata.getOutFields());
        boolean tag = this.updateById(target);
        //2.更新模型数据
        TbTagModel tagModel = new TbTagModel();
        tagModel.setId(modelTag.getModelId());
        tagModel.setModelArgs(modelTag.getModelArgs());
        String modelPath = modelTag.getModelPath();
        String modelJar = modelPath.substring(modelPath.lastIndexOf("/") + 1);
        tagModel.setModelJar(modelJar);
        tagModel.setModelMain(modelTag.getModelMain());
        tagModel.setModelName(modelTag.getModelName());
        tagModel.setModelPath(modelPath);
        tagModel.setUpdateTime(LocalDateTime.now());
        //设置定时规则
        String rule = modelTag.getSchedule() + "," + StringUtils.join(modelTag.getStarEnd(), ",");
        tagModel.setScheduleRule(rule);
        //设置spark参数
        if (StringUtils.isEmpty(modelTag.getSparkOpts())) {
            tagModel.setSparkOpts(this.profileProperties.getOozie().getSparkOpts());
        } else {
            tagModel.setSparkOpts(modelTag.getSparkOpts());
        }
        boolean model = modelService.updateById(tagModel);
        //3.更新元数据
        metadata.setTagId(modelTag.getId());
        boolean metaData = this.metadataService.updateByTagId(metadata);
        return tag && model && metaData;
    }


}
