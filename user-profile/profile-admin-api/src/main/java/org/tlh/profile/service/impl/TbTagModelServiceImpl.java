package org.tlh.profile.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tlh.profile.config.ProfileProperties;
import org.tlh.profile.entity.TbTagModel;
import org.tlh.profile.mapper.TbTagModelMapper;
import org.tlh.profile.service.ITbTagModelService;
import org.tlh.profile.util.HDfsUtils;

import java.io.IOException;
import java.util.Date;

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
}
