package org.tlh.rt.dw.entity
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods.{mapper, parse}
import org.tlh.rt.dw.utils.DwSerializers

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-28
  */
object OrderInfoTest {

  def main(args: Array[String]): Unit = {
    val json=
      """
        |{
        |  "ship_channel": null,
        |  "city": 260,
        |  "add_time": "2021-07-28 17:18:56",
        |  "refund_type": null,
        |  "actual_price": 18141,
        |  "refund_amount": null,
        |  "pay_id": null,
        |  "consignee": "危　江朵",
        |  "confirm_time": null,
        |  "ship_time": null,
        |  "refund_content": null,
        |  "order_price": 18143,
        |  "coupon_price": 0,
        |  "end_time": null,
        |  "country": 2307,
        |  "ship_sn": null,
        |  "user_id": 647,
        |  "pay_time": null,
        |  "order_sn": "643554185362176",
        |  "id": 91062,
        |  "aftersale_status": null,
        |  "order_status": 101,
        |  "refund_time": null,
        |  "province": 20,
        |  "address": "广西壮族自治区贺州市富川瑶族自治县第5大街第17号楼1单元665门",
        |  "message": "描述184734",
        |  "freight_price": 10,
        |  "goods_price": 18133,
        |  "mobile": "13503177645",
        |  "integral_price": 2,
        |  "update_time": null,
        |  "deleted": 0,
        |  "comments": 13,
        |  "groupon_price": 0
        |}
      """.stripMargin

    implicit val formats: Formats = DefaultFormats ++ DwSerializers.all

    val orderInfo = parse(json).extract[OrderInfo]

    println(orderInfo)
  }

}
