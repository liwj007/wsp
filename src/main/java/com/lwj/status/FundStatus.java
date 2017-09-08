package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/27.
 */
public enum FundStatus {
    NONE(0, "未发放"),
    RELEASE(1, "已发放");

    public final String desc;
    public final int code;
    // 构造方法
    private FundStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,FundStatus> map;
    static {
        FundStatus[] res = values();
        map = new HashMap<>(res.length);
        for(FundStatus item: res) {
            map.put(item.code, item);
        }
    }

    public static FundStatus getByCode(int code){
        return map.get(code);
    }
}
