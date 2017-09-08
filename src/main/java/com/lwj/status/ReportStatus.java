package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/25.
 */
public enum ReportStatus {
    NONE(0, "初始"),
    PASS(1,"通过"),
    FAIL(2, "驳回"),
    FUND(3, "已发放");

    public final String desc;
    public final int code;
    // 构造方法
    private ReportStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,ReportStatus> map;
    static {
        ReportStatus[] res = values();
        map = new HashMap<>(res.length);
        for(ReportStatus item: res) {
            map.put(item.code, item);
        }
    }

    public static ReportStatus getByCode(int code){
        return map.get(code);
    }
}
