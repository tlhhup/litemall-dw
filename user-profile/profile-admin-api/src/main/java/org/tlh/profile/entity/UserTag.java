package org.tlh.profile.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
@Data
@SolrDocument(collection = "litemall")
public class UserTag {

    @Id
    private int id;

    private String gender;
    private String age;
    private String consumptionAbility;
    private String consumption;
    private String coupon;
    private String discountRate;
    private String singleOrderMax;
    private String orderFrequency;
    private String pct;
    private String refundRate;
    private String rfm;
    private String brandFavor;

}
