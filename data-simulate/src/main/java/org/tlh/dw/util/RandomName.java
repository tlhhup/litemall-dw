package org.tlh.dw.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-20
 */
public class RandomName {

    private static final String[] SURNAME = {"赵", "钱", "孙", "李　周", "吴", "郑", "王　冯", "陈", "褚", "卫　蒋", "沈", "韩", "杨",
            "朱", "秦", "尤", "许　何", "吕", "施", "张　孔", "曹", "严", "华　金", "魏", "陶", "姜",
            "戚", "谢", "邹", "喻　柏", "水", "窦", "章　云", "苏", "潘", "葛　奚", "范", "彭", "郎",
            "鲁", "韦", "昌", "马　苗", "凤", "花", "方　俞", "任", "袁", "柳　酆", "鲍", "史", "唐",
            "费", "廉", "岑", "薛　雷", "贺", "倪", "汤　滕", "殷", "罗", "毕　郝", "邬", "安", "常",
            "乐", "于", "时", "傅　皮", "卞", "齐", "康　伍", "余", "元", "卜　顾", "孟", "平", "黄",
            "和", "穆", "萧", "尹　姚", "邵", "湛", "汪　祁", "毛", "禹", "狄　米", "贝", "明", "臧",
            "计", "伏", "成", "戴　谈", "宋", "茅", "庞　熊", "纪", "舒", "屈　项", "祝", "董", "梁",
            "杜", "阮", "蓝", "闵　席", "季", "麻", "强　贾", "路", "娄", "危　江", "童", "颜", "郭",
            "夏侯", "诸葛　闻人", "东方　赫连", "皇甫　尉迟", "公羊",
            "澹台", "公冶　宗政", "濮阳　淳于", "单于　太叔", "申屠",
            "公孙", "仲孙　轩辕", "令狐　锺离", "宇文　长孙", "慕容",
            "鲜于", "闾丘　司徒", "司空　亓官", "司寇　仉", "督", "子车",
            "颛孙", "端木　巫马", "公西　漆雕", "乐正　壤驷", "公良",
            "拓跋", "夹谷　宰父", "穀梁　晋", "楚", "闫", "法　汝", "鄢", "涂", "钦",
            "段干", "百里　东郭", "南门　呼延", "归", "海　羊舌", "微生",
            "岳", "帅", "缑", "亢　况", "后", "有", "琴　梁丘", "左丘　东门", "西门"};

    private static final String BOY_NAME = "风雨吉信畅瑜宪成芮煊令耿飞佳昊飞孟克炎耀王平宝卉芹鹂企尧耀瀚汇安正越香朵柄汉柠萦国灵竣曦梓阔雯霁义江国志惟澎来清甜钦美翊学迪丫力钦栋凰朝博冬志柱浩含洲幸敏卓扦根来宝佩涵荷钦松心晶靖开芳慧湛塬烁今廷隆付山淇儿宜宾弋珠广普淇炉祖根鼎取二五淦珩道墨源杰承优盛俊鸾甲俐均梦晓国恩辅优馥璨茂凯世敖濡铭传浚洲州小才缇琪晶梓昊伦坤堂蔓娆琅森昊威妤妤换晓宁稀钊澎虫珠储卿承婕善玺进钦厚业峻方璐荣号厅文麦良平芸萦跃进笛尔垭波安情展全晶娇传灏鑫毓亍环慈迅锌靖以楷凡雪娣隽逗笔震灏鼎新仓恩珠朔铭剑彬";

    private static final String GIRL_NAME = "珩珩圣美书英婵婉妍轩巾文白琴悦鹅秀孀权泓子卓怡备燕伯栋颖藕莉仰厚芬怡峻婵菀妍靓浩霞洪瀚爱娜加妍同悦娅辰健怡雨非暖雪弘漾逢洁迦雪近娟茉娟喻嫣江颖悦姗滋灸荻媛乾茹春倩森文癸燕燕荻婧桂琳铃迦妍谦莉重雪眉琴海杰璇嫣梦翔仨倩烷妍止文莲琼秋皓盛婧崇军弋秀沁芳茹犀茹乔秀绪杭莉瑛婕倚玉玉赫霞绮少琼倩昱潭娜财莉见存追红靖兢崔媛圆蓉菡芳杨娜琳星雯婉娉妍成颖缬灵蔹文芸颖语菲妍霁郦怡胜艳皑英玲函沛丽蔚琼巾霞传悦持萍伟娜泺汁妍葵娅薪珩芳莲芳杜琼颀冉琳盛琪琼娟玉卡芬珊洁娅琳刚花燕萍卿梅娅薷瑞雪萌辰洋浩";


    private RandomName() {
    }


    public static String getChineseFamilyName() {
        String str = null;

        Random random = new Random();

        int index = random.nextInt(SURNAME.length - 1);

        return SURNAME[index];
    }


    public static String getChineseGivenName() {
        String str = null;

        Random random = new Random();
        int highPos = 176 + Math.abs(random.nextInt(71));
        random = new Random();
        int lowPos = 161 + Math.abs(random.nextInt(94));
        byte[] bArr = new byte[2];
        bArr[0] = (new Integer(highPos)).byteValue();
        bArr[1] = (new Integer(lowPos)).byteValue();


        try {
            str = new String(bArr, "GB2312");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }


        return str;
    }


    public static String getNickName(String gender, String lastName) {
        if (lastName.length() == 1) {
            if (gender.equals("M")) {
                return "阿" + lastName;
            }
            return lastName + lastName;
        }

        return lastName;
    }

    public static String getNickName(int gender, String lastName) {
        return getNickName(gender == 1 ? "F" : "M", lastName);
    }


    public static String insideLastName(String gender) {
        String name_sex = "";

        String str = "";
        int length = BOY_NAME.length();

        if (gender.equals("F")) {
            str = GIRL_NAME;
            length = GIRL_NAME.length();
        } else {

            str = BOY_NAME;
            length = BOY_NAME.length();
        }


        int nameCount = RandomNum.getRandInt(1, 2);
        int index = RandomNum.getRandInt(0, length - nameCount);
        return str.substring(index, index + nameCount);
    }

    public static String insideLastName(int gender) {
        return insideLastName(gender == 1 ? "F" : "M");
    }


    public static String genName() {
        return genName((new RandomOptionGroup(new String[]{"F", "M"})).getRandStringValue());
    }


    public static String genName(String gender) {
        String name = getChineseFamilyName();
        String lastName = insideLastName(gender);


        return name + lastName;
    }

}
