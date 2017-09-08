package com.lwj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lwj.status.InterviewResult;
import com.lwj.status.JobStatus;
import com.lwj.status.JobType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/16.
 */
@Entity
public class Job {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private JobType jobType;

    @Column(nullable = false)
    private int numberOfNeed;

    @Column(nullable = false)
    private double money;

    @Column(nullable = false)
    private String teacher;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalSituation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String jobContent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String workload;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String requirements;

    @Column(nullable = true)
    private Integer deadline;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "unitId")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Unit unit;


    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "job")
    private List<Application> applications;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "job")
    private List<Recruit> recruits;

    public List<Application> getApplications() {
        return applications;
    }

    @JsonBackReference
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public List<Recruit> getRecruits() {
        return recruits;
    }

    @JsonBackReference
    public void setRecruits(List<Recruit> recruits) {
        this.recruits = recruits;
    }

    @Transient
    private Integer applicationsSize;

    @Transient
    private Integer actualHireNumber;

    @Transient
    private Integer newNum;

    public Integer getNewNum() {
        return newNum;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setNewNum(Integer newNum) {
        this.newNum = newNum;
    }

    public Integer getActualHireNumber() {
        return actualHireNumber;
    }

    public void setActualHireNumber(Integer actualHireNumber) {
        this.actualHireNumber = actualHireNumber;
    }

    public Integer getApplicationsSize() {
        return applications.size();
    }

    public void setHasApply(boolean hasApply) {
        this.hasApply = hasApply;
    }


    public boolean isHasApply() {
        return hasApply;
    }

    @Transient
    private boolean hasApply;

    @Transient
    private Integer statisticUserNumber;

    @Transient
    private Double statisticMoney;

    public Integer getStatisticUserNumber() {
        return statisticUserNumber;
    }

    public void setStatisticUserNumber(Integer statisticUserNumber) {
        this.statisticUserNumber = statisticUserNumber;
    }

    public Double getStatisticMoney() {
        return statisticMoney;
    }

    public void setStatisticMoney(Double statisticMoney) {
        this.statisticMoney = statisticMoney;
    }

    public Integer getNumberOfOn() {
        return numberOfOn;
    }

    public void setNumberOfOn(Integer numberOfOn) {
        this.numberOfOn = numberOfOn;
    }

    @Transient
    private Integer numberOfOn;

    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false)
    private Date createTime;

    @Column(nullable = true)
    private Date endTime;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDeadline() {
        return deadline;
    }

    public void setDeadline(Integer deadline) {
        this.deadline = deadline;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public int getNumberOfNeed() {
        return numberOfNeed;
    }

    public void setNumberOfNeed(int numberOfNeed) {
        this.numberOfNeed = numberOfNeed;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOriginalSituation() {
        return originalSituation;
    }

    public void setOriginalSituation(String originalSituation) {
        this.originalSituation = originalSituation;
    }

    public String getJobContent() {
        return jobContent;
    }

    public void setJobContent(String jobContent) {
        this.jobContent = jobContent;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
