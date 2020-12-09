package org.tlh.dw.service;

import org.tlh.dw.vo.DashBoardHeader;

import java.util.List;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
public interface DashBoardService {

    /**
     * 查询数据总汇
     *
     * @param date
     * @param type
     * @return
     */
    DashBoardHeader queryByDate(String date, int type);

    /**
     * 查询指定时间间隔的：新增用户、订单量、支付量、退款量
     *
     * @param duration
     * @return
     */
    List<Map> queryByDuration(int duration);
}
