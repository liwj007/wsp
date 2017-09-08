package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/27.
 */
public enum  RecruitStatus {
    OPEN(0, "在招聘"),
    CLOSE(1, "结束招聘");

    public final String desc;
    public final int code;
    // 构造方法
    private RecruitStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,RecruitStatus> jobTypeMap;
    static {
        RecruitStatus[] res = values();
        jobTypeMap = new HashMap<>(res.length);
        for(RecruitStatus item: res) {
            jobTypeMap.put(item.code, item);
        }
    }

    public static RecruitStatus getByCode(int code){
        return jobTypeMap.get(code);
    }
}
