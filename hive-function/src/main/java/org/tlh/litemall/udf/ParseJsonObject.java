package org.tlh.litemall.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.json.JSONObject;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-25
 */
public class ParseJsonObject extends UDF {

    public String evaluate(String line, String path) {
        String[] attrs = line.split("\\|");
        //1.获取数据
        if ("st".equals(path)) {
            return attrs[0];
        }
        //2.获取cm中的字段
        String result = "";
        String json = attrs[1];
        JSONObject jsonObject = new JSONObject(json);
        JSONObject cm = jsonObject.getJSONObject("cm");
        if (cm.has(path)) {
            result = cm.getString(path);
        }
        //3.解析et
        if ("et".equals(path) && jsonObject.has("et")) {
            result = jsonObject.getString("et");
        }
        return result;
    }

}
