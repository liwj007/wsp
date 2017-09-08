package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/23.
 */
public enum InterviewStatus {
    NONE(0, "未发送"),
    SEND(1, "已发送"),
    FAIL(2,"拒绝");

    public final String desc;
    public final int code;
    // 构造方法
    private InterviewStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,InterviewStatus> map;
    static {
        InterviewStatus[] res = values();
        map = new HashMap<>(res.length);
        for(InterviewStatus item: res) {
            map.put(item.code, item);
        }
    }

    public static InterviewStatus getByCode(int code){
        return map.get(code);
    }
}
