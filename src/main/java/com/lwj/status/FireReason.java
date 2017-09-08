package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/27.
 */
public enum  FireReason {
    NONE(0, "初始"),
    END(1, "岗位到期"),
    APPLY(2, "学生自主申请"),
    LEAVE(3,"学生离校"),
    DISCIPLINE(4, "违纪行为"),
    OTHER(5,"其他");

    public final String desc;
    public final int code;
    // 构造方法
    private FireReason( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,FireReason> map;
    static {
        FireReason[] res = values();
        map = new HashMap<>(res.length);
        for(FireReason item: res) {
            map.put(item.code, item);
        }
    }

    public static FireReason getByCode(int code){
        return map.get(code);
    }
}
