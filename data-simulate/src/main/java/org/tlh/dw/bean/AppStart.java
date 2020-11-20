package org.tlh.dw.bean;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
public class AppStart extends AppBase {

    private String action;//状态：成功=1 失败=2
    private String loadingTime;//加载时长：计算下拉开始到接口返回数据的时间，（开始加载报0，加载成功或加载失败才上报时间）
    private String detail;//失败码（没有则上报空）
    private String extend1;//失败的message（没有则上报空）
    private String en;//启动日志类型标记

}
