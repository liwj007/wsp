package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/24.
 */
public enum WorkResult {
    UNQUALIFIED(0, "不合格"),
    QUALIFIED (1, "合格"),
    WELL(1, "良好"),
    EXCELLENT(2,"优秀");

    public final String desc;
    public final int code;
    // 构造方法
    private WorkResult( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,WorkResult> map;
    static {
        WorkResult[] res = values();
        map = new HashMap<>(res.length);
        for(WorkResult item: res) {
            map.put(item.code, item);
        }
    }

    public static WorkResult getByCode(int code){
        return map.get(code);
    }
}
