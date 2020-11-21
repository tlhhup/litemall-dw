package org.tlh.dw.util;

import java.util.Random;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
public class RandomNum {

    public static final int getRandInt(int fromNum, int toNum) {
        return fromNum + (new Random()).nextInt(toNum - fromNum + 1);
    }

}
