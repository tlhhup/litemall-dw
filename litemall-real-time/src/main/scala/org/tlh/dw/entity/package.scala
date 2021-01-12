package org.tlh.dw

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
package object entity {

  // 用户
  val USER_LOGIN_COUNT = "user:login:count:"
  val USER_REGISTER_COUNT = ":user:register:count"
  val USER_ORDER_COUNT = "user:order:count:"
  val USER_ORDER_AMOUNT = "user:order:amount:"
  val USER_PAY_COUNT = "user:pay:count:"
  val USER_PAY_AMOUNT = "user:pay:amount:"
  val USER_REFUND_COUNT = "user:refund:count:"
  val USER_REFUND_AMOUNT = "user:refund:amount:"

  //订单
  val ORDER_SPEED = "order:speed:"
  val ORDER_COUNT = ":order:count"
  val ORDER_AMOUNT = ":order:amount"
  val PAY_COUNT = ":pay:count"
  val PAY_AMOUNT = ":pay:amount"
  val REFUND_COUNT = ":refund:count"
  val REFUND_AMOUNT = ":refund:amount"
  val CONFIRM_COUNT = ":confirm:count"
  val CONFIRM_AMOUNT = ":confirm:amount"

  //商品
  val GOODS_CART = ":goods:cart"
  val GOODS_COLLECT = ":goods:collect"
  val GOODS_ORDER = ":goods:order"
  val GOODS_COMMENT_GOOD = ":goods:comment:good"
  val GOODS_COMMENT_BAD = ":goods:comment:bad"

}