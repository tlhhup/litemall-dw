package org.tlh.dw.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionOrderVo {

    private String name;
    private long orderCount;
    private double orderAmount;

}
