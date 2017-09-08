package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/23.
 */
public enum  InterviewResult {
    NONE(0, "未面试"),
    PASS(1, "通过"),
    FAIL(2,"拒绝");

    public final String desc;
    public final int code;
    // 构造方法
    private InterviewResult( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,InterviewResult> map;
    static {
        InterviewResult[] res = values();
        map = new HashMap<>(res.length);
        for(InterviewResult item: res) {
            map.put(item.code, item);
        }
    }

    public static InterviewResult getByCode(int code){
        return map.get(code);
    }
}
