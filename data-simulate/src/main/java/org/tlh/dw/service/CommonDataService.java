package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.dao.LitemallTopicMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallTopic;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 公共数据
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
@Slf4j
@Service
public class CommonDataService {

    private List<Integer> userId;
    private List<Integer> goodsId;
    private List<Integer> topicId;

    private Random random;

    @Autowired
    private LitemallUserMapper userMapper;

    @Autowired
    private LitemallGoodsMapper goodsMapper;

    @Autowired
    private LitemallTopicMapper topicMapper;

    public CommonDataService() {
        this.userId = new CopyOnWriteArrayList<>();
        this.goodsId = new CopyOnWriteArrayList<>();
        this.topicId = new CopyOnWriteArrayList<>();
        this.random = new Random();
    }

    @PostConstruct
    public void init() {
        //初始化用户id
        List<LitemallUser> users = this.userMapper.selectByExample(null);
        if (!ObjectUtils.isEmpty(users)) {
            this.userId.addAll(users.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
        //初始化商品id
        List<LitemallGoods> goods = this.goodsMapper.selectByExample(null);
        if (!ObjectUtils.isEmpty(goods)) {
            this.goodsId.addAll(goods.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
        //初始化topicId
        List<LitemallTopic> topics = this.topicMapper.selectByExample(null);
        if (!ObjectUtils.isEmpty(topics)) {
            this.topicId.addAll(topics.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
    }

    public int randomUserId() {
        int index = random.nextInt(this.userId.size());
        return this.userId.get(index);
    }

    public Set<Integer> randomUserId(int weight) {
        int size = this.userId.size();
        int fetchSize = size * weight / 100;
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < fetchSize; i++) {
            int index = new Random().nextInt(size);
            result.add(this.userId.get(index));
        }
        return result;
    }

    public int randomGoodId() {
        int index = random.nextInt(this.goodsId.size());
        return this.goodsId.get(index);
    }

    public int randomTopicId() {
        int index = random.nextInt(this.topicId.size());
        return this.topicId.get(index);
    }

    public void updateUserId(int userId) {
        this.userId.add(userId);
    }

    public void updateGoodId(int goodsId) {
        this.goodsId.add(goodsId);
    }

    public void updateTopicId(int topicId) {
        this.topicId.add(topicId);
    }

}
