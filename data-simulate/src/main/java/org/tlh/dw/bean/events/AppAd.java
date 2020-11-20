package org.tlh.dw.bean.events;

import lombok.Data;

/**
 * 广告
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
public class AppAd {

    private String entry;//入口：商品列表页=1 应用首页=2 商品详情页=3
    private String action;//动作： 广告展示=1 广告点击=2
    private String contentType;//Type: 1 商品2 营销活动
    private String displayMills;//展示时长毫秒数
    private int itemId; //商品id
    private String activityId; //营销活动id

}
