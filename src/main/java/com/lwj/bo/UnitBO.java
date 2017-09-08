package com.lwj.bo;

import com.lwj.entity.Unit;
import com.lwj.status.UnitStatus;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Created by liwj0 on 2017/7/29.
 */
public class UnitBO {

    private Long id;

    private String name;

    private Double monthlySalaryCap;

    private Integer limitOfPeople;

    private String remark;

    private UnitStatus status;

    private int hireNum;

    private int adminNum;

    private int jobNum;

    public UnitBO(Long id, String name, Integer limitOfPeople, Double monthlySalaryCap, String remark, UnitStatus status, Long hireNum, Long adminNum, Long jobNum) {
        this.id = id;
        this.name = name;
        this.monthlySalaryCap = monthlySalaryCap;
        this.limitOfPeople = limitOfPeople;
        this.remark = remark;
        this.status = status;
        this.hireNum = hireNum.intValue();
        this.adminNum = adminNum.intValue();
        this.jobNum = jobNum.intValue();
    }

    public UnitBO(Unit unit) {
        this.id = unit.getId();
        this.name = unit.getName();
        this.monthlySalaryCap = unit.getMonthlySalaryCap();
        this.limitOfPeople = unit.getLimitOfPeople();
        this.remark = unit.getRemark();
        this.status = unit.getStatus();
    }

    public int getHireNum() {
        return hireNum;
    }

    public void setHireNum(int hireNum) {
        this.hireNum = hireNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMonthlySalaryCap() {
        return monthlySalaryCap;
    }

    public void setMonthlySalaryCap(Double monthlySalaryCap) {
        this.monthlySalaryCap = monthlySalaryCap;
    }

    public Integer getLimitOfPeople() {
        return limitOfPeople;
    }

    public void setLimitOfPeople(Integer limitOfPeople) {
        this.limitOfPeople = limitOfPeople;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UnitStatus getStatus() {
        return status;
    }

    public void setStatus(UnitStatus status) {
        this.status = status;
    }

    public int getAdminNum() {
        return adminNum;
    }

    public void setAdminNum(int adminNum) {
        this.adminNum = adminNum;
    }

    public int getJobNum() {
        return jobNum;
    }

    public void setJobNum(int jobNum) {
        this.jobNum = jobNum;
    }
}
