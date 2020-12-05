package org.tlh.dw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.tlh.dw.entity.AdsAppraiseBadTopn;
import org.tlh.dw.mapper.AdsAppraiseBadTopnMapper;
import org.tlh.dw.service.IAdsAppraiseBadTopnService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品差评率 TopN 服务实现类
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Service
public class AdsAppraiseBadTopnServiceImpl extends ServiceImpl<AdsAppraiseBadTopnMapper, AdsAppraiseBadTopn> implements IAdsAppraiseBadTopnService {

    @Override
    public IPage<AdsAppraiseBadTopn> queryByDate(Page<AdsAppraiseBadTopn> page,String date) {
        //1.校验数据
        if (StringUtils.isEmpty(date)) {
            //默认查询T+1的数据
            Date yesterday = DateUtils.addDays(new Date(), -1);
            date = DateFormatUtils.format(yesterday, "yyyy-MM-dd");
        }
        //2.查询数据
        QueryWrapper wrapper = new QueryWrapper<AdsAppraiseBadTopn>().eq("dt", date);
        IPage<AdsAppraiseBadTopn> result = this.baseMapper.selectPage(page, wrapper);
        return result;
    }
}
