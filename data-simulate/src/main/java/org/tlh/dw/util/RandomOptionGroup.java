package org.tlh.dw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
public class RandomOptionGroup<T> {

    int totalWeight = 0;

    List<RanOpt> optList = new ArrayList();

    public RandomOptionGroup(String... values) {
        for (String value : values) {
            this.totalWeight++;
            this.optList.add(new RanOpt(value, 1));
        }
    }


    public RandomOptionGroup(RanOpt... opts) {
        for (RanOpt opt : opts) {
            this.totalWeight += opt.getWeight();
            for (int i = 0; i < opt.getWeight(); i++) {
                this.optList.add(opt);
            }
        }
    }


    public RandomOptionGroup(int trueWeight, int falseWeight) {
        this(new RanOpt[]{new RanOpt(Boolean.valueOf(true), trueWeight), new RanOpt(Boolean.valueOf(false), falseWeight)});
    }


    public RanOpt<T> getRandomOpt() {
        int i = (new Random()).nextInt(this.totalWeight);
        return (RanOpt) this.optList.get(i);
    }

    public String getRandStringValue() {
        int i = (new Random()).nextInt(this.totalWeight);
        return (String) this.optList.get(i).getValue();
    }

    public int getRandIntValue() {
        int i = (new Random()).nextInt(this.totalWeight);
        return (Integer) this.optList.get(i).getValue();
    }


    public Boolean getRandBoolValue() {
        int i = (new Random()).nextInt(this.totalWeight);
        return (Boolean) this.optList.get(i).getValue();
    }

}
