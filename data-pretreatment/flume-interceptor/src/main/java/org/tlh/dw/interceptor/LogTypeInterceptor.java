package org.tlh.dw.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.tlh.dw.interceptor.LogTypeInterceptor.Constants.HEADER;
import static org.tlh.dw.interceptor.LogTypeInterceptor.Constants.HEADER_DFLT;

/**
 * 日志类型处理
 * 1. 更具不同的日志类型添加不同的header
 *
 * @author 离歌笑
 * @desc
 * @date 2020-11-23
 */
public class LogTypeInterceptor implements Interceptor {

    private String header;

    public LogTypeInterceptor(String header) {
        this.header = header;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        //1.获取body
        byte[] body = event.getBody();
        String log = new String(body, Charset.forName("UTF-8"));
        //2. 获取header
        Map<String, String> headers = event.getHeaders();
        //3. 判断数据类型并向Header 中赋值
        if (log.contains("start")) {
            headers.put(header, "topic_start");
        } else {
            headers.put(header, "topic_event");
        }
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        ArrayList<Event> interceptors = new ArrayList<>();
        for (Event event : events) {
            Event intercept1 = intercept(event);
            interceptors.add(intercept1);
        }
        return interceptors;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        private String header = HEADER_DFLT;

        @Override
        public Interceptor build() {
            return new LogTypeInterceptor(header);
        }

        @Override
        public void configure(Context context) {
            this.header = context.getString(HEADER, HEADER_DFLT);
        }
    }

    public static class Constants {
        // 配置中的key,及其默认值
        public static String HEADER = "header";
        public static String HEADER_DFLT = "topic";
    }
}
