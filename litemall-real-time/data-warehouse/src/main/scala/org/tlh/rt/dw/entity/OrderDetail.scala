package org.tlh.rt.dw.entity

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-30
  */
case class OrderDetail(
                        id: Int,
                        order_id: Int,
                        goods_id: Int,
                        goods_name: String,
                        number: Int,
                        price: Double,

                        var category_id: Int = 0,
                        var category_name: String = "",
                        var brand_id: Int = 0,
                        var brand_name: String = ""
                      )
