package org.tlh.dw.process

import org.tlh.dw.entity.OriginalData

/**
  * @author 离歌笑
  * @desc
  * @date 2020-12-16
  */
trait AbstractProcess {

  def process(item: OriginalData): Unit

}
