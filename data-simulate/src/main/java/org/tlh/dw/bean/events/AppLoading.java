package org.tlh.dw.bean.events;

import lombok.Data;

/**
 * 商品列表
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
public class AppLoading {

    private String action;//动作：开始加载=1，加载成功=2，加载失败=3
    private String loadingTime;//加载时长：计算下拉开始到接口返回数据的时间，（开始加载报0，加载成功或加载失败才上报时间）
    private String loadingWay;//加载类型：1-读取缓存，2-从接口拉新数据（加载成功才上报加载类型）
    private String extend1;//扩展字段Extend1
    private String extend2;//扩展字段Extend2
    private String type;//加载类型：自动加载=1，用户下拽加载=2，底部加载=3（底部条触发点击底部提示条/点击返回顶部加载）
    private String type1;//加载失败码：把加载失败状态码报回来（报空为加载成功，没有失败）

}
