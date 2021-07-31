package org.tlh.rt.dw.entity

import java.util.Date

/**
  * 订单详情宽表
  *
  * @author 离歌笑
  * @desc
  * @date 2021-07-31
  */
class OrderWide(
                 val orderId: Long,
                 val user_id: Long, //用户ID
                 val province: Long, //省份ID
                 val city: Long, //城市ID
                 val country: Long, //乡镇ID
                 val actual_price: Double, // 实付金额 = order_price - integral_price
                 val order_price: Double, //订单金额 = goods_price + freight_price - coupon_price
                 val goods_price: Double, //商品总金额
                 val freight_price: Double, //配送费用
                 val coupon_price: Double, //优惠券减免
                 val integral_price: Double, //用户积分减免
                 val add_time: Date, //创建时间
                 val is_first_order: Boolean, //是否是首单
                 val province_name: String, //省份名称
                 val province_code: Int, //省份编号
                 val city_name: String, //城市名称
                 val city_code: Int, //城市编号
                 val country_code: Int, //乡镇编号
                 val country_name: String, //乡镇名称
                 val user_age_group: String, //用户年龄段
                 val user_gender: String, //用户性别

                 //订单详情信息
                 val orderDetailId: Int,
                 val goods_id: Int,
                 val goods_name: String,
                 val number: Int,
                 val price: Double,
                 val category_id: Int,
                 val category_name: String,
                 val brand_id: Int,
                 val brand_name: String,
                 var capitation_price: Double = 0 //该订单详情均摊金额
               ) extends Serializable{


  override def toString = s"OrderWide($orderId, $user_id, $province, $city, $country, $actual_price, $order_price, $goods_price, $freight_price, $coupon_price, $integral_price, $add_time, $is_first_order, $province_name, $province_code, $city_name, $city_code, $country_code, $country_name, $user_age_group, $user_gender, $orderDetailId, $goods_id, $goods_name, $number, $price, $category_id, $category_name, $brand_id, $brand_name, $capitation_price)"
}

object OrderWide {

  def apply(order: OrderInfo, orderDetail: OrderDetail): OrderWide = {
    new OrderWide(
      order.id,
      order.user_id,
      order.province,
      order.city,
      order.country,
      order.actual_price,
      order.order_price,
      order.goods_price,
      order.freight_price,
      order.coupon_price,
      order.integral_price,
      order.add_time,
      order.is_first_order,
      order.province_name,
      order.province_code,
      order.city_name,
      order.city_code,
      order.country_code,
      order.country_name,
      order.user_age_group,
      order.user_gender,
      orderDetail.id,
      orderDetail.goods_id,
      orderDetail.goods_name,
      orderDetail.number,
      orderDetail.price,
      orderDetail.category_id,
      orderDetail.category_name,
      orderDetail.brand_id,
      orderDetail.brand_name
    )
  }

}
