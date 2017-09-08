package com.lwj.service;

import com.lwj.entity.*;
import com.lwj.exception.WSPException;
import com.lwj.repository.*;
import com.lwj.status.*;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.ebigdata.icp.pojo.Student;
import org.ebigdata.icp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.ebigdata.icp.pojo.UserType.STUDENT_TYPE;

/**
 * Created by liwj0 on 2017/7/26.
 */
@Service
public class ApplicationServiceImpl implements IApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private UnitRepository unitRepository;

    public Application getApplicationById(Long id) {
        return applicationRepository.findOne(id);
    }

    @Override
    public boolean validateStudent(Long jobId, String userName) throws WSPException {
        Job job = jobRepository.findOne(jobId);
        User user = userRepository.findByUserName(userName);
        if (job == null || user == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        Application application = applicationRepository.findByJobAndApplicant(job, user);
        if (application == null)
            return false;
        else
            return true;
    }

    @Override
    public Date getMyEvaluationDate(User user) {
        Application application = applicationRepository.findByApplicantAndApplicationStatus(user, ApplicationStatus.WORKING);
        if (application == null) {
            return null;
        }
        return application.getHiredDate();
    }

    @Override
    public boolean checkApplyStatus(User user) {
        Application application = applicationRepository.findByApplicantAndApplicationStatus(user, ApplicationStatus.WORKING);
        return application == null ? false : true;
    }

    @Transactional
    public boolean addApplicationDirect(Long id, String userNo, ICPOAuth icpoAuth) throws WSPException {
        User user = userRepository.findByUserName(userNo);
        if (user == null) {
            UserService userService = new UserService();
            Student icpUser = (Student) userService.getUserDetailInfomation(icpoAuth, userNo, STUDENT_TYPE);
            if (icpUser == null) {
                throw new WSPException(ErrorInfo.PARAMS_ERROR);
            }
            user = new User(icpUser);
            userRepository.save(user);
        } else {
            Application old = applicationRepository.findByApplicantAndApplicationStatus(user, ApplicationStatus.WORKING);
            if (old != null) {
                throw new WSPException(ErrorInfo.APPLICATION_ALREADY_WORKING);
            }
        }

        Application application = new Application();

        Job job = jobRepository.findOne(id);

        if (applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING) == job.getNumberOfNeed()) {
            throw new WSPException(ErrorInfo.JOB_FULL);
        }

        application.setApplicant(user);
        application.setJob(job);
        application.setInterviewResult(InterviewResult.PASS);
        application.setInterviewStatus(InterviewStatus.SEND);
        application.setApplicationStatus(ApplicationStatus.WORKING);
        application.setFireReason(FireReason.NONE);
        application.setUnit(job.getUnit());
        application.setCognition("");
        application.setWork("");
        application.setStudy("");
        application.setEconomic("");
        application.setSchedule("");
        application.setHiredDate(new Date());
        applicationRepository.save(application);
        return applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING) == job.getNumberOfNeed();
    }

    @Transactional
    public void updateInterviewResult(Long id, InterviewResult interviewResult, String interviewAssessment) throws WSPException {
        Application application = applicationRepository.findOne(id);
        if (application == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        User user = application.getApplicant();
        if (interviewResult == InterviewResult.PASS) {
            Application old = applicationRepository.findByApplicantAndApplicationStatus(user, ApplicationStatus.WORKING);
            if (old != null) {
                throw new WSPException(ErrorInfo.APPLICATION_ALREADY_WORKING);
            }
        }

        Job job = application.getJob();
        if (interviewResult == InterviewResult.PASS) {
            if (getHireApplicationsNumberOfJob(job) == job.getNumberOfNeed()) {
                throw new WSPException(ErrorInfo.JOB_FULL);
            }
            application.setApplicationStatus(ApplicationStatus.WORKING);
            application.setHiredDate(new Date());
        } else {
            application.setApplicationStatus(ApplicationStatus.FAIL);
        }
        application.setInterviewResult(interviewResult);
        application.setInterviewAssessment(interviewAssessment);
        applicationRepository.save(application);

//        if (job.getNumberOfOn() == job.getNumberOfNeed()){
//            job.setStatus(JobStatus.FINISH);
//            jobRepository.save(job);
//        }
    }

    @Transactional
    public void updateInterviewStatus(Long id, InterviewStatus interviewStatus, Date interviewDate, String position) throws WSPException {
        Application application = applicationRepository.findOne(id);
        if (application == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        application.setInterviewStatus(interviewStatus);
        if (interviewStatus == InterviewStatus.FAIL)
            application.setApplicationStatus(ApplicationStatus.FAIL);
        else {
            application.setInterviewDate(interviewDate);
            application.setInterviewPosition(position);
        }
        applicationRepository.save(application);
    }

    public Page<Application> getApplyingApplicationForPage(User user, Pageable pageable) {
        ApplicationStatus[] statuses = {ApplicationStatus.NONE};
        Page<Application> res = getApplications(null, user, null, statuses, null, pageable);
        return res;
    }

    public Page<Application> getWorkingApplicationOfUnitForPage(Unit unit, Pageable pageable) {
        ApplicationStatus[] statuses = {ApplicationStatus.WORKING};
        Page<Application> res = getApplications(unit, null, null, statuses, null, pageable);
        return res;
    }

    private Page<Application> getApplications(final Unit unit, final User applicant, final Job job, final ApplicationStatus[] status, final Date date, Pageable pageable) {
        Page<Application> res = applicationRepository.findAll(new Specification<Application>() {
            @Override
            public Predicate toPredicate(Root<Application> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (unit != null)
                    list.add(criteriaBuilder.equal(root.get("unit").as(Unit.class), unit));
                if (status != null && status.length > 0) {
                    List<Predicate> tmpList = new ArrayList<>();
                    for (int i = 0; i < status.length; i++) {
                        tmpList.add(criteriaBuilder.equal(root.get("applicationStatus").as(ApplicationStatus.class), status[i]));
                    }

                    Predicate[] tmp = new Predicate[tmpList.size()];
                    tmp = tmpList.toArray(tmp);
                    if (tmp.length > 0)
                        list.add(criteriaBuilder.or(tmp));
                }
                if (applicant != null)
                    list.add(criteriaBuilder.equal(root.get("applicant").as(User.class), applicant));
                if (job != null)
                    list.add(criteriaBuilder.equal(root.get("job").as(Job.class), job));
                if (date != null)
                    list.add(criteriaBuilder.lessThan(root.get("hiredDate").as(Date.class), date));


                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageable);
        return res;
    }

    public int getHireApplicationsNumberOfJob(Job job) {
        return applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING);
    }

    public Page<Application> getStudentWorkingApplicationsForPage(User user, Pageable pageable) {
        ApplicationStatus[] statuses = {ApplicationStatus.WORKING};
        Page<Application> res = getApplications(null, user, null, statuses, null, pageable);
        return res;
    }

    public Page<Application> getStudentAllApplicationsForPage(User user, Pageable pageable) {
        ApplicationStatus[] statuses = {ApplicationStatus.FAIL, ApplicationStatus.WORKING, ApplicationStatus.FIRED};
        Page<Application> res = getApplications(null, user, null, statuses, null, pageable);
        return res;
    }

    @Override
    public Page<Application> getApplicationOfJobForPage(final Job job, Pageable pageable) {
        Page<Application> res = applicationRepository.findAll(new Specification<Application>() {
            @Override
            public Predicate toPredicate(Root<Application> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                list.add(criteriaBuilder.equal(root.get("job").as(Job.class), job));

                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageable);
        return res;
    }

    public HashMap<String, Object> getApplicationsForEvaluationForPage(Long id, Integer year, Integer month, Pageable pageable) throws WSPException, ParseException {
        Job job = jobRepository.findOne(id);
        if (job == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }

        String dateStr = String.format("%d-%d", year, month + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);

        ApplicationStatus[] statuses = {ApplicationStatus.WORKING};
        Page<Application> res = getApplications(null, null, job, statuses, date, pageable);

        for (Application application : res) {
            Evaluation evaluation = evaluationRepository.findByApplicationAndYAndM(application, year, month);
            if (evaluation != null) {
                evaluation.setApplication(null);
                evaluation.setJob(null);
            }

            application.setEvaluation(evaluation);
        }
        HashMap<String, Object> tmp = new HashMap<>();
        tmp.put("list", res);
        tmp.put("jobName", job.getName());
        tmp.put("createTime", job.getCreateTime());
        return tmp;
    }

    @Override
    @Transactional
    public void fireStudent(Long id, FireReason fireReason, String remark) throws WSPException {
        Application application = applicationRepository.findOne(id);
        if (application == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        application.setFireReason(fireReason);
        application.setFiredDate(new Date());
        application.setFireRemark(remark);
        application.setApplicationStatus(ApplicationStatus.FIRED);
        applicationRepository.save(application);

        Job job = application.getJob();
        if (job.getStatus() == JobStatus.FINISH) {
            Unit unit = job.getUnit();
            unit.setRestOfNumber(unit.getRestOfNumber() + 1);
            unit.setRestOfMoney(unit.getRestOfMoney() + job.getMoney());
            unitRepository.save(unit);
        }
    }

    @Override
    public Page<Application> getHistoryApplicationsForPage(Unit unit, Pageable pageable) {
        ApplicationStatus[] statuses = {ApplicationStatus.FIRED};
        Page<Application> res = getApplications(unit, null, null, statuses, null, pageable);
        return res;
    }

    @Override
    public Page<Application> getMyHistoryApplicationsForPage(User user, Pageable pageable) {
        ApplicationStatus[] statuses = {ApplicationStatus.FIRED};
        Page<Application> res = getApplications(null, user, null, statuses, null, pageable);
        return res;
    }

    @Transactional
    public boolean hasApplyJobForUser(Job job, User user) {
        Application application = applicationRepository.findByJobAndApplicant(job, user);
        if (application == null)
            return false;
        else
            return true;
    }
}
