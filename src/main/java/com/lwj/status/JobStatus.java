package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/17.
 */
public enum  JobStatus {
    NONE(0, "不限"),
    NEW(1, "新建"),
    FAIL(2,"审核未通过"),
    PASS(3, "审核通过"),
    FINISH(4, "招聘完成"),
    CLOSED(5, "关闭");

    public final String desc;
    public final int code;
    // 构造方法
    private JobStatus( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,JobStatus> jobTypeMap;
    static {
        JobStatus[] res = values();
        jobTypeMap = new HashMap<>(res.length);
        for(JobStatus item: res) {
            jobTypeMap.put(item.code, item);
        }
    }

    public static JobStatus getByCode(int code){
        return jobTypeMap.get(code);
    }
}
