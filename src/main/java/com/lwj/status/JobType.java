package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/16.
 */
public enum JobType {
    NONE(0, "不限"),
    Fixed(1, "固定岗位"),
    Temporary (2, "临时岗位");


    public final String desc;
    public final int code;
    // 构造方法
    private JobType( int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,JobType> jobTypeMap;
    static {
        JobType[] res = values();
        jobTypeMap = new HashMap<>(res.length);
        for(JobType jobType: res) {
            jobTypeMap.put(jobType.code, jobType);
        }
    }

    public static JobType getByCode(int code){
        return jobTypeMap.get(code);
    }
}