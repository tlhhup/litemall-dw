package org.tlh.dw.bean;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
public class AppBase {

    private String mid; // (String) 设备唯一标识
    private int uid; // (Integer) 用户uid
    private String vc; // (String) versionCode，程序版本号
    private String vn; // (String) versionName，程序版本名
    private String l; // (String) 系统语言
    private String sr; // (String) 渠道号，应用从哪个渠道来的。
    private String os; // (String) Android 系统版本
    private String ar; // (String) 区域
    private String md; // (String) 手机型号
    private String ba; // (String) 手机品牌
    private String sv; // (String) sdkVersion
    private String g; // (String) gmail
    private String hw; // (String) heightXwidth，屏幕宽高
    private String t; // (String) 客户端日志产生时的时间


}
