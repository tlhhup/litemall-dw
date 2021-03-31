package org.tlh.profile.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.junit.Test;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-31
 */
public class PinyinUtilTest {

    @Test
    public void pingYin() throws Exception{
        //1.创建一个格式化输出对象
        HanyuPinyinOutputFormat outputF = new HanyuPinyinOutputFormat();
        //2.设置好格式
        outputF.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputF.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        String s = PinyinHelper.toHanYuPinyinString("你大爷", outputF, "", true);
        System.out.println(s);
    }


}
