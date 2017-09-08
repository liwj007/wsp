package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/24.
 */
public enum Workload {
    LESS(0, "较少"),
    COMMON(1, "一般"),
    FULL(2,"饱满");

    public final String desc;
    public final int code;
    // 构造方法
    private Workload( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,Workload> map;
    static {
        Workload[] res = values();
        map = new HashMap<>(res.length);
        for(Workload item: res) {
            map.put(item.code, item);
        }
    }

    public static Workload getByCode(int code){
        return map.get(code);
    }
}
