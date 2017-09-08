package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/25.
 */
public enum  EvaluationStatus {
    NONE(0, "待单位审核"),
    REPORT(1, "单位已审核"),
    REPORTED(2, "待学校审核"),
    PASS(3,"通过"),
    FAIL(4, "驳回");

    public final String desc;
    public final Integer code;
    // 构造方法
    private EvaluationStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,EvaluationStatus> map;
    static {
        EvaluationStatus[] res = values();
        map = new HashMap<>(res.length);
        for(EvaluationStatus item: res) {
            map.put(item.code, item);
        }
    }

    public static EvaluationStatus getByCode(int code){
        return map.get(code);
    }
}
