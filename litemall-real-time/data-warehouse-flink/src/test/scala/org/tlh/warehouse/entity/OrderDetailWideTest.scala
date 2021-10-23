package org.tlh.warehouse.entity

import org.json4s.jackson.Serialization.read
import org.junit.Test

/**
  * @author 离歌笑
  * @desc
  * @date 2021-10-23
  */
class OrderDetailWideTest {

  @Test
  def parseEntity(): Unit = {
    val json =
      """
        |{
        |  "id": 14,
        |  "order_id": 3,
        |  "goods_id": 1064000,
        |  "goods_name": "清新条纹开放式宠物窝",
        |  "goods_sn": "1064000",
        |  "product_id": "72",
        |  "number": 2,
        |  "price": 79,
        |  "specifications": "[\"标准\"]",
        |  "pic_url": "http://yanxuan.nosdn.127.net/ebe118f94ddafe82c4a8cd51da6ff183.png",
        |  "add_time": "2021-10-22 16:28:06",
        |  "brand_id": 1015000,
        |  "brand_name": "NITORI制造商",
        |  "first_category_id": 1017000,
        |  "first_category_name": "宠物",
        |  "second_category_id": 1005000,
        |  "second_category_name": "居家"
        |}
      """.stripMargin
    val orderDetailWide = read[OrderDetailWide](json)
  }

}
