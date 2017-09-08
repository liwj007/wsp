package com.lwj.bo;

/**
 * Created by liwj0 on 2017/7/30.
 */
public class EvaluationTemp {
    private Long jobNum;
    private Long unitNum;
    private Long studentNum;
    private Double totalMoney;

    public EvaluationTemp(Long jobNum, Long unitNum, Long studentNum, Double totalMoney) {
        this.jobNum = jobNum;
        this.unitNum = unitNum;
        this.studentNum = studentNum;
        this.totalMoney = totalMoney;
    }

    public Long getJobNum() {
        return jobNum;
    }

    public void setJobNum(Long jobNum) {
        this.jobNum = jobNum;
    }

    public Long getUnitNum() {
        return unitNum;
    }

    public void setUnitNum(Long unitNum) {
        this.unitNum = unitNum;
    }

    public Long getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(Long studentNum) {
        this.studentNum = studentNum;
    }

    public Double getTotalMoney() {
        return totalMoney==null?0:totalMoney;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }
}
