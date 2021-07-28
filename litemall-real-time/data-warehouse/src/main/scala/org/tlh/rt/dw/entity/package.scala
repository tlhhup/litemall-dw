package org.tlh.rt.dw

import java.util.Date

/**
  * @author 离歌笑
  * @desc
  * @date 2021-07-26
  */
package object entity {

  /**
    * 区域信息
    *
    */
  case class RegionInfo(
                         id: Long,
                         name: String, //名称
                         code: Int //编号
                       )

  /**
    * 用户信息
    */
  case class UserInfo(
                       id: Long,
                       username: String, //用户名
                       gender: Int, //性别
                       birthday: Date, //出生日期
                       last_login_time: Date, //最后一次登陆时间
                       last_login_ip: String, //最后一次登陆IP
                       user_level: Int, //用户等级
                       nickname: String, //昵称
                       mobile: String, //电话
                       avatar: String, //用户头像
                       weixin_openid: String, //微信ID
                       status: Int, //用户状态
                       add_time: Date, //创建时间
                       update_time: Date //更新时间
                     ) {

    def genderName(): String = {
      this.gender match {
        case 0 => "未知"
        case 1 => "男"
        case 2 => "女"
      }
    }

    def ageGroup(): String = {
      this.birthday match {
        case x: Date if x.before(new Date()) && x.after(new Date()) => "50后"
        case x: Date if x.before(new Date()) && x.after(new Date()) => "60后"
        case x: Date if x.before(new Date()) && x.after(new Date()) => "70后"
        case x: Date if x.before(new Date()) && x.after(new Date()) => "80后"
        case x: Date if x.before(new Date()) && x.after(new Date()) => "90后"
        case x: Date if x.before(new Date()) && x.after(new Date()) => "00后"
        case x: Date if x.before(new Date()) && x.after(new Date()) => "10后"
        case x: Date if x.before(new Date()) && x.after(new Date()) => "20后"
      }
    }

  }

  /**
    * 商品品牌
    *
    * @param id
    * @param name
    */
  case class GoodsBrand(id: Int, name: String)

  /**
    * 商品分类
    *
    * @param id
    * @param name
    */
  case class GoodsCategory(id: Int, name: String)

  /**
    * 商品信息
    *
    * @param id
    * @param name
    * @param category_id
    * @param brand_id
    */
  case class GoodsSku(id: Int,
                      name: String,
                      category_id: Int,
                      brand_id: Int,

                      var category_name: String,
                      var brand_name: String
                     )

}
