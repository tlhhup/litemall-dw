package org.tlh.dw.service;

import org.tlh.dw.vo.EchartBarVo;

import java.util.List;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-04
 */
public interface ReportBoardService {

    /**
     * 用户行为转换
     * @param date
     * @return
     */
    List<Map<String, Object>> uaConvert(String date);


    /**
     * 销量排行
     * @param date
     * @return
     */
    List<EchartBarVo> saleTopN(String date);

}
