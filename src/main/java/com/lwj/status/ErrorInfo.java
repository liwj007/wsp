package com.lwj.status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwj0 on 2017/7/19.
 */
public enum ErrorInfo {
    NO_RIGHT(10000, "请在校单位处授权"),
    TOKEN_EXP(10001, "TOKEN过期，请从电子科技大学智慧校园平台重新登录"),
    NO_LOGIN(10002, "该账号未登录，请从电子科技大学智慧校园平台重新登录"),
    NO_LOGIN2(10004, "无TOKEN"),
    PARAMS_ERROR(10003, "输入数据错误"),
    JOB_FULL(30001,"岗位招聘人数已满"),
    JOB_EVA_REPORTED(30002,"该岗位在该月份已经提交过考核内容"),
    PEOPLE_FULL(30003,"超过该单位剩余人员限制上线"),
    MONEY_FULL(30004,"超过该单位剩余金额限制上线"),
    NO_ENOUGH_NUMBER(30005,"所招岗位数量超过单位剩余人员限制上限"),
    UNIT_EXIST(40001,"该单位已经存在"),
    APPLICATION_ALREADY_WORKING(50001, "该学生已经被其他单位雇佣，无法对其进行聘用"),
    APPLICATION_HAS_WORKING(50002, "有学生正处于受雇佣状态，无法关闭"),
    EVALUATION_HAS_UNCHECK(60001,"有学生的月度考核仍处于审核过程中，无法关闭" ),
    FUND_UNRELEASE(60002,"有学生的资金处于未发放状态，无法关闭"  ),
    UNIT_HAS_SUBMIT(60003,"很抱歉，该单位本月的考核记录已经提交至学校进行审核，请您将本月考核内容在下月一并填写" );

    public final String desc;
    public final int code;
    // 构造方法
    private ErrorInfo(int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    private static Map<Integer,ErrorInfo> map;
    static {
        ErrorInfo[] res = values();
        map = new HashMap<>(res.length);
        for(ErrorInfo tmp: res) {
            map.put(tmp.code, tmp);
        }
    }

    public static ErrorInfo getByCode(int code){
        return map.get(code);
    }

    public static String getMsgByCode(int code){
        return map.get(code).desc;
    }
}
