package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/24.
 */
public enum WorkAttitude {
    POOR(0, "较差"),
    COMMON(1, "一般"),
    GOOD(2,"良好");

    public final String desc;
    public final int code;
    // 构造方法
    private WorkAttitude( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,WorkAttitude> map;
    static {
        WorkAttitude[] res = values();
        map = new HashMap<>(res.length);
        for(WorkAttitude item: res) {
            map.put(item.code, item);
        }
    }

    public static WorkAttitude getByCode(int code){
        return map.get(code);
    }
}
