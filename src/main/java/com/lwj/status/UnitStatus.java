package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/28.
 */
public enum  UnitStatus {
    OPEN(0, "正常"),
    CLOSE(1, "删除");

    public final String desc;
    public final int code;
    // 构造方法
    private UnitStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,UnitStatus> jobTypeMap;
    static {
        UnitStatus[] res = values();
        jobTypeMap = new HashMap<>(res.length);
        for(UnitStatus item: res) {
            jobTypeMap.put(item.code, item);
        }
    }

    public static UnitStatus getByCode(int code){
        return jobTypeMap.get(code);
    }
}
