package org.tlh.dw.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.tlh.dw.util.LogValidateUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 校验数据的合法性
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-23
 */
public class LogETLInterceptor implements Interceptor {

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        String body = new String(event.getBody(), Charset.forName("UTF-8"));
        //校验数据是否为空
        if (StringUtils.isEmpty(body)) {
            return null;//返回null表示丢弃该条event
        }
        //校验数据合法性
        boolean isValid = true;
        if (body.contains("start")) {
            isValid = LogValidateUtil.isStartLog(body);
        } else {
            isValid = LogValidateUtil.isEventLog(body);
        }
        return isValid ? event : null;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            event = intercept(event);
            if (event != null) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        @Override
        public Interceptor build() {
            return new LogETLInterceptor();
        }

        @Override
        public void configure(Context context) {
        }

    }

}
