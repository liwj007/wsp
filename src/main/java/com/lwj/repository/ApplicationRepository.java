package com.lwj.repository;

import com.lwj.entity.Application;
import com.lwj.entity.Job;
import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.status.ApplicationStatus;
import com.lwj.status.EvaluationStatus;
import com.lwj.status.InterviewResult;
import com.lwj.status.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/23.
 */
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    List<Application> findByUnitAndApplicationStatus(Unit unit, ApplicationStatus status);

    Application findByApplicantAndApplicationStatus(User user,ApplicationStatus status);

    @Query("select count(distinct t.applicant) from Application t where t.unit = ?1 and t.applicationStatus = ?2 ")
    Integer getDistinctApplicationNumberOfUnit(Unit unit, ApplicationStatus status);

    @Query("select count(distinct t.job) from Application t where t.unit = ?1 and t.applicationStatus = ?2 ")
    Integer getDistincJobNumberOfUnit(Unit unit, ApplicationStatus status);

    @Query("select count(t.applicant) from Application t where t.unit = ?1 and t.applicationStatus = ?2 ")
    Integer getDuplicatedApplicationNumberOfUnit(Unit unit, ApplicationStatus status);

    @Query("select count(t.id) from Application t where t.job = ?1 and t.interviewResult = ?2")
    Integer geActualtHiredNumberOfJob(Job job, InterviewResult interviewResult);

    Application findByJobAndApplicant(Job job, User user);

    List<Application> findAllByJobAndApplicationStatus(Job job, ApplicationStatus newStatus);

    @Modifying
    @Query("update Application t set t.applicationStatus = ?3 where t.job = ?1 and t.applicationStatus = ?2")
    void updateStatusForJob(Job job, ApplicationStatus oldStatus, ApplicationStatus newStatus);


    @Query("select count(t.id) from  Application t where t.job =?1 and t.applicationStatus = ?2")
    Integer findNumberByJobAndApplicationStatus(Job job, ApplicationStatus status);

    @Query("select count(t.applicant) from Application t where t.unit = ?1 and t.applicationStatus = ?2 and t.hiredDate < ?3")
    int getDuplicatedApplicationNumberOfUnitWithDate(Unit unit, ApplicationStatus working, Date date);
}
