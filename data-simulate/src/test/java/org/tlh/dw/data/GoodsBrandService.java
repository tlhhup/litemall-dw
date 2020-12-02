package org.tlh.dw.data;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.dao.LitemallBrandMapper;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallBrand;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-02
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsBrandService {

    @Autowired
    private LitemallGoodsMapper litemallGoodsMapper;

    @Autowired
    private LitemallBrandMapper brandMapper;

    @Test
    public void updateBrandId(){
        //获取所有品牌
        List<LitemallBrand> litemallBrands = this.brandMapper.selectByExample(null);
        List<Integer> collect = litemallBrands.stream().map(item -> item.getId()).collect(Collectors.toList());
        LitemallGoodsExample example=new LitemallGoodsExample();
        example.createCriteria().andBrandIdEqualTo(0);
        List<LitemallGoods> litemallGoods = this.litemallGoodsMapper.selectByExample(example);
        for (LitemallGoods litemallGood : litemallGoods) {
            litemallGood.setBrandId(collect.get(RandomUtils.nextInt(0,collect.size())));
            this.litemallGoodsMapper.updateByPrimaryKey(litemallGood);
        }
    }

}
