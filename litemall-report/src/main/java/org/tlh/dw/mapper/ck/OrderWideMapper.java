package org.tlh.dw.mapper.ck;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.tlh.dw.entity.ck.OrderWide;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-08-31
 */
@DS("clickhouse")
public interface OrderWideMapper extends BaseMapper<OrderWide> {
}
