package org.tlh.dw.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
public class RandomNumString {

    public static final String getRandNumString(int fromNum, int toNum, int count, String delimiter, boolean canRepeat) {
        String numString = "";
        if (canRepeat) {
            ArrayList<Integer> numList = new ArrayList<>();
            while (numList.size() < count) {
                numList.add(Integer.valueOf(fromNum + (new Random()).nextInt(toNum - fromNum + 1)));
            }
            numString = StringUtils.join(numList, delimiter);
        } else {
            HashSet<Integer> numSet = new HashSet<>();
            if (count <= (toNum - fromNum + 1) / 2) {
                while (numSet.size() < count) {
                    numSet.add(Integer.valueOf(fromNum + (new Random()).nextInt(toNum - fromNum + 1)));
                }
            } else {
                HashSet<Integer> exNumSet = new HashSet<>();
                while (exNumSet.size() < toNum - fromNum + 1 - count) {
                    exNumSet.add(Integer.valueOf(fromNum + (new Random()).nextInt(toNum - fromNum + 1)));
                }

                for (int i = fromNum; i <= toNum; i++) {
                    if (!exNumSet.contains(Integer.valueOf(i))) {
                        numSet.add(Integer.valueOf(i));
                    }
                }
            }
            numString = StringUtils.join(numSet, delimiter);
        }
        return numString;
    }


    public static final String getRandNumString(int fromNum, int toNum, int count, String delimiter) {
        return getRandNumString(fromNum, toNum, count, delimiter, true);
    }

}
