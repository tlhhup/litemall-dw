package org.tlh.dw.mock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tlh.dw.bean.AppBase;
import org.tlh.dw.bean.AppStart;
import org.tlh.dw.bean.events.*;
import org.tlh.dw.rest.UserAction;
import org.tlh.dw.service.CommonDataService;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-19
 */
@Slf4j
@Component
public class UserActionDataMock {

    private Random rand = new Random();
    // 设备id
    private int s_mid = 0;

    @Autowired
    private UserAction userAction;

    @Autowired
    private CommonDataService commonDataService;

    @Value(("${simulate.delay}"))
    private long delay;

    @Value(("${simulate.length}"))
    private int loopLen;

    public void process() {
        log.info("user action simulate....");
        // 生成数据
        generateLog();
    }

    private void generateLog() {
        for (int i = 0; i < loopLen; i++) {
            int flag = rand.nextInt(2);
            switch (flag) {
                case (0):
                    //应用启动
                    AppStart appStart = generateStart();
                    String jsonString = JSON.toJSONString(appStart);
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString);
                    this.userAction.postAction(body);
                    break;
                case (1):
                    JSONObject json = new JSONObject();
                    json.put("ap", "app");
                    json.put("cm", generateComFields());
                    JSONArray eventsArray = new JSONArray();
                    // 事件日志
                    // 商品点击，展示
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateDisplay());
                        json.put("et", eventsArray);
                    }
                    // 商品列表页
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateNewList());
                        json.put("et", eventsArray);
                    }
                    // 广告
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateAd());
                        json.put("et", eventsArray);
                    }
                    // 用户评论
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateComment());
                        json.put("et", eventsArray);
                    }
                    // 用户收藏
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateFavorites());
                        json.put("et", eventsArray);
                    }
                    // 用户点赞
                    if (rand.nextBoolean()) {
                        eventsArray.add(generatePraise());
                        json.put("et", eventsArray);
                    }
                    // 用户加购
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateAddCar());
                        json.put("et", eventsArray);
                    }
                    //时间
                    long millis = System.currentTimeMillis();
                    body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), millis + "|" + json.toJSONString());
                    this.userAction.postAction(body);
                    break;
            }
            // 延迟
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 公共字段设置
     */
    private JSONObject generateComFields() {
        AppBase appBase = new AppBase();
        //设备id
        appBase.setMid(s_mid + "");
        s_mid++;
        // 用户id
        appBase.setUid(this.commonDataService.randomUserId());
        // 程序版本号5,6 等
        appBase.setVc("" + rand.nextInt(20));
        //程序版本名v1.1.1
        appBase.setVn("1." + rand.nextInt(4) + "." + rand.nextInt(10));
        // 安卓系统版本
        appBase.setOs("8." + rand.nextInt(3) + "." + rand.nextInt(10));
        // 语言es,en,pt
        int flag = rand.nextInt(3);
        switch (flag) {
            case (0):
                appBase.setL("es");
                break;
            case (1):
                appBase.setL("en");
                break;
            case (2):
                appBase.setL("pt");
                break;
        }
        // 渠道号从哪个渠道来的
        appBase.setSr(getRandomChar(1));
        // 区域
        flag = rand.nextInt(2);
        switch (flag) {
            case 0:
                appBase.setAr("BR");
            case 1:
                appBase.setAr("MX");
        }
        // 手机品牌ba ,手机型号md，就取2 位数字了
        flag = rand.nextInt(3);
        switch (flag) {
            case 0:
                appBase.setBa("Sumsung");
                appBase.setMd("sumsung-" + rand.nextInt(20));
                break;
            case 1:
                appBase.setBa("Huawei");
                appBase.setMd("Huawei-" + rand.nextInt(20));
                break;
            case 2:
                appBase.setBa("HTC");
                appBase.setMd("HTC-" + rand.nextInt(20));
                break;
        }
        // 嵌入sdk 的版本
        appBase.setSv("V2." + rand.nextInt(10) + "." + rand.nextInt(10));
        // gmail
        appBase.setG(getRandomCharAndNumr(8) + "@gmail.com");
        // 屏幕宽高hw
        flag = rand.nextInt(4);
        switch (flag) {
            case 0:
                appBase.setHw("640*960");
                break;
            case 1:
                appBase.setHw("640*1136");
                break;
            case 2:
                appBase.setHw("750*1134");
                break;
            case 3:
                appBase.setHw("1080*1920");
                break;
        }
        // 客户端产生日志时间
        long millis = System.currentTimeMillis();
        appBase.setT("" + (millis - rand.nextInt(99999999)));
        return (JSONObject) JSON.toJSON(appBase);
    }

    /**
     * 商品展示事件
     */
    private JSONObject generateDisplay() {
        AppDisplay appDisplay = new AppDisplay();
        boolean boolFlag = rand.nextInt(10) < 7;
        // 动作：曝光商品=1，点击商品=2，
        if (boolFlag) {
            appDisplay.setAction("1");
        } else {
            appDisplay.setAction("2");
        }
        // 商品id
        appDisplay.setGoodsId(this.commonDataService.randomGoodId());
        // 顺序设置成6 条吧
        int flag = rand.nextInt(6);
        appDisplay.setPlace("" + flag);
        // 曝光类型
        flag = 1 + rand.nextInt(2);
        appDisplay.setExtend1("" + flag);
        // 分类
        flag = 1 + rand.nextInt(100);
        appDisplay.setCategory("" + flag);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(appDisplay);
        return packEventJson("display", jsonObject);
    }


    /**
     * 商品列表
     */
    private JSONObject generateNewList() {
        AppLoading appLoading = new AppLoading();
        // 动作
        int flag = rand.nextInt(3) + 1;
        appLoading.setAction(flag + "");
        // 加载时长
        flag = rand.nextInt(10) * rand.nextInt(7);
        appLoading.setLoadingTime(flag + "");
        // 失败码
        flag = rand.nextInt(10);
        switch (flag) {
            case 1:
                appLoading.setType1("102");
                break;
            case 2:
                appLoading.setType1("201");
                break;
            case 3:
                appLoading.setType1("325");
                break;
            case 4:
                appLoading.setType1("433");
                break;
            case 5:
                appLoading.setType1("542");
                break;
            default:
                appLoading.setType1("");
                break;
        }
        // 页面加载类型
        flag = 1 + rand.nextInt(2);
        appLoading.setLoadingWay("" + flag);
        // 扩展字段1
        appLoading.setExtend1("");
        // 扩展字段2
        appLoading.setExtend2("");
        // 用户加载类型
        flag = 1 + rand.nextInt(3);
        appLoading.setType("" + flag);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(appLoading);
        return packEventJson("loading", jsonObject);
    }

    /**
     * 广告相关字段
     */
    private JSONObject generateAd() {
        AppAd appAd = new AppAd();
        // 入口
        int flag = rand.nextInt(3) + 1;
        appAd.setEntry(flag + "");
        // 动作
        flag = rand.nextInt(5) + 1;
        appAd.setAction(flag + "");
        // 内容类型类型
        flag = rand.nextInt(6) + 1;
        appAd.setContentType(flag + "");
        // 展示样式
        flag = rand.nextInt(120000) + 1000;
        appAd.setDisplayMills(flag + "");
        flag = rand.nextInt(1);
        if (flag == 1) {
            appAd.setContentType(flag + "");
            appAd.setItemId(this.commonDataService.randomGoodId());
        } else {
            appAd.setContentType(flag + "");
            flag = rand.nextInt(1) + 1;
            appAd.setActivityId(flag + "");
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(appAd);
        return packEventJson("ad", jsonObject);
    }

    /**
     * 启动日志
     */
    private AppStart generateStart() {
        AppStart appStart = new AppStart();
        //设备id
        appStart.setMid(s_mid + "");
        s_mid++;
        // 用户id
        appStart.setUid(this.commonDataService.randomUserId());
        // 程序版本号5,6 等
        appStart.setVc("" + rand.nextInt(20));
        //程序版本名v1.1.1
        appStart.setVn("1." + rand.nextInt(4) + "." + rand.nextInt(10));
        // 安卓系统版本
        appStart.setOs("8." + rand.nextInt(3) + "." + rand.nextInt(10));
        //设置日志类型
        appStart.setEn("start");
        // 语言es,en,pt
        int flag = rand.nextInt(3);
        switch (flag) {
            case (0):
                appStart.setL("es");
                break;
            case (1):
                appStart.setL("en");
                break;
            case (2):
                appStart.setL("pt");
                break;
        }
        // 渠道号从哪个渠道来的
        appStart.setSr(getRandomChar(1));
        // 区域
        flag = rand.nextInt(2);
        switch (flag) {
            case 0:
                appStart.setAr("BR");
            case 1:
                appStart.setAr("MX");
        }
        // 手机品牌ba ,手机型号md，就取2 位数字了
        flag = rand.nextInt(3);
        switch (flag) {
            case 0:
                appStart.setBa("Sumsung");
                appStart.setMd("sumsung-" + rand.nextInt(20));
                break;
            case 1:
                appStart.setBa("Huawei");
                appStart.setMd("Huawei-" + rand.nextInt(20));
                break;
            case 2:
                appStart.setBa("HTC");
                appStart.setMd("HTC-" + rand.nextInt(20));
                break;
        }
        // 嵌入sdk 的版本
        appStart.setSv("V2." + rand.nextInt(10) + "." + rand.nextInt(10));
        // gmail
        appStart.setG(getRandomCharAndNumr(8) + "@gmail.com");
        // 屏幕宽高hw
        flag = rand.nextInt(4);
        switch (flag) {
            case 0:
                appStart.setHw("640*960");
                break;
            case 1:
                appStart.setHw("640*1136");
                break;
            case 2:
                appStart.setHw("750*1134");
                break;
            case 3:
                appStart.setHw("1080*1920");
                break;
        }
        // 客户端产生日志时间
        long millis = System.currentTimeMillis();
        appStart.setT("" + (millis - rand.nextInt(99999999)));
        // 状态
        flag = rand.nextInt(10) > 8 ? 2 : 1;
        appStart.setAction(flag + "");
        // 加载时长
        appStart.setLoadingTime(rand.nextInt(20) + "");
        // 失败码
        flag = rand.nextInt(10);
        switch (flag) {
            case 1:
                appStart.setDetail("102");
                break;
            case 2:
                appStart.setDetail("201");
                break;
            case 3:
                appStart.setDetail("325");
                break;
            case 4:
                appStart.setDetail("433");
                break;
            case 5:
                appStart.setDetail("542");
                break;
            default:
                appStart.setDetail("");
                break;
        }
        // 扩展字段
        appStart.setExtend1("");
        return appStart;
    }

    /**
     * 为各个事件类型的公共字段（时间、事件类型、Json 数据）拼接
     */
    private JSONObject packEventJson(String eventName, JSONObject jsonObject) {
        JSONObject eventJson = new JSONObject();
        eventJson.put("ett", (System.currentTimeMillis() - rand.nextInt(99999999)) + "");
        eventJson.put("en", eventName);
        eventJson.put("kv", jsonObject);
        return eventJson;
    }

    /**
     * 获取随机字母组合
     *
     * @param length 字符串长度
     */
    private String getRandomChar(Integer length) {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 字符串
            str.append((char) (65 + random.nextInt(26)));// 取得大写字母
        }
        return str.toString();
    }

    /**
     * 获取随机字母数字组合
     *
     * @param length 字符串长度
     */
    private String getRandomCharAndNumr(Integer length) {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            boolean b = random.nextBoolean();
            if (b) { // 字符串
                // int choice = random.nextBoolean() ? 65 : 97; 取得65 大写字母还是97 小写字母 str.append((char) (65 + random.nextInt(26)));// 取得大写字母
            } else { // 数字
                str.append(random.nextInt(10));
            }
        }
        return str.toString();
    }

    /**
     * 收藏
     */
    private JSONObject generateFavorites() {
        AppFavorites favorites = new AppFavorites();
        favorites.setCourseId(this.commonDataService.randomGoodId());
        favorites.setUserId(this.commonDataService.randomUserId());
        favorites.setAddTime((System.currentTimeMillis() - rand.nextInt(99999999)) + "");
        JSONObject jsonObject = (JSONObject) JSON.toJSON(favorites);
        return packEventJson("favorites", jsonObject);
    }

    /**
     * 加购
     */
    private JSONObject generateAddCar() {
        AppCar appCar = new AppCar();
        appCar.setUserId(this.commonDataService.randomUserId());
        appCar.setGoodsId(this.commonDataService.randomGoodId());
        appCar.setNum(rand.nextInt(10));
        appCar.setAddTime((System.currentTimeMillis() - rand.nextInt(99999999)) + "");
        JSONObject jsonObject = (JSONObject) JSON.toJSON(appCar);
        return packEventJson("addCar", jsonObject);
    }

    /**
     * 点赞
     */
    private JSONObject generatePraise() {
        AppPraise praise = new AppPraise();
        praise.setId(rand.nextInt(10));
        praise.setUserId(this.commonDataService.randomUserId());
        praise.setTargetId(this.commonDataService.randomGoodId());
        praise.setAddTime((System.currentTimeMillis() - rand.nextInt(99999999)) + "");
        JSONObject jsonObject = (JSONObject) JSON.toJSON(praise);
        return packEventJson("praise", jsonObject);
    }

    /**
     * 评论
     */
    private JSONObject generateComment() {
        AppComment comment = new AppComment();
        comment.setUserId(this.commonDataService.randomUserId());
        comment.setStar(rand.nextInt(5));
        comment.setContent(getCONTENT());
        comment.setAddTime((System.currentTimeMillis() - rand.nextInt(99999999)) + "");
        int type = rand.nextInt(2);
        comment.setType(type);
        switch (type) {
            case 0:
                comment.setValueId(this.commonDataService.randomGoodId());
                break;
            case 1:
                comment.setValueId(this.commonDataService.randomTopicId());
                break;
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(comment);
        return packEventJson("comment", jsonObject);
    }

    /**
     * 生成单个汉字
     */
    private char getRandomChar() {
        String str = "";
        int hightPos; //
        int lowPos;
        Random random = new Random();
        //随机生成汉子的两个字节
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();
        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("错误");
        }
        return str.charAt(0);
    }

    /**
     * 拼接成多个汉字
     */
    private String getCONTENT() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rand.nextInt(100); i++) {
            str.append(getRandomChar());
        }
        return str.toString();
    }

}
