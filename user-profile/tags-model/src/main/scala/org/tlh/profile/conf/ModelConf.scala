package org.tlh.profile.conf

import com.typesafe.config.ConfigFactory

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-29
  */
object ModelConf {

  private[this] val config = ConfigFactory.load("application.conf")

  val MODEL_DIR = config.getString("model.dir")
  val WRITE_OVER = config.getBoolean("model.writeover")

}
