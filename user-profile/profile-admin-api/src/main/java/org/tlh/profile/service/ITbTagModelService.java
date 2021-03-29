package org.tlh.profile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import org.tlh.profile.dto.ApproveModelDto;
import org.tlh.profile.entity.TbTagModel;
import org.tlh.profile.enums.ModelTaskState;
import org.tlh.profile.vo.BasicTagListVo;

import java.util.List;

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
     *
     * @param jar
     * @return
     */
    String uploadFile(MultipartFile jar);

    /**
     * 查询待审核的模型
     *
     * @return
     */
    List<BasicTagListVo> querySubmitModel(String modelName);

    /**
     * 模型审批
     *
     * @param approveModel
     * @return
     */
    boolean approveModel(ApproveModelDto approveModel);

    /**
     * 完成模型开发
     *
     * @param tagId
     * @return
     */
    boolean finishModelTag(long tagId);

    /**
     * 发布模型
     *
     * @param tagId
     * @param modelId
     * @return
     */
    boolean publishModel(long tagId, long modelId);

    /**
     * 模型下线
     *
     * @param tagId
     * @param modelId
     * @return
     */
    boolean offlineModel(long tagId, long modelId);

    /**
     * 暂停和启动模型
     *
     * @param tagId
     * @param modelId
     * @param taskState
     * @return
     */
    boolean runOrStopModel(long tagId, long modelId, ModelTaskState taskState);
}
