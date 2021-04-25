package org.tlh.profile.service;

import org.tlh.profile.vo.EChartsGraphVo;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
public interface IUserTagSearchService {

    /**
     * 查询用户的标签
     *
     * @param id
     * @return
     */
    EChartsGraphVo searchUserTagById(int id);

}
