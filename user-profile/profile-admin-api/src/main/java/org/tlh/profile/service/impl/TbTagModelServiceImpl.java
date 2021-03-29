package org.tlh.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tlh.profile.config.ProfileProperties;
import org.tlh.profile.dto.ApproveModelDto;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.entity.TbTagModel;
import org.tlh.profile.enums.ModelTaskState;
import org.tlh.profile.enums.OozieScheduleType;
import org.tlh.profile.mapper.TbTagModelMapper;
import org.tlh.profile.service.ITbBasicTagService;
import org.tlh.profile.service.ITbTagModelService;
import org.tlh.profile.util.HDfsUtils;
import org.tlh.profile.vo.BasicTagListVo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签模型 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Slf4j
@Service
public class TbTagModelServiceImpl extends ServiceImpl<TbTagModelMapper, TbTagModel> implements ITbTagModelService {

    @Autowired
    private HDfsUtils hDfsUtils;

    @Autowired
    private ProfileProperties profileProperties;

    @Autowired
    private TbTagModelMapper modelMapper;

    @Autowired
    private ITbBasicTagService basicTagService;

    @Override
    public String uploadFile(MultipartFile jar) {
        try {
            StringBuilder builder = new StringBuilder(profileProperties.getHdfs().getModelPath());
            String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
            String targetFile = builder.append("/").append(date).append("/").append(jar.getOriginalFilename()).toString();
            if (this.hDfsUtils.uploadFile(jar.getInputStream(), targetFile)) {
                return targetFile;
            }
        } catch (IOException e) {
            log.error("upload file error", e);
        }
        return null;
    }

    @Override
    public List<BasicTagListVo> querySubmitModel(String modelName) {
        List<BasicTagListVo> result = this.modelMapper.querySubmitModel(modelName);
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
    public boolean approveModel(ApproveModelDto approveModel) {
        ModelTaskState taskState = ModelTaskState.convert(approveModel.getState());
        //1.修改标签状态
        TbBasicTag tag = new TbBasicTag();
        tag.setId(approveModel.getTagId());
        tag.setState(taskState.getState());
        tag.setUpdateTime(LocalDateTime.now());
        boolean c1 = this.basicTagService.updateById(tag);
        //2.修改模型状态
        TbTagModel tagModel = new TbTagModel();
        tagModel.setId(approveModel.getModelId());
        tagModel.setState(taskState.getState());
        tagModel.setUpdateTime(LocalDateTime.now());
        boolean c2 = this.updateById(tagModel);
        return c1 && c2;
    }

    @Override
    public boolean finishModelTag(long tagId) {
        //1.更新标签状态
        TbBasicTag tag = new TbBasicTag();
        tag.setId(tagId);
        tag.setState(ModelTaskState.DEVELOPED.getState());
        tag.setUpdateTime(LocalDateTime.now());
        boolean c1 = this.basicTagService.updateById(tag);
        //2.更新模型状态
        TbTagModel tagModel = new TbTagModel();
        tagModel.setState(ModelTaskState.DEVELOPED.getState());
        tagModel.setUpdateTime(LocalDateTime.now());
        UpdateWrapper<TbTagModel> wrapper = new UpdateWrapper<>();
        wrapper.eq("tag_id", tagId);
        boolean c2 = this.update(tagModel, wrapper);
        return c1 && c2;
    }

    @Override
    public boolean publishModel(long tagId, long modelId) {
        //1.提交任务到oozie todo
        //2.解析task id
        String taskId = "";
        //3.更新模型表，存储task id
        TbTagModel tagModel = new TbTagModel();
        tagModel.setId(modelId);
        tagModel.setState(ModelTaskState.ONLINE.getState());
        tagModel.setUpdateTime(LocalDateTime.now());
        tagModel.setOozieTaskId(taskId);
        boolean c1 = this.updateById(tagModel);
        //4.更新tag表状态
        TbBasicTag tag = new TbBasicTag();
        tag.setId(tagId);
        tag.setState(ModelTaskState.ONLINE.getState());
        tag.setUpdateTime(LocalDateTime.now());
        boolean c2 = this.basicTagService.updateById(tag);
        return c1 && c2;
    }

    @Override
    public boolean offlineModel(long tagId, long modelId) {
        //1.查询详细
        TbTagModel tagModel = this.getById(modelId);
        //2.校验状态
        if (tagModel == null
                || ModelTaskState.ONLINE == ModelTaskState.convert(tagModel.getState())
                || StringUtils.isEmpty(tagModel.getOozieTaskId())) {
            throw new IllegalStateException("模型状态错误！");
        }
        //3.删除oozie中的任务 todo

        //4.更新状态
        tagModel.setState(ModelTaskState.OFFLINE.getState());
        tagModel.setUpdateTime(LocalDateTime.now());
        boolean c1 = this.updateById(tagModel);

        TbBasicTag tag = new TbBasicTag();
        tag.setId(tagId);
        tag.setState(ModelTaskState.OFFLINE.getState());
        tag.setUpdateTime(LocalDateTime.now());
        boolean c2 = this.basicTagService.updateById(tag);
        return c1 && c2;
    }

    @Override
    public boolean runOrStopModel(long tagId, long modelId, ModelTaskState taskState) {
        //1.查询详细
        TbTagModel tagModel = this.getById(modelId);
        //2.校验状态
        if (tagModel == null
                || ModelTaskState.ONLINE == ModelTaskState.convert(tagModel.getState())
                || StringUtils.isEmpty(tagModel.getOozieTaskId())) {
            throw new IllegalStateException("模型状态错误！");
        }
        //3.切换oozie中的任务 todo
        switch (taskState) {
            case ONLINE:
                break;
            case STOPPED:
                break;
            default:
                throw new IllegalArgumentException("不支持的操作！");
        }

        //4.更新状态
        tagModel.setState(taskState.getState());
        tagModel.setUpdateTime(LocalDateTime.now());
        boolean c1 = this.updateById(tagModel);

        TbBasicTag tag = new TbBasicTag();
        tag.setId(tagId);
        tag.setState(taskState.getState());
        tag.setUpdateTime(LocalDateTime.now());
        boolean c2 = this.basicTagService.updateById(tag);
        return c1 && c2;
    }
}
