package org.tlh.warehouse.entity

import org.json4s.jackson.JsonMethods.parse
import org.junit.Test

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-22
  */
class OrderEntityTest {

  @Test
  def parseEntity(): Unit = {
    val json =
      """
        |{
        |  "ship_channel": null,
        |  "city": 110,
        |  "add_time": "2021-10-22 16:28:33",
        |  "refund_type": null,
        |  "actual_price": 1580.8,
        |  "refund_amount": null,
        |  "pay_id": null,
        |  "consignee": "贺霞",
        |  "confirm_time": null,
        |  "ship_time": null,
        |  "refund_content": null,
        |  "order_price": 1587.8,
        |  "coupon_price": 0,
        |  "end_time": null,
        |  "country": 1139,
        |  "ship_sn": null,
        |  "user_id": 12,
        |  "pay_time": null,
        |  "order_sn": "929726482276414",
        |  "id": 49,
        |  "aftersale_status": null,
        |  "order_status": 101,
        |  "refund_time": null,
        |  "province": 10,
        |  "address": "江苏省苏州市相城区第1大街第36号楼1单元544门",
        |  "message": "描述966737",
        |  "freight_price": 19,
        |  "goods_price": 1568.8,
        |  "mobile": "13599490037",
        |  "integral_price": 7,
        |  "update_time": null,
        |  "deleted": 0,
        |  "comments": 5,
        |  "groupon_price": 0
        |}
      """.stripMargin
    val order = parse(json).extract[Order]
  }

}
