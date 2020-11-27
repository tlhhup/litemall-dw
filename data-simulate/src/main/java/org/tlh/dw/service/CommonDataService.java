package org.tlh.dw.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.dao.*;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.tlh.dw.bean.RegionInfo;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    private List<LitemallCoupon> litemallCoupons;
    private List<LitemallGoods> litemallGoods;
    private List<RegionInfo> regions;

    private Random random;

    @Autowired
    private LitemallUserMapper userMapper;

    @Autowired
    private LitemallGoodsMapper goodsMapper;

    @Autowired
    private LitemallTopicMapper topicMapper;

    @Autowired
    private LitemallCouponMapper couponMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public CommonDataService() {
        this.userId = new CopyOnWriteArrayList<>();
        this.goodsId = new CopyOnWriteArrayList<>();
        this.litemallGoods = new CopyOnWriteArrayList<>();
        this.topicId = new CopyOnWriteArrayList<>();
        this.litemallCoupons = new CopyOnWriteArrayList<>();
        this.random = new Random();
    }

    @PostConstruct
    public void init() {
        //初始化用户id
        List<LitemallUser> users = this.userMapper.selectByExample(null);
        if (!ObjectUtils.isEmpty(users)) {
            this.userId.addAll(users.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
        //初始化区域数据
        initRegion();
        //加载数据
        reloadCommonData();
    }

    private void initRegion() {
        String sql = "select\n" +
                "\tprovince.id as pId,\n" +
                "\tprovince.name as pName,\n" +
                "\tcity.id as cId,\n" +
                "\tcity.name as cName,\n" +
                "\tcountry.id as tId,\n" +
                "\tcountry.name as tName,\n" +
                "\tcountry.code as code\n" +
                "from\n" +
                "(\n" +
                "\tselect\n" +
                "\t\tid,\n" +
                "\t\tname\n" +
                "\tfrom litemall_region\n" +
                "\twhere type=1\n" +
                ") as province\n" +
                "join \n" +
                "(\n" +
                "\tselect\n" +
                "\t\tpid,\n" +
                "\t\tid,\n" +
                "\t\tname\n" +
                "\tfrom litemall_region\n" +
                "\twhere type=2\n" +
                ") as city\n" +
                "on province.id=city.pid\n" +
                "join \n" +
                "(\n" +
                "\tselect\n" +
                "\t\tpid,\n" +
                "\t\tid,\n" +
                "\t\tname,\n" +
                "\t\tcode\n" +
                "\tfrom litemall_region\n" +
                "\twhere type=3\n" +
                ") as country\n" +
                "on city.id=country.pid";
        this.regions = this.jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RegionInfo.class));
    }

    public void reloadCommonData() {
        //1.清空数据
        this.litemallGoods.clear();
        this.goodsId.clear();
        this.topicId.clear();
        this.litemallCoupons.clear();

        //2.重新加载
        //初始化商品id
        List<LitemallGoods> goods = this.goodsMapper.selectByExample(null);
        if (!ObjectUtils.isEmpty(goods)) {
            this.goodsId.addAll(goods.stream().map(item -> item.getId()).collect(Collectors.toList()));
            this.litemallGoods.addAll(goods);
        }
        //初始化topicId
        List<LitemallTopic> topics = this.topicMapper.selectByExample(null);
        if (!ObjectUtils.isEmpty(topics)) {
            this.topicId.addAll(topics.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
        //初始化卷数据
        LitemallCouponExample example = new LitemallCouponExample();
        example.createCriteria().andIdNotEqualTo(3);//非新用户卷
        List<LitemallCoupon> litemallCoupons = this.couponMapper.selectByExample(example);
        if (!ObjectUtils.isEmpty(litemallCoupons)) {
            this.litemallCoupons.addAll(litemallCoupons);
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

    public List<Integer> getGoodsId() {
        return goodsId;
    }

    public LitemallGoods randomGoods() {
        int index = random.nextInt(this.litemallGoods.size());
        return this.litemallGoods.get(index);
    }

    public int randomTopicId() {
        int index = random.nextInt(this.topicId.size());
        return this.topicId.get(index);
    }

    public List<Integer> getTopicId() {
        return topicId;
    }

    public RegionInfo randomRegion() {
        int index = random.nextInt(this.regions.size());
        return this.regions.get(index);
    }

    public void updateUserId(int userId) {
        this.userId.add(userId);
    }

    public LitemallCoupon randomCouPonId() {
        int index = random.nextInt(this.litemallCoupons.size());
        return this.litemallCoupons.get(index);
    }

}
