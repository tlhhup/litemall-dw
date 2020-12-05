package org.tlh.dw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.tlh.dw.entity.AdsAppraiseBadTopn;

/**
 * <p>
 * 商品差评率 TopN 服务类
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
public interface IAdsAppraiseBadTopnService extends IService<AdsAppraiseBadTopn> {

    /**
     * 查询指定日期的商品差评数据
     *
     * @param page
     * @param date
     * @return
     */
    IPage<AdsAppraiseBadTopn> queryByDate(Page<AdsAppraiseBadTopn> page, String date);
}
