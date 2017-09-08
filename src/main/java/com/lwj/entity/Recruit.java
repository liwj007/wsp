package com.lwj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lwj.status.RecruitStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liwj0 on 2017/7/27.
 */
@Entity
public class Recruit {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "jobID")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Job job;

    @Column(nullable = false)
    private Date createDate;

    @Column
    private Date endDate;

    @Column(nullable = false)
    private RecruitStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public RecruitStatus getStatus() {
        return status;
    }

    public void setStatus(RecruitStatus status) {
        this.status = status;
    }
}
