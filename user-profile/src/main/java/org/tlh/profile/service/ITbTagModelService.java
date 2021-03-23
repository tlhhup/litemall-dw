package org.tlh.profile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import org.tlh.profile.entity.TbTagModel;

/**
 * <p>
 * 标签模型 服务类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
public interface ITbTagModelService extends IService<TbTagModel> {

    /**
     * 上传文件
     * @param jar
     * @return
     */
    String uploadFile(MultipartFile jar);

}
