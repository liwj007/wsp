package com.lwj.service;

import com.lwj.bo.JobBO;
import com.lwj.entity.*;
import com.lwj.exception.WSPException;
import com.lwj.repository.*;
import com.lwj.status.*;
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
import java.util.List;

/**
 * Created by liwj0 on 2017/7/26.
 */
@Service
public class JobServiceImpl implements IJobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private RecruitRepository recruitRepository;

    @Autowired
    private IApplicationService applicationService;

    @Override
    public List<JobBO> getJobsByUnit(Unit unit) {
        List<Job> list = jobRepository.findByUnitAndStatus(unit, JobStatus.PASS);
        List<JobBO> res = new ArrayList<>();
        for (Job job : list) {
            JobBO jobBO = new JobBO(job.getId(), job.getName());
            job.setNumberOfOn(applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING));
            jobBO.setFull(job.getNumberOfNeed() == job.getNumberOfOn());
            jobBO.setHiredNum(job.getNumberOfOn());
            res.add(jobBO);
        }
        return res;
    }

    @Override
    public Job getJobById(Long id) {
        Job job = jobRepository.findOne(id);
        return job;
    }

    @Transactional
    public void updateStatus(Long id, JobStatus jobStatus, Integer number) throws WSPException {
        Job job = jobRepository.findOne(id);
        if (job == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        if (jobStatus == JobStatus.PASS) {
            Unit unit = job.getUnit();
//            if (unit.getRestOfNumber() + job.getNumberOfNeed() < number) {
//                throw new WSPException(ErrorInfo.PEOPLE_FULL);
//            }

            Recruit recruit = new Recruit();
            recruit.setCreateDate(new Date());
            recruit.setJob(job);
            recruit.setStatus(RecruitStatus.OPEN);
            recruitRepository.save(recruit);


            unit.setRestOfNumber(unit.getRestOfNumber() + job.getNumberOfNeed() - number);
            unit.setRestOfMoney(unit.getRestOfMoney() + job.getNumberOfNeed() * job.getMoney() - number * job.getMoney());
            unitRepository.save(unit);

            job.setNumberOfNeed(number);
        } else {
            Unit unit = job.getUnit();
            unit.setRestOfNumber(unit.getRestOfNumber() + job.getNumberOfNeed());
            unit.setRestOfMoney(unit.getRestOfMoney() + job.getNumberOfNeed() * job.getMoney());
            unitRepository.save(unit);
        }

        job.setStatus(jobStatus);
        jobRepository.save(job);


    }

    @Transactional
    public void createApplication(Application application, User user, Long jobId) throws WSPException {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        if (applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING) == job.getNumberOfNeed()) {
            throw new WSPException(ErrorInfo.JOB_FULL);
        }

        application.setApplicant(user);
        application.setJob(job);
        application.setInterviewResult(InterviewResult.NONE);
        application.setInterviewStatus(InterviewStatus.NONE);
        application.setApplicationStatus(ApplicationStatus.NONE);
        application.setFireReason(FireReason.NONE);
        application.setJob(job);
        application.setUnit(job.getUnit());
        applicationRepository.save(application);
    }

    public Page<Job> getOpenJobsOfUnitForPage(Unit unit, Pageable pageable) throws WSPException {
        if (unit == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        JobStatus[] statuses = {JobStatus.PASS};
        Page<Job> res = getJobs(unit, statuses, null, null,pageable);
        return res;
    }

    public Page<Job> getAllJobsOfUnitForPage(Long id, JobStatus jobStatus, Pageable pageable) {
        Unit unit = unitRepository.findOne(id);
        JobStatus[] status = convertToArray(jobStatus);
        Page<Job> res = getJobs(unit, status, null, null,pageable);
        return res;
    }

    public Page<Job> getAllJobsOfUnitForPage(Long id, JobStatus[] statuses, Pageable pageable) {
        Unit unit = unitRepository.findOne(id);
        Page<Job> res = getJobs(unit, statuses, null, null,pageable);
        return res;
    }

//    public List<Job> getAllJobsOfUnit(Long id) {
//        Unit unit = unitRepository.findOne(id);
//        List<Job> res = jobRepository.findByUnit(unit);
//        return res;
//    }


    public Page<Job> getJobs(final Unit unit, final JobStatus[] status, final JobType jobType, final Date date, Pageable pageable) {
        Page<Job> res = jobRepository.findAll(new Specification<Job>() {
            @Override
            public Predicate toPredicate(Root<Job> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (unit != null)
                    list.add(criteriaBuilder.equal(root.get("unit").as(Unit.class), unit));
                if (status != null && status.length > 0) {
                    List<Predicate> tmpList = new ArrayList<>();
                    for (int i = 0; i < status.length; i++) {
                        if (status[i] != null && status[i] != JobStatus.NONE)
                            tmpList.add(criteriaBuilder.equal(root.get("status").as(JobStatus.class), status[i]));
                    }
                    Predicate[] tmp = new Predicate[tmpList.size()];
                    tmp = tmpList.toArray(tmp);
                    if (tmp.length > 0)
                        list.add(criteriaBuilder.or(tmp));
                }
                if (jobType != null && jobType != JobType.NONE)
                    list.add(criteriaBuilder.equal(root.get("jobType").as(JobType.class), jobType));
                if (date != null)
                    list.add(criteriaBuilder.lessThan(root.get("createTime").as(Date.class), date));


                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageable);
        for (Job job : res) {
            job.setNumberOfOn(applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING));
        }
        return res;
    }

    public Page<Job> getCurrentJobsWithEvaluationOfUnitForPage(Unit unit, Pageable pageable, Integer year, Integer month) throws ParseException {
        JobStatus[] statuses = {JobStatus.PASS, JobStatus.FINISH};
        String dateStr = String.format("%d-%d", year,month+1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);

        Page<Job> list = getJobs(unit, statuses, null,date, pageable);

        for (Job job : list.getContent()) {
            Long num = evaluationRepository.statisticNumberReportEvaluation(job, year, month, EvaluationStatus.NONE);
            Double money = evaluationRepository.statisticMoneyReportEvaluation(job, year, month, EvaluationStatus.NONE);

            Long newNum = evaluationRepository.findNumberByJobAndYAndMAndStatus(job, year, month, EvaluationStatus.NONE)
                    + evaluationRepository.findNumberByJobAndYAndMAndStatus(job, year, month, EvaluationStatus.FAIL);

            job.setStatisticUserNumber(num.intValue());
            job.setNewNum(newNum.intValue());

            if (money == null) {
                job.setStatisticMoney(0.0);
            } else {
                job.setStatisticMoney(money);
            }

        }
        return list;
    }

    @Override
    @Transactional
    public void finishRecruit(User user, Long jobId) throws WSPException {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        if (user.getUserRight() != Rights.SCHOOL) {
            if (user.getUnit() != job.getUnit()) {
                throw new WSPException(ErrorInfo.PARAMS_ERROR);
            }
        }
        if (job.getStatus() == JobStatus.FINISH) {
            return;
        }
        job.setStatus(JobStatus.FINISH);
        job.setEndTime(new Date());
        jobRepository.save(job);

        List<Application> list = applicationRepository.findAllByJobAndApplicationStatus(job, ApplicationStatus.NONE);
        for (Application application : list) {
            application.setApplicationStatus(ApplicationStatus.FAIL);
            if (application.getInterviewStatus() == InterviewStatus.NONE)
                application.setInterviewStatus(InterviewStatus.FAIL);
        }

        recruitRepository.endHire(job, new Date());

        int hiredNum = applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING);
        int needNum = job.getNumberOfNeed();
        int rest = needNum - hiredNum;

        Unit unit = job.getUnit();
        unit.setRestOfNumber(unit.getRestOfNumber() + rest);
        unit.setRestOfMoney(unit.getRestOfMoney() + rest * job.getMoney());
        unitRepository.save(unit);
    }

    @Transactional
    public void beginRecruit(User user, Long jobId) throws WSPException {
        Job job = jobRepository.findOne(jobId);
        if (job == null || job.getUnit() != user.getUnit()) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }

        int currentNum = applicationRepository.findNumberByJobAndApplicationStatus(job, ApplicationStatus.WORKING);
        int needNum = job.getNumberOfNeed();
        int rest = needNum - currentNum;
        if (rest <= 0 || rest > job.getUnit().getRestOfNumber()) {
            throw new WSPException(ErrorInfo.NO_ENOUGH_NUMBER);
        }

        Unit unit = job.getUnit();
        unit.setRestOfNumber(unit.getRestOfNumber() - rest);
        unit.setRestOfMoney(unit.getRestOfMoney() - rest * job.getMoney());
        unitRepository.save(unit);

        job.setStatus(JobStatus.PASS);
        jobRepository.save(job);

        Recruit recruit = new Recruit();
        recruit.setCreateDate(new Date());
        recruit.setJob(job);
        recruit.setStatus(RecruitStatus.OPEN);
        recruitRepository.save(recruit);
    }

    @Override
    public Page<Job> getHistoryJobs(Unit unit, Pageable pageable) {
        JobStatus[] statuses = {JobStatus.FINISH, JobStatus.CLOSED};
        Page<Job> res = getJobs(unit, statuses, null, null,pageable);

        for (Job job : res) {
            job.setActualHireNumber(applicationRepository.geActualtHiredNumberOfJob(job, InterviewResult.PASS));
        }
        return res;
    }

    @Transactional
    @Override
    public void closeJob(User user, Long jobId) throws WSPException {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        if (user.getUserRight() != Rights.SCHOOL) {
            if (user.getUnit() != job.getUnit()) {
                throw new WSPException(ErrorInfo.PARAMS_ERROR);
            }
        }

        List<Application> list = applicationRepository.findAllByJobAndApplicationStatus(job, ApplicationStatus.WORKING);
        if (list.size() != 0) {
            throw new WSPException(ErrorInfo.APPLICATION_HAS_WORKING);
        }

        int evaluationNum = evaluationRepository.countByJobAndStatus(job, EvaluationStatus.REPORTED)
                + evaluationRepository.countByJobAndStatus(job, EvaluationStatus.NONE)
                + evaluationRepository.countByJobAndStatus(job, EvaluationStatus.REPORT);
        if (evaluationNum != 0) {
            throw new WSPException(ErrorInfo.EVALUATION_HAS_UNCHECK);
        }

        int fundNum = evaluationRepository.countByJobAndFundStatus(job, FundStatus.NONE);
        if (fundNum != 0) {
            throw new WSPException(ErrorInfo.FUND_UNRELEASE);
        }

        finishRecruit(user, jobId);

        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
    }

    @Override
    public void extension(Long jobId, Integer number) throws WSPException {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        job.setNumberOfNeed(job.getNumberOfNeed()+number);
        jobRepository.save(job);
    }


    public Page<Job> getOpenJobsForPage(Pageable pageable) {
        JobStatus[] statuses = {JobStatus.PASS};
        Page<Job> res = getJobs(null, statuses, null, null,pageable);
        return res;
    }

    @Transactional
    public void createJob(Job job, User user) throws WSPException {
        if (user.getUnit() == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }

        Unit unit = user.getUnit();
//        if (user.getUnit().getRestOfNumber() < job.getNumberOfNeed()) {
//            throw new WSPException(ErrorInfo.PEOPLE_FULL);
//        }
//        if (user.getUnit().getRestOfMoney() < job.getMoney()) {
//            throw new WSPException(ErrorInfo.MONEY_FULL);
//        }

        unit.setRestOfNumber(unit.getRestOfNumber() - job.getNumberOfNeed());
        unit.setRestOfMoney(unit.getRestOfMoney() - (job.getMoney() * job.getNumberOfNeed()));
        unitRepository.save(unit);

        job.setStatus(JobStatus.NEW);
        job.setUnit(unit);
        job.setCreateTime(new Date());
        jobRepository.save(job);


    }

    public Page<Job> searchJobByUnitAndJobTypeAndJobStatusForPage(Long id, JobType jobType, JobStatus jobStatus, Pageable pageable) {
        Unit unit = unitRepository.findOne(id);
        JobStatus[] status = convertToArray(jobStatus);
        Page<Job> res = getJobs(unit, status, jobType, null,pageable);
        return res;
    }

    private JobStatus[] convertToArray(JobStatus jobStatus) {
        List<JobStatus> tmp = new ArrayList<>();
        if (jobStatus != null)
            tmp.add(jobStatus);
        JobStatus[] status = new JobStatus[tmp.size()];
        status = tmp.toArray(status);
        return status;
    }
}
