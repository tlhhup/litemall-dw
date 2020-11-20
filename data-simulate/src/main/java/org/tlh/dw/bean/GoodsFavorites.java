package org.tlh.dw.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsFavorites {

    private int id;//主键
    private int courseId;//商品 id
    private int userId;//用户 ID
    private Date addTime;//创建时间

}
