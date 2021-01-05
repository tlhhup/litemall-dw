package org.tlh.dw.vo;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-05
 */
@Data
public class RealTimeVo {

    private long orderCount;
    private double orderAmount;
    private long payCount;
    private double payAmount;

}
