package org.tlh.dw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 商品个数信息
 * </p>
 *
 * @author 离歌笑
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ads_product_sale_topN")
public class AdsProductSaleTopn implements Serializable {

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
     * 销量
     */
    private Long paymentCount;

    // 商品 spu 信息
    private Integer spuId;
    private String spuName;


}
