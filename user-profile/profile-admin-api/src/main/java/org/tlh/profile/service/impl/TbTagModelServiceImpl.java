package org.tlh.profile.service.impl;

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
    public List<BasicTagListVo> querySubmitModel() {
        List<BasicTagListVo> result = this.modelMapper.querySubmitModel();
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
}
