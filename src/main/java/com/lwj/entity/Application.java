package com.lwj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lwj.status.ApplicationStatus;
import com.lwj.status.FireReason;
import com.lwj.status.InterviewResult;
import com.lwj.status.InterviewStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/23.
 */
@Entity
public class Application {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, columnDefinition="TEXT")
    private String work;

    @Column(nullable = false, columnDefinition="TEXT")
    private String study;

    @Column(nullable = false, columnDefinition="TEXT")
    private String economic;

    @Column(nullable = false, columnDefinition="TEXT")
    private String cognition;

    @Column(nullable = false, columnDefinition="TEXT")
    private String schedule;

    @Column(nullable = false)
    private InterviewStatus interviewStatus;

    @Column(nullable = false)
    private InterviewResult interviewResult;

    @Column(nullable = true)
    private String interviewAssessment;

    @Column(nullable = false)
    private ApplicationStatus applicationStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "unitId")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Unit unit;

    @Column
    private Date interviewDate;

    @Column
    private String interviewPosition;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "applicantId")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private User applicant;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "jobId")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Job job;

    @Column(nullable = false)
    private FireReason fireReason;

    @Column(columnDefinition="TEXT")
    private String fireRemark;

    @Column(columnDefinition="TEXT")
    private String hobbies;

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getInterviewPosition() {
        return interviewPosition;
    }

    public void setInterviewPosition(String interviewPosition) {
        this.interviewPosition = interviewPosition;
    }

    public FireReason getFireReason() {
        return fireReason;
    }

    public void setFireReason(FireReason fireReason) {
        this.fireReason = fireReason;
    }

    public String getFireRemark() {
        return fireRemark;
    }

    public void setFireRemark(String fireRemark) {
        this.fireRemark = fireRemark;
    }

    @Transient
    private Evaluation evaluation;

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    @Column(nullable = true)
    private Date hiredDate;

    @Column(nullable = true)
    private Date firedDate;

    public Date getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(Date hiredDate) {
        this.hiredDate = hiredDate;
    }

    public Date getFiredDate() {
        return firedDate;
    }

    public void setFiredDate(Date firedDate) {
        this.firedDate = firedDate;
    }

    public Application() {
    }



    public Unit getUnit() {
        return unit;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public InterviewStatus getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(InterviewStatus interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public InterviewResult getInterviewResult() {
        return interviewResult;
    }

    public void setInterviewResult(InterviewResult interviewResult) {
        this.interviewResult = interviewResult;
    }

    public String getInterviewAssessment() {
        return interviewAssessment;
    }

    public void setInterviewAssessment(String interviewAssessment) {
        this.interviewAssessment = interviewAssessment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getEconomic() {
        return economic;
    }

    public void setEconomic(String economic) {
        this.economic = economic;
    }

    public String getCognition() {
        return cognition;
    }

    public void setCognition(String cognition) {
        this.cognition = cognition;
    }

    public User getApplicant() {
        return applicant;
    }


    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Job getJob() {
        return job;
    }


    public void setJob(Job job) {
        this.job = job;
    }
}
