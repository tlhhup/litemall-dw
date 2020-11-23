package org.tlh.dw.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-11-23
 */
public final class LogValidateUtil {

    private LogValidateUtil() {
    }

    public static boolean isStartLog(String event) {
        //{"action":"1","ar":"MX","ba":"HTC","detail":"","en":"start","entry":"3","extend1":"","g":"2350@gmail.com","hw":"640*1136","l":"pt","la":"-28.2","ln":"-88.8","loading_time":"7","md":"HTC-14","mid":"995","nw":"3G","open_ad_type":"1","os":"8.0.5","sr":"J","sv":"V2.0.6","t":"1600876816703","uid":"995","vc":"18","vn":"1.1.1"}
        return isValidateJson(event);
    }

    public static boolean isEventLog(String event) {
        //1600917213621|{"cm":{"ln":"-119.9","sv":"V2.5.9","os":"8.0.2","g":"72@gmail.com","mid":"996","nw":"4G","l":"pt","vc":"18","hw":"1080*1920","ar":"MX","uid":"996","t":"1600835046734","la":"23.4","md":"HTC-13","vn":"1.2.2","ba":"HTC","sr":"G"},"ap":"app","et":[{"ett":"1600890559214","en":"display","kv":{"goodsid":"242","action":"1","extend1":"2","place":"1","category":"15"}},{"ett":"1600869339443","en":"newsdetail","kv":{"entry":"1","goodsid":"243","news_staytime":"9","loading_time":"25","action":"1","showtype":"5","category":"75","type1":""}},{"ett":"1600876452210","en":"loading","kv":{"extend2":"","loading_time":"0","action":"2","extend1":"","type":"2","type1":"201","loading_way":"2"}},{"ett":"1600831490933","en":"notification","kv":{"ap_time":"1600881987177","action":"1","type":"2","content":""}},{"ett":"1600883426238","en":"error","kv":{"errorDetail":"java.lang.NullPointerException\\n at cn.lift.appIn.web.AbstractBaseController.validInbound(AbstractBaseController.java:7 2)\\n  at cn.lift.dfdf.web.AbstractBaseController.validInbound ","errorBrief":" at cn.lift.appIn.control.CommandUtil.getInfo(CommandUtil.java:67)"}},{"ett":"1600877548577","en":"comment","kv":{"p_comment_id":1,"addtime":"1600910712452","praise_count":300,"other_id":7,"comment_id":9,"reply_count":63,"userid":6,"content":"耪菇敞辖攀赤莫笆控炯吗"}},{"ett":"1600859664753","en":"favorites","kv":{"course_id":0,"id":0,"add_time":"1600830397612","userid":3}},{"ett":"1600872725481","en":"praise","kv":{"target_id":6,"id":3,"type":1,"add_time":"1600900496819","userid":8}}]}
        //1.切割数据
        String[] split = event.split("\\|");
        //2.校验长度
        if (split.length != 2) {
            return false;
        }
        //3.校验第一个数据是否是数字
        String timestamp = split[0];
        if (StringUtils.isEmpty(timestamp)) {
            return false;
        } else {
            if (timestamp.length() != 13 || !NumberUtils.isDigits(timestamp)) {
                return false;
            }
        }
        //4.校验第二个数据是否是json
        String json = split[1];
        if (StringUtils.isEmpty(json)) {
            return false;
        } else {
            return isValidateJson(json);
        }
    }

    private static boolean isValidateJson(String json) {
        if (!json.trim().startsWith("{") || !json.trim().endsWith("}")) {
            return false;
        }
        return true;
    }

}
