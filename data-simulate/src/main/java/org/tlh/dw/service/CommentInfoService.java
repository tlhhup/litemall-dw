package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallCommentMapper;
import org.linlinjava.litemall.db.domain.LitemallComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.dw.config.SimulateProperty;
import org.tlh.dw.util.ParamUtil;
import org.tlh.dw.util.RanOpt;
import org.tlh.dw.util.RandomNumString;
import org.tlh.dw.util.RandomOptionGroup;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 评论
 * 周期性事务
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class CommentInfoService {

    @Autowired
    private SimulateProperty simulateProperty;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private LitemallCommentMapper commentMapper;


    public void genComments() {
        //商品评论
        saveComment(0, this.commonDataService.getGoodsId());
        //主题评论
        saveComment(1, this.commonDataService.getTopicId());
    }

    private void saveComment(int type, List<Integer> valueIds) {
        List<Integer> appraiseRate = this.simulateProperty.getComment().getAppraiseRate();

        Date date = ParamUtil.checkDate(this.simulateProperty.getDate());
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Random random = new Random();

        RandomOptionGroup<Integer> appraiseOptionGroup = new RandomOptionGroup<>(new RanOpt[]{
                new RanOpt(5, appraiseRate.get(0)),
                new RanOpt(3, appraiseRate.get(1)),
                new RanOpt(0, appraiseRate.get(2)),
                new RanOpt(1, appraiseRate.get(3))});

        for (int valueId : valueIds) {
            int userId = this.commonDataService.randomUserId();
            LitemallComment comment = init(userId, valueId, type, localDateTime);
            //设置评价
            int star = appraiseOptionGroup.getRandIntValue();
            star = star != 1 ? star : random.nextInt(5);
            comment.setStar((short) star);
            this.commentMapper.insert(comment);
        }
        log.info("总共生成{}评价{}条", type == 1 ? "主题" : "商品", valueIds.size());
    }

    private LitemallComment init(int userId, int valueId, int type, LocalDateTime dateTime) {
        LitemallComment litemallComment = new LitemallComment();
        litemallComment.setUserId(userId);
        litemallComment.setValueId(valueId);
        litemallComment.setType((byte) type);
        litemallComment.setDeleted(false);
        litemallComment.setAddTime(dateTime);
        litemallComment.setContent("评论内容" + RandomNumString.getRandNumString(1, 9, 50, ""));

        return litemallComment;
    }

}
