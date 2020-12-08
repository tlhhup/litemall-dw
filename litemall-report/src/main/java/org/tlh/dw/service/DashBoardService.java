package org.tlh.dw.service;

import org.tlh.dw.vo.DashBoardHeader;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-08
 */
public interface DashBoardService {

    /**
     * 查询数据总汇
     * @param date
     * @param type
     * @return
     */
    DashBoardHeader queryByDate(String date,int type);

}
