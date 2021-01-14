package org.tlh.dw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 商品收藏 TopN
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ads_product_favor_topN")
public class AdsProductFavorTopn implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 统计日期
     */
    private String dt;

    /**
     * 商品 ID
     */
    private Integer skuId;

    /**
     * 收藏量
     */
    private Long favorCount;

    // 商品 spu 信息
    private Integer spuId;
    private String spuName;


}
