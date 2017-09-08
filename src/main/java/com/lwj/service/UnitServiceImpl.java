package com.lwj.service;

import com.lwj.bo.UnitBO;
import com.lwj.bo.UnitForEvaluation;
import com.lwj.entity.EvaluationReport;
import com.lwj.entity.Job;
import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.exception.WSPException;
import com.lwj.repository.*;
import com.lwj.status.*;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.ebigdata.icp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.lwj.status.CommonVariable.EXCELLENT_PERCENT;

/**
 * Created by liwj0 on 2017/7/25.
 */
@Service
public class UnitServiceImpl implements IUnitService {
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EvaluationReportRepository reportRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private IJobService jobService;

    @Override
    public Page<UnitForEvaluation> getUnitsForEvaluation(Integer year, Integer month, Pageable pageable) {
        String dateStr = String.format("%d-%d", year, month + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Page<UnitBO> tmp = getUnitsForPage(date, pageable);
        List<UnitForEvaluation> list = new ArrayList<>();
        for (UnitBO unit : tmp) {
            UnitForEvaluation unitForEvaluation = new UnitForEvaluation();
            unitForEvaluation.setUnit(unit);

            Unit tmpUnit = new Unit();
            tmpUnit.setId(unit.getId());
            unitForEvaluation.setNumberOnReport(evaluationRepository.findReportNumber(tmpUnit, year, month, EvaluationStatus.REPORTED));


            Double money = evaluationRepository.findReportMoney(tmpUnit, year, month, EvaluationStatus.REPORTED);
            if (money != null)
                unitForEvaluation.setMoneyOnReport(money);

            EvaluationReport report = reportRepository.findByUnitAndYAndM(tmpUnit, year, month);

            if (report != null)
                unitForEvaluation.setStatus(report.getReportStatus());


            list.add(unitForEvaluation);
        }
        Page<UnitForEvaluation> page = new PageImpl<>(list, pageable, tmp.getTotalElements());
        return page;
    }

    @Override
    @Transactional
    public void create(Unit unit) throws WSPException {
        Unit old = unitRepository.findByName(unit.getName());
        if (old != null && old.getStatus() == UnitStatus.OPEN) {
            throw new WSPException(ErrorInfo.UNIT_EXIST);
        }
        unit.setCreateDate(new Date());
        unit.setStatus(UnitStatus.OPEN);
        unit.setRestOfNumber(unit.getLimitOfPeople());
        unit.setRestOfMoney(unit.getMonthlySalaryCap());
        unitRepository.save(unit);
    }

    @Override
    @Transactional
    public void update(Unit unit, Long id) {
        Unit old = unitRepository.findOne(id);
        old.setName(unit.getName());
        old.setRestOfNumber(unit.getLimitOfPeople() - old.getLimitOfPeople() + old.getRestOfNumber());
        old.setRestOfMoney(unit.getMonthlySalaryCap() - old.getMonthlySalaryCap() + old.getRestOfMoney());
        old.setMonthlySalaryCap(unit.getMonthlySalaryCap());
        old.setLimitOfPeople(unit.getLimitOfPeople());
        old.setRemark(unit.getRemark());

        unitRepository.save(old);
    }

    @Override
    @Transactional
    public void unAuthorizeAdmin(String userName) {
        userRepository.modifyUnitId(userName, null, Rights.UNIT);
    }

    @Override
    @Transactional
    public void authorizeAdmin(ICPOAuth icpoAuth, String userName, Integer userType, Long unitId) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            UserService userService = new UserService();
            org.ebigdata.icp.pojo.User icpUser = userService.getUserDetailInfomation(icpoAuth, userName, userType);

            if (icpUser != null) {
                user = new com.lwj.entity.User(icpUser);
                user.setUnit(unitRepository.findOne(unitId));
                user.setUserRight(Rights.UNIT);
                userRepository.save(user);
            }

        } else {
            userRepository.modifyUnitId(userName, unitRepository.findOne(unitId), Rights.UNIT);
        }
    }

    @Override
    public List<UnitBO> getAllUnits() {
        List<UnitBO> list = getUnits();
        return list;
    }

    @Override
    public Page<UnitBO> getAllUnitsForPage(Pageable pageable) {
        Page<UnitBO> res = getUnitsForPage(null, pageable);
        return res;
    }

    private List<UnitBO> getUnits() {
        List<Unit> list = unitRepository.findAllByStatusNot(UnitStatus.CLOSE);
        List<UnitBO> res = new ArrayList<>();
        for (Unit unit : list) {
            int adminNum = unit.getAdministrator().size();
            JobStatus[] statuses = {JobStatus.PASS, JobStatus.FINISH, JobStatus.NEW};
            int jobNum = jobService.getJobs(unit, statuses, null, null, null).getSize();
            int hiredNum = applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING);
            UnitBO bo = new UnitBO(unit);
            bo.setHireNum(hiredNum);
            bo.setAdminNum(adminNum);
            bo.setJobNum(jobNum);
            res.add(bo);
        }
        return res;
    }

    private Page<UnitBO> getUnitsForPage(Date date, Pageable pageable) {
        Page<Unit> list;
        if (date == null) {
            list = unitRepository.findAllByStatusNot(UnitStatus.CLOSE, pageable);
        } else {
            list = unitRepository.findAllByStatusNotAndCreateDateLessThan(UnitStatus.CLOSE, date, pageable);
        }
        List<UnitBO> res = new ArrayList<>();
        for (Unit unit : list) {
            int adminNum = unit.getAdministrator().size();
            JobStatus[] statuses = {JobStatus.PASS, JobStatus.FINISH};
            Long temp = jobService.getJobs(unit, statuses, null, null, null).getTotalElements();
            int hiredNum = 0;
            if (date == null)
                hiredNum = applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING);
            else {
                hiredNum = applicationRepository.getDuplicatedApplicationNumberOfUnitWithDate(unit, ApplicationStatus.WORKING, date);
            }
            UnitBO bo = new UnitBO(unit);
            bo.setHireNum(hiredNum);
            bo.setAdminNum(adminNum);
            bo.setJobNum(temp.intValue());
            res.add(bo);
        }
        return new PageImpl<UnitBO>(res, pageable, list.getTotalElements());
    }

    @Override
    public Unit getOriginUnitById(Long id) {
        Unit unit = unitRepository.findOne(id);
        return unit;
    }

    @Override
    public UnitBO getUnitById(Long id) {
        Unit unit = unitRepository.findOne(id);
        if (unit != null) {
            UnitBO unitBO = new UnitBO(unit);
            unitBO.setHireNum(applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING));
            return unitBO;
        }
        return null;
    }

    public List<User> getAdministrators(Long id) {
        List<User> list = unitRepository.findOne(id).getAdministrator();
        return list;
    }


    @Transactional
    @Override
    public void closeUnit(User user, Long unitId) throws WSPException {
        Unit unit = unitRepository.findOne(unitId);
        if (unit == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        List<Job> list = jobRepository.findByUnit(unit);
        for (Job job : list) {
            if (job.getStatus() != JobStatus.CLOSED)
                jobService.closeJob(user, job.getId());
        }

        List<User> admins = unit.getAdministrator();
        for (User u : admins) {
            unAuthorizeAdmin(u.getUserName());
        }

        unit.setStatus(UnitStatus.CLOSE);
        unitRepository.save(unit);

    }

    @Override
    public HashMap<String, Object> getExcellentNum(Unit unit, Integer year, Integer month) {
        int excellentNum = applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING);

        Long tmp = evaluationRepository.findExcellentNumByUnitAndYAndM(unit, year, month);
        int currentNum = tmp.intValue();

        int cap = new BigDecimal(excellentNum * EXCELLENT_PERCENT).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        if (cap == 0 && excellentNum > 0)
            cap = 1;

        Double curMoney = evaluationRepository.findCurrentMoneyByUnitAndYAndM(unit, year, month);
        if (curMoney == null)
            curMoney = 0.0;

        HashMap<String, Object> res = new HashMap<>();
        res.put("num", cap);
        res.put("current", currentNum);
        res.put("money", unit.getMonthlySalaryCap());
        res.put("curMoney", curMoney);
        return res;

    }

    @Override
    public Page<UnitBO> getAllUnitsHistoryForPage(Pageable pageable) {
        Page<Unit> list = unitRepository.findAll(pageable);
        List<UnitBO> res = new ArrayList<>();
        for (Unit unit : list) {
            int adminNum = unit.getAdministrator().size();
            JobStatus[] statuses = {JobStatus.FINISH, JobStatus.CLOSED};
            Long temp = jobService.getJobs(unit, statuses, null, null, null).getTotalElements();
            int hiredNum = applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING);
            UnitBO bo = new UnitBO(unit);
            bo.setHireNum(hiredNum);
            bo.setAdminNum(adminNum);
            bo.setJobNum(temp.intValue());
            res.add(bo);
        }
        return new PageImpl<UnitBO>(res, pageable, list.getTotalElements());
    }


}
