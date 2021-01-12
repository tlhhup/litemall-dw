package org.tlh.dw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 离线计算 评论数据
 *
 * @author 离歌笑
 * @desc
 * @date 2021-01-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private int valueId;
    private int productId;

}
