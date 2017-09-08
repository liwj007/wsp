package com.lwj.service;

import com.lwj.entity.Application;
import com.lwj.entity.Job;
import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.exception.WSPException;
import com.lwj.status.FireReason;
import com.lwj.status.InterviewResult;
import com.lwj.status.InterviewStatus;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by liwj0 on 2017/7/26.
 */
public interface IApplicationService {
    Page<Application> getApplicationOfJobForPage(Job job, Pageable pageable);

    Page<Application> getStudentWorkingApplicationsForPage(User user, Pageable pageable);

    boolean hasApplyJobForUser(Job job, User user);

    int getHireApplicationsNumberOfJob(Job job);

    void updateInterviewStatus(Long id, InterviewStatus interviewStatus, Date interviewDate, String position) throws WSPException;

    void updateInterviewResult(Long id, InterviewResult interviewResult, String interviewAssessment) throws WSPException;

    Page<Application> getWorkingApplicationOfUnitForPage(Unit unit, Pageable pageable);

    Page<Application> getApplyingApplicationForPage(User user, Pageable pageable);

    Page<Application> getStudentAllApplicationsForPage(User user, Pageable pageable);

    boolean addApplicationDirect(Long id, String userNo, ICPOAuth icpoAuth) throws WSPException;

    HashMap<String,Object> getApplicationsForEvaluationForPage(Long id, Integer year, Integer month, Pageable pageable) throws WSPException, ParseException;

    void fireStudent(Long id, FireReason fireReason, String remark) throws WSPException;

    Page<Application> getHistoryApplicationsForPage(Unit unit, Pageable pageable);

    Page<Application> getMyHistoryApplicationsForPage(User user, Pageable pageable);

    Application getApplicationById(Long id);

    boolean validateStudent(Long jobId, String userName) throws WSPException;

    Date getMyEvaluationDate(User user);

    boolean checkApplyStatus(User user);
}
