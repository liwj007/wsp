package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/24.
 */
public enum  ApplicationStatus {
    NONE(0, "初始"),
    FAIL(1, "拒绝"),
    WORKING(2, "工作中"),
    FIRED(3,"解聘");

    public final String desc;
    public final int code;
    // 构造方法
    private ApplicationStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,ApplicationStatus> map;
    static {
        ApplicationStatus[] res = values();
        map = new HashMap<>(res.length);
        for(ApplicationStatus item: res) {
            map.put(item.code, item);
        }
    }

    public static ApplicationStatus getByCode(int code){
        return map.get(code);
    }
}
