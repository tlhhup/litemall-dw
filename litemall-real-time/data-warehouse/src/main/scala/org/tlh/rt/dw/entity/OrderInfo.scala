package org.tlh.rt.dw.entity

import java.util.Date

/**
  * 订单信息表
  *
  * @author 离歌笑
  * @desc
  * @date 2021-07-26
  */
case class OrderInfo(
                      id: Long, //订单ID
                      user_id: Long, //用户ID
                      province: Long, //省份ID
                      city: Long, //城市ID
                      country: Long, //乡镇ID
                      actual_price: Double, // 实付金额 = order_price - integral_price
                      order_price: Double, //订单金额 = goods_price + freight_price - coupon_price
                      goods_price: Double, //商品总金额
                      freight_price: Double, //配送费用
                      coupon_price: Double, //优惠券减免
                      integral_price: Double, //用户积分减免
                      add_time: Date, //创建时间
                      update_time: Date, //更新时间

                      var is_first_order: Boolean, //是否是首单

                      var province_name: String, //省份名称
                      var province_code: String, //省份编号
                      var city_name: String, //城市名称
                      var city_code: String, //城市编号
                      var country_name: String, //乡镇名称
                      var country_code: String, //乡镇编号

                      var user_age_group: String, //用户年龄段
                      var user_gender: String //用户性别
                    )
