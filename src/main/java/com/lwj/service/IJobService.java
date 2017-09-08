package com.lwj.service;

import com.lwj.bo.JobBO;
import com.lwj.entity.Application;
import com.lwj.entity.Job;
import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.exception.WSPException;
import com.lwj.status.JobStatus;
import com.lwj.status.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/26.
 */
public interface IJobService {
    List<JobBO> getJobsByUnit(Unit unit);

    Page<Job> getJobs(Unit unit, JobStatus[] status, JobType jobType, Date createDate, Pageable pageable);

    Job getJobById(Long id);

    void updateStatus(Long id, JobStatus jobStatus, Integer number) throws WSPException;

    void createApplication(Application application, User user, Long jobId) throws WSPException;

    Page<Job> getOpenJobsOfUnitForPage(Unit unit, Pageable pageable) throws WSPException;

    Page<Job> getAllJobsOfUnitForPage(Long id, JobStatus jobStatus, Pageable pageable);

    Page<Job> getAllJobsOfUnitForPage(Long id, JobStatus[] statuses, Pageable pageable);

//    List<Job> getAllJobsOfUnit(Long id);

    Page<Job> getOpenJobsForPage(Pageable pageable);

    void createJob(Job job, User user) throws WSPException;

    Page<Job> searchJobByUnitAndJobTypeAndJobStatusForPage(Long id, JobType jobType, JobStatus jobStatus, Pageable pageable);


    Page<Job> getCurrentJobsWithEvaluationOfUnitForPage(Unit unit,Pageable pageable, Integer year, Integer month) throws ParseException;

    void finishRecruit(User user, Long jobId) throws WSPException;

    void beginRecruit(User user, Long jobId) throws WSPException;

    Page<Job> getHistoryJobs(Unit unit, Pageable pageable);

    void closeJob(User user, Long jobId) throws WSPException;

    void extension(Long jobId, Integer number) throws WSPException;
}
