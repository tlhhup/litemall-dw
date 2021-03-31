package org.tlh.profile.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-31
 */
public final class PinyinUtil {

    private static HanyuPinyinOutputFormat outputF;

    static {
        //1.创建一个格式化输出对象
        outputF = new HanyuPinyinOutputFormat();
        //2.设置好格式
        outputF.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputF.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    }

    private PinyinUtil() {

    }

    public static String toPingYin(String source) {
        try {
            String result = PinyinHelper.toHanYuPinyinString(source, outputF, "", true);
            return result;
        } catch (BadHanyuPinyinOutputFormatCombination e) {

        }
        return source;
    }

}
