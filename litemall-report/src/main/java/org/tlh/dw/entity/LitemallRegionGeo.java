package org.tlh.dw.entity;

import lombok.Data;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-15
 */
@Data
public class LitemallRegionGeo {

    private int regionId;
    private String geo;
    private String countryName;
    private String cityName;
    private String provinceName;

}
