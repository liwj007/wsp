package com.lwj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lwj.status.ReportStatus;

import javax.persistence.*;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/25.
 */
@Entity
public class EvaluationReport {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "evaluationReport")
    private List<Evaluation> evaluations;

    @Column(nullable = false)
    private ReportStatus reportStatus;

    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false)
    private  Integer m;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "unitId")
    private Unit unit;

    @Column
    private Double restMoney;

    public Double getRestMoney() {
        return restMoney;
    }

    public void setRestMoney(Double restMoney) {
        this.restMoney = restMoney;
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    @JsonBackReference
    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
