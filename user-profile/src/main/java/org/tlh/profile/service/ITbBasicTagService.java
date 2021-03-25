package org.tlh.profile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.tlh.profile.dto.BasicTagDto;
import org.tlh.profile.dto.DeleteTagDto;
import org.tlh.profile.dto.ModelTagDto;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.vo.BasicTagListVo;
import org.tlh.profile.vo.ElementTreeVo;

import java.util.List;

/**
 * <p>
 * 基础标签 服务类
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
public interface ITbBasicTagService extends IService<TbBasicTag> {

    /**
     * 创建主分类标签(1,2,3级标签)
     *
     * @param basicTag
     * @return
     */
    boolean createPrimaryTag(BasicTagDto basicTag);

    /**
     * 查询一级和对应的二级标签
     *
     * @return
     */
    List<ElementTreeVo> queryPrimaryTree();

    /**
     * 左边的tree
     *
     * @return
     */
    List<ElementTreeVo> leftTree();

    /**
     * 查询
     *
     * @param pid
     * @return
     */
    List<BasicTagListVo> childTags(Long pid);

    /**
     * 通过标签名查询
     *
     * @param name
     * @return
     */
    List<BasicTagDto> queryByTagName(String name);

    /**
     * 添加业务标签
     *
     * @param modelTag
     * @return
     */
    boolean createModelTag(ModelTagDto modelTag);

    /**
     * 添加业务标签值域
     *
     * @param basicTag
     * @return
     */
    boolean saveModelRule(BasicTagDto basicTag);

    /**
     * 删除标签
     * @param deleteTag
     * @return
     */
    boolean deleteTag(DeleteTagDto deleteTag);
}
