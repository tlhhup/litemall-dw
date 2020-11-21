package org.tlh.dw.bean;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-21
 */
@Data
public class RegionInfo {

    private String pId;//省份ID
    private String pName;
    private String cId;//城市ID
    private String cName;
    private String tId;//区县id
    private String tName;
    private String code;//邮编

}
