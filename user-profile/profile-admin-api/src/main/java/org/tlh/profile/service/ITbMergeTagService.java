package org.tlh.profile.service;

import org.tlh.profile.dto.MergeTagDto;
import org.tlh.profile.entity.TbMergeTag;
import com.baomidou.mybatisplus.extension.service.IService;
import org.tlh.profile.vo.MergeTagListVo;

import java.util.List;

/**
 * <p>
 * 组合标签 服务类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
public interface ITbMergeTagService extends IService<TbMergeTag> {

    /**
     * 创建组合标签
     *
     * @param mergeTag
     * @return
     */
    boolean createMergeTag(MergeTagDto mergeTag);

    /**
     * 查询列表
     *
     * @param name
     * @return
     */
    List<MergeTagListVo> queryTags(String name);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean removeMergeTag(long id);

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    MergeTagListVo getMergeTagDetail(long id);

    /**
     * 更新组合标签
     *
     * @param mergeTag
     * @return
     */
    boolean updateMergeTag(MergeTagListVo mergeTag);

    /**
     * 检查使用有上线的组合标签在使用该基础标签
     *
     * @param basicTagId
     * @return
     */
    boolean checkUsingStatus(long basicTagId);

    /**
     * 删除组合标签详情
     * @param basicTagId
     */
    void removeMergeDetail(long basicTagId);
}
