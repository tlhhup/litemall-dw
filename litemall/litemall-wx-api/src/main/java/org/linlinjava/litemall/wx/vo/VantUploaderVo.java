package org.linlinjava.litemall.wx.vo;

import java.io.Serializable;

/**
 * Vant 上传组建数据
 *
 * @author 离歌笑
 * @desc
 * @date 2021-01-08
 */
public class VantUploaderVo implements Serializable {

    private String content;//图片编码后的base64
    private String url;// 图片地址

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
