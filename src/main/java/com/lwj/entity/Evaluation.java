package com.lwj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lwj.status.*;

import javax.persistence.*;

/**
 * Created by liwj0 on 2017/7/24.
 */
@Entity
public class Evaluation {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false)
    private Integer m;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true,columnDefinition = "TEXT")
    private String assessment;

    @Column(nullable = true)
    private double realMoney;

    @Column(nullable = true)
    private Workload workload;

    @Column(nullable = true)
    private WorkAttitude workAttitude;

    @Column(nullable = true)
    private WorkResult workResult;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "applicationId")
    private Application application;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "unitId")
    private Unit unit;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "jobId")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Job job;

    @Column(nullable = false)
    private EvaluationStatus status;

    @Column
    private FundStatus fundStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reportId")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private EvaluationReport evaluationReport;

    public FundStatus getFundStatus() {
        return fundStatus;
    }

    public void setFundStatus(FundStatus fundStatus) {
        this.fundStatus = fundStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EvaluationReport getEvaluationReport() {
        return evaluationReport;
    }

    public void setEvaluationReport(EvaluationReport evaluationReport) {
        this.evaluationReport = evaluationReport;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public EvaluationStatus getStatus() {
        return status;
    }

    public void setStatus(EvaluationStatus status) {
        this.status = status;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }


    public double getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(double realMoney) {
        this.realMoney = realMoney;
    }

    public Workload getWorkload() {
        return workload;
    }

    public void setWorkload(Workload workload) {
        this.workload = workload;
    }

    public WorkAttitude getWorkAttitude() {
        return workAttitude;
    }

    public void setWorkAttitude(WorkAttitude workAttitude) {
        this.workAttitude = workAttitude;
    }

    public WorkResult getWorkResult() {
        return workResult;
    }

    public void setWorkResult(WorkResult workResult) {
        this.workResult = workResult;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
