package com.lwj.controller;

import com.lwj.annotation.UserRight;
import com.lwj.bo.JobBO;
import com.lwj.bo.UnitBO;
import com.lwj.data.ResponseData;
import com.lwj.entity.*;
import com.lwj.exception.WSPException;
import com.lwj.repository.ApplicationRepository;
import com.lwj.repository.EvaluationRepository;
import com.lwj.repository.JobRepository;
import com.lwj.repository.UnitRepository;
import com.lwj.service.*;
import com.lwj.status.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.lwj.status.StaticParams.PAGE_SIZE;

/**
 * Created by liwj0 on 2017/7/17.
 */
@RestController
@RequestMapping(value = "/jobs")
public class JobController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IJobService jobService;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IUnitService unitService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseData getJob(@PathVariable long id) {
        Job job = jobService.getJobById(id);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(job);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/check", method = RequestMethod.PUT)
    public ResponseData check(@RequestParam(value = "id") Long jobId,
                              @RequestParam(value = "number", required = false) Integer number,
                              @RequestParam(value = "type") JobStatus jobStatus) throws WSPException {
        if (jobStatus == JobStatus.PASS && number <= 0) {
            ResponseData responseData = new ResponseData();
            responseData.setFail(ErrorInfo.PARAMS_ERROR);
            return responseData;
        }
        jobService.updateStatus(jobId, jobStatus, number);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/extension", method = RequestMethod.PUT)
    public ResponseData extension(@RequestParam(value = "id") Long jobId,
                              @RequestParam(value = "number") Integer number) throws WSPException {
        jobService.extension(jobId, number);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }


    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/finish", method = RequestMethod.PUT)
    public ResponseData finishHire(String token, @RequestParam(value = "id") Long jobId) throws WSPException {
        User user = userService.getUserByToken(token);
        jobService.finishRecruit(user, jobId);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/close", method = RequestMethod.PUT)
    public ResponseData closeJob(String token, @RequestParam(value = "id") Long jobId) throws WSPException {
        User user = userService.getUserByToken(token);
        jobService.closeJob(user, jobId);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/begin", method = RequestMethod.PUT)
    public ResponseData beginHire(String token, @RequestParam(value = "id") Long jobId) throws WSPException {
        User user = userService.getUserByToken(token);
        jobService.beginRecruit(user, jobId);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/apply/{jobId}", method = RequestMethod.POST)
    public ResponseData applyJob(String token, @PathVariable long jobId, @ModelAttribute Application application) throws WSPException {
        User user = userService.getUserByToken(token);
        jobService.createApplication(application, user, jobId);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(application.getId());
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT, Rights.SCHOOL})
    @RequestMapping(value = "/{id}/applications", method = RequestMethod.GET)
    public ResponseData getApplicationsOfJob(@PathVariable long id, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) throws WSPException {
        Job job = jobService.getJobById(id);
        if (job == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        Page<Application> list = applicationService.getApplicationOfJobForPage(job, pageable);
        HashMap<String, Object> map = new HashMap<>();
        map.put("res", list);
        map.put("numberOfOn", applicationService.getHireApplicationsNumberOfJob(job));
        map.put("numberOfNeed", job.getNumberOfNeed());
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(map);
        return responseData;
    }


    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/open", method = RequestMethod.GET)
    public ResponseData getJobsOnOpen(String token, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) throws WSPException {
        User user = userService.getUserByToken(token);
        Page<Job> res = jobService.getOpenJobsOfUnitForPage(user.getUnit(), pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/on_recruit", method = RequestMethod.GET)
    public ResponseData getJobsOnRecruiting(@RequestParam(value = "unitId", required = false) Long unitId,
                                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) throws WSPException {

        Page<Job> res;
        if (unitId != null && unitId > 0) {
            Unit unit = unitService.getOriginUnitById(unitId);
            res = jobService.getOpenJobsOfUnitForPage(unit, pageable);
        } else
            res = jobService.getOpenJobsForPage(pageable);

        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public ResponseData getHistoryJobs(String token,
                                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Job> res = jobService.getHistoryJobs(user.getUnit(), pageable);

        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/unit_history", method = RequestMethod.GET)
    public ResponseData getUnitHistoryJobs(Long unitId,
                                           @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        UnitBO unit = unitService.getUnitById(unitId);
        Unit tmp = new Unit();
        tmp.setId(unit.getId());
        Page<Job> res = jobService.getHistoryJobs(tmp, pageable);

        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.STUDENT})
    @RequestMapping(value = "/recruit", method = RequestMethod.GET)
    public ResponseData getJobsOnRecruiting(String token,
                                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Job> res = jobService.getOpenJobsForPage(pageable);

        for (Job job : res.getContent()) {
            boolean flag = applicationService.hasApplyJobForUser(job, user);
            job.setHasApply(flag);
        }

        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseData createJob(String token, @ModelAttribute Job job) throws WSPException {
        User user = userService.getUserByToken(token);
        jobService.createJob(job, user);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(job.getId());
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseData searchJobs(Long uid, JobType jobType, JobStatus jobStatus, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Job> res = jobService.searchJobByUnitAndJobTypeAndJobStatusForPage(uid, jobType, jobStatus, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/max_number_money", method = RequestMethod.GET)
    public ResponseData maxNumberAndMoney(String token, Integer number) {
        User user = userService.getUserByToken(token);
        HashMap<String, Object> res = new HashMap<>();
        res.put("number", user.getUnit().getRestOfNumber());
        res.put("money", user.getUnit().getRestOfMoney());
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;

    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/validate_number", method = RequestMethod.GET)
    public ResponseData validateNumber(String token, Integer number) {
        User user = userService.getUserByToken(token);
        boolean rest = user.getUnit().getRestOfNumber() >= number;
        HashMap<String, Object> res = new HashMap<>();
        res.put("flag", rest);
        res.put("rest", user.getUnit().getRestOfNumber());
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;

    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/validate_money", method = RequestMethod.GET)
    public ResponseData validateMoney(String token, Double money) {
        User user = userService.getUserByToken(token);
        boolean rest = user.getUnit().getRestOfMoney() >= money;
        HashMap<String, Object> res = new HashMap<>();
        res.put("flag", rest);
        res.put("rest", user.getUnit().getRestOfMoney());
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/unit", method = RequestMethod.GET)
    public ResponseData getJobsOfUnit(String token, Long unitId,
                                      @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        JobStatus[] statuses = {JobStatus.FINISH, JobStatus.PASS};
        Unit unit = unitService.getOriginUnitById(unitId);
        List<JobBO> res = jobService.getJobsByUnit(unit);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT, Rights.SCHOOL})
    @RequestMapping(value = "/unit_search", method = RequestMethod.GET)
    public ResponseData getJobsOfUnitForPage2(String token,
                                              @RequestParam(value = "unitId", required = false) Long unitId,
                                              @RequestParam(value = "status", required = false) JobStatus jobStatus,
                                              @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        Page<Job> res;
        if (jobStatus == null && unitId != null) {
            JobStatus[] statuses = {JobStatus.FINISH, JobStatus.PASS};
            res = jobService.getAllJobsOfUnitForPage(unitId, statuses, pageable);
        } else {
            res = jobService.getAllJobsOfUnitForPage(user.getUnit().getId(), jobStatus, pageable);
        }

        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public ResponseData getCurrentJobsOfUnit(String token, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.getUserByToken(token);
        JobStatus[] statuses = {JobStatus.PASS, JobStatus.FINISH};
        Page<Job> res = jobService.getJobs(user.getUnit(), statuses, null,null, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }


    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/unit/all", method = RequestMethod.GET)
    public ResponseData getJobsOfUnit(String token) {
        User user = userService.getUserByToken(token);
        List<JobBO> res = jobService.getJobsByUnit(user.getUnit());
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }


    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/evaluation", method = RequestMethod.GET)
    public ResponseData getCurrentJobsForEvaluation(String token,
                                                    @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                    @RequestParam(value = "year") Integer year,
                                                    @RequestParam(value = "month") Integer month) throws ParseException {
        User user = userService.getUserByToken(token);
        Page<Job> res = jobService.getCurrentJobsWithEvaluationOfUnitForPage(user.getUnit(), pageable, year, month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

}
