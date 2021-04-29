package org.tlh.tag.test

/**
  * @author 离歌笑
  * @desc
  * @date 2021-04-29
  */
object RegTest {

  def main(args: Array[String]): Unit = {
    val rule = "0.4~1"
    val reg = "^([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[1-9]\\d*)?([~|>|<|=]*)([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[1-9]\\d*|0)$".r

    val reg(start, symble, end) = rule

    println(s"$start,$end,$symble")
  }

}
