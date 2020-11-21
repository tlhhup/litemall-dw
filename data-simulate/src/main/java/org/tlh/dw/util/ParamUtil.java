package org.tlh.dw.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
public class ParamUtil {

    public static final Integer checkRatioNum(Integer rateNum) {
        try {
            if (rateNum.intValue() < 0 || rateNum.intValue() > 100) {
                throw new RuntimeException("输入的比率必须为0-100的数字");
            }
            return rateNum;
        } catch (Exception e) {
            throw new RuntimeException("输入的比率必须为0-100的数字");
        }
    }


    public static final Date checkDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return simpleDateFormat.parse(dateString);
        }
        catch (ParseException e) {
            throw new RuntimeException("必须为日期型格式 例如： 2020-02-02");
        }
    }

    public static final Boolean checkBoolean(String bool) {
        if (bool.equals("1") || bool.equals("true"))
            return Boolean.valueOf(true);
        if (bool.equals("0") || bool.equals("false")) {
            return Boolean.valueOf(false);
        }
        throw new RuntimeException("是非型参数请填写：1或0，true或false");
    }


    public static final Integer[] checkRate(String rateString, int rateCount) {
        try {
            String[] rateArray = rateString.split(":");
            if (rateArray.length != rateCount) {
                throw new RuntimeException("请按比例个数补足 ");
            }
            Integer[] rateNumArr = new Integer[rateArray.length];
            for (int i = 0; i < rateArray.length; i++) {
                Integer rate = checkRatioNum(Integer.parseInt(rateArray[i]));
                rateNumArr[i] = rate;
            }
            return rateNumArr;
        } catch (Exception e) {
            throw new RuntimeException("请按比例填写，如：75:10:15");
        }
    }



    public static final Integer checkCount(String count) {
        try {
            if (count == null) {
                return Integer.valueOf(0);
            }
            return Integer.valueOf(count);
        }
        catch (Exception e) {
            throw new RuntimeException("输入的必须为数字");
        }
    }

}
