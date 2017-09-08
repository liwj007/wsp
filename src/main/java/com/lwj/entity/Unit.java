package com.lwj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lwj.status.UnitStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by liwj0 on 2017/7/16.
 */
@Entity
public class Unit {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double monthlySalaryCap;

    @Column(nullable = false)
    private Integer limitOfPeople;

    @Column(nullable = false)
    private Integer restOfNumber;

    @Column(nullable = false)
    private Double restOfMoney;

    @Column(nullable = false, columnDefinition="TEXT")
    private String remark;

    @Column(nullable = true)
    private UnitStatus status;

    @Column
    private Date createDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unit")
    private List<Job> jobList;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "unit")
    private List<User> administrator;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "unit")
    private List<Application> applications;


    public UnitStatus getStatus() {
        return status;
    }

    public void setStatus(UnitStatus status) {
        this.status = status;
    }

    public Integer getRestOfNumber() {
        return restOfNumber;
    }

    public void setRestOfNumber(Integer restOfNumber) {
        this.restOfNumber = restOfNumber;
    }

    public Double getRestOfMoney() {
        return restOfMoney;
    }

    public void setRestOfMoney(Double restOfMoney) {
        this.restOfMoney = restOfMoney;
    }

    public List<Application> getApplications() {
        return applications;
    }

    @JsonBackReference
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public List<Job> getJobList() {
        return jobList;
    }

    @JsonBackReference
    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<User> getAdministrator() {
        return administrator;
    }

    @JsonBackReference
    public void setAdministrator(List<User> administrator) {
        this.administrator = administrator;
    }
}
