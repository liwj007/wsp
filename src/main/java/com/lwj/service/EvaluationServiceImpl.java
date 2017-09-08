package com.lwj.service;

import com.lwj.bo.EvaluationTemp;
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
import java.util.*;

/**
 * Created by liwj0 on 2017/7/26.
 */
@Service
public class EvaluationServiceImpl implements IEvaluationService {
    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EvaluationReportRepository reportRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private FundReportRepository fundReportRepository;

    @Transactional
    public void passEvaluation(Long id, double money) throws WSPException {
        Evaluation evaluation = evaluationRepository.findOne(id);
        if (evaluation == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        evaluation.setRealMoney(money);
        evaluation.setStatus(EvaluationStatus.PASS);
        evaluationRepository.save(evaluation);

        int size = evaluationRepository.findByUnitAndYAndMAndStatus(evaluation.getUnit(), evaluation.getY(), evaluation.getM(), EvaluationStatus.REPORTED).size();
        if (size == 0) {
            EvaluationReport report = reportRepository.findByUnitAndYAndM(evaluation.getUnit(), evaluation.getY(), evaluation.getM());
            report.setReportStatus(ReportStatus.PASS);
            reportRepository.save(report);
        }
    }

    @Transactional
    public void submitToSchool(Long reportId, User unitAdmin, String name, String remark, Integer year, Integer month) throws WSPException {
        Unit unit = unitAdmin.getUnit();
        if (unit == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        EvaluationReport report;
        if (reportId != null) {
            report = reportRepository.findOne(reportId);
            if (report == null) {
                throw new WSPException(ErrorInfo.PARAMS_ERROR);
            }
        } else {
            report = new EvaluationReport();
        }
        report.setName(name);
        report.setRemark(remark);
        report.setReportStatus(ReportStatus.NONE);
        report.setY(year);
        report.setM(month);
        report.setUnit(unit);
        reportRepository.save(report);


        List<Evaluation> evaluations = evaluationRepository.findByUnitAndYAndMAndStatus(unit, year, month, EvaluationStatus.REPORT);

        for (Evaluation evaluation : evaluations) {
            evaluation.setStatus(EvaluationStatus.REPORTED);
            evaluation.setEvaluationReport(report);
            evaluationRepository.save(evaluation);
        }
    }

    public Page<Evaluation> getReportEvaluations(final Unit unit, final Integer year, final Integer month, Pageable pageable) {
        Page<Evaluation> res = evaluationRepository.findAll(new Specification<Evaluation>() {
            @Override
            public Predicate toPredicate(Root<Evaluation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                list.add(criteriaBuilder.equal(root.get("unit").as(Unit.class), unit));
                list.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("status").as(EvaluationStatus.class), EvaluationStatus.FAIL),
                        criteriaBuilder.equal(root.get("status").as(EvaluationStatus.class), EvaluationStatus.REPORTED),
                        criteriaBuilder.equal(root.get("status").as(EvaluationStatus.class), EvaluationStatus.PASS)
//                        criteriaBuilder.equal(root.get("status").as(EvaluationStatus.class), EvaluationStatus.REPORT)
                ));
                list.add(criteriaBuilder.equal(root.get("y").as(Integer.class), year));
                list.add(criteriaBuilder.equal(root.get("m").as(Integer.class), month));
                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageable);

        return res;
    }

    public HashMap<String, Object> getReportEvaluationsForCheckForPage(Long unitId, Integer year, Integer month, Pageable pageable) {
        Unit unit = unitRepository.findOne(unitId);
        Page<Evaluation> res = getReportEvaluations(unit, year, month, pageable);

        double moneyCap = unit.getMonthlySalaryCap();

        HashMap<String, Object> result = new HashMap<>();
        result.put("moneyCap", moneyCap);
        result.put("totalMoney", evaluationRepository.findReportMoney(unit, year, month, EvaluationStatus.REPORTED));
        result.put("reportNum", evaluationRepository.findReportNumber(unit, year, month, EvaluationStatus.REPORTED));
        result.put("hireNum", applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING));
        result.put("list", res);
        result.put("unitName", unit.getName());

        EvaluationReport report = reportRepository.findByUnitAndYAndM(unit, year, month);
        if (report != null) {
            result.put("remark", report.getRemark());
            result.put("status", report.getReportStatus());
        }
        return result;
    }

    public HashMap<String, Object> getReportEvaluationsForPage(final Unit unit, final Integer year, final Integer month, Pageable pageable) {
        Page<Evaluation> res = evaluationRepository.findAll(new Specification<Evaluation>() {
            @Override
            public Predicate toPredicate(Root<Evaluation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                list.add(criteriaBuilder.equal(root.get("unit").as(Unit.class), unit));
                list.add(criteriaBuilder.ge(root.get("status").as(Integer.class), EvaluationStatus.REPORT.code));
                list.add(criteriaBuilder.equal(root.get("y").as(Integer.class), year));
                list.add(criteriaBuilder.equal(root.get("m").as(Integer.class), month));
                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageable);

        double moneyCap = unit.getMonthlySalaryCap();

        HashMap<String, Object> result = new HashMap<>();
        result.put("moneyCap", moneyCap);
        Double tmp = evaluationRepository.findReportMoney(unit, year, month, EvaluationStatus.REPORT);
        result.put("totalMoney", tmp == null ? 0 : tmp);
        Integer tmp2 = evaluationRepository.findReportNumber(unit, year, month, EvaluationStatus.REPORT);
        result.put("reportNum", tmp == null ? 0 : tmp2);
        result.put("hireNum", applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING));
        result.put("list", res);
        result.put("jobNum", applicationRepository.getDistincJobNumberOfUnit(unit, ApplicationStatus.WORKING));

        EvaluationReport report = reportRepository.findByUnitAndYAndM(unit, year, month);
        if (report == null) {
            result.put("canSubmit", true);
        } else if (report.getReportStatus() == ReportStatus.FAIL) {
            result.put("canSubmit", true);
            result.put("reportId", report.getId());
        } else {
            result.put("canSubmit", false);
        }

        return result;
    }

    @Transactional
    public void checkEvaluation(Long id, Long appId, Workload workload, WorkAttitude workAttitude, WorkResult workResult,
                                String assessment, Double money, Integer year, Integer month) throws WSPException {
        Evaluation evaluation;
        if (id == null) {
            Application application = applicationRepository.findOne(appId);
            if (application == null) {
                throw new WSPException(ErrorInfo.PARAMS_ERROR);
            }
            evaluation = new Evaluation();

            evaluation.setApplication(application);
            evaluation.setY(year);
            evaluation.setM(month);
            evaluation.setContent("由单位管理员自动生成");
            evaluation.setJob(application.getJob());
            evaluation.setRealMoney(application.getJob().getMoney());
            evaluation.setStatus(EvaluationStatus.NONE);
            evaluation.setUnit(application.getUnit());
            evaluation.setUser(application.getApplicant());
            evaluationRepository.save(evaluation);
        } else {
            evaluation = evaluationRepository.findOne(id);
        }
        evaluation.setWorkload(workload);
        evaluation.setWorkAttitude(workAttitude);
        evaluation.setWorkResult(workResult);
        evaluation.setAssessment(assessment);
        evaluation.setRealMoney(money);
        evaluation.setStatus(EvaluationStatus.REPORT);
        evaluation.setFundStatus(FundStatus.NONE);
        evaluationRepository.save(evaluation);
    }

    @Transactional
    public void createEvaluation(Long appId, Integer year, Integer month, String content) throws WSPException {
        Application application = applicationRepository.findOne(appId);
        if (application == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        EvaluationReport report = reportRepository.findByUnitAndYAndM(application.getUnit(),year,month);
        if (report!=null && (report.getReportStatus()!=ReportStatus.FAIL)){
            throw new WSPException(ErrorInfo.UNIT_HAS_SUBMIT);
        }

        Evaluation evaluation = evaluationRepository.findByApplicationAndYAndM(application, year, month);
        if (evaluation == null) {
            evaluation = new Evaluation();
            evaluation.setApplication(application);
            evaluation.setY(year);
            evaluation.setM(month);
            evaluation.setContent(content);
            evaluation.setJob(application.getJob());
            evaluation.setRealMoney(application.getJob().getMoney());
            evaluation.setStatus(EvaluationStatus.NONE);
            evaluation.setUnit(application.getUnit());
            evaluation.setUser(application.getApplicant());
            evaluationRepository.save(evaluation);
        } else {
            throw new WSPException(ErrorInfo.JOB_EVA_REPORTED);
        }
    }

    @Override
    public Page<Evaluation> getMyEvaluationForPage(final User user, Pageable pageable) {
        Page<Evaluation> res = evaluationRepository.findAll(new Specification<Evaluation>() {
            @Override
            public Predicate toPredicate(Root<Evaluation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                list.add(criteriaBuilder.equal(root.get("user").as(User.class), user));

                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageable);

        return res;
    }

    @Override
    public Page<Evaluation> getMyPassEvaluationForPage(final User user, Pageable pageable) {

        Page<Evaluation> res = evaluationRepository.findAll(new Specification<Evaluation>() {
            @Override
            public Predicate toPredicate(Root<Evaluation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                list.add(criteriaBuilder.equal(root.get("user").as(User.class), user));
                list.add(criteriaBuilder.equal(root.get("status").as(EvaluationStatus.class), EvaluationStatus.PASS));

                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageable);

        return res;
    }

    @Override
    public HashMap<String, Object> getFundList(Integer year, Integer month, Pageable pageable) {
        List<EvaluationReport> reports = reportRepository.findByReportStatusAndYAndM(ReportStatus.PASS,year,month);
        Page<Evaluation> list = evaluationRepository.findByEvaluationReportIn(reports, pageable);

//        Page<Evaluation> list = evaluationRepository.findByYAndMAndStatusAndFundStatus(year, month, EvaluationStatus.PASS, FundStatus.NONE, pageable);
        Set<Unit> units = new HashSet<>();
        Set<Job> jobs = new HashSet<>();
        Set<User> users = new HashSet<>();
        for (Evaluation evaluation : list) {
            jobs.add(evaluation.getJob());
            units.add(evaluation.getUnit());
            users.add(evaluation.getUser());
        }

        EvaluationTemp temp = evaluationRepository.findReportNumber(year, month, EvaluationStatus.PASS, FundStatus.RELEASE);
        EvaluationTemp temp2 = evaluationRepository.findReportNumber(year, month, EvaluationStatus.PASS, FundStatus.NONE);
        HashMap<String, Object> res = new HashMap<>();
        res.put("list", list);
        res.put("unitNum", temp.getUnitNum());
        res.put("jobNum", temp.getJobNum());
        res.put("studentNum", temp.getStudentNum());
        res.put("totalMoney", temp.getTotalMoney());
        res.put("unitNum2", temp2.getUnitNum());
        res.put("jobNum2", temp2.getJobNum());
        res.put("studentNum2", temp2.getStudentNum());
        res.put("totalMoney2", temp2.getTotalMoney());
        return res;
    }

    @Override
    @Transactional
    public void fundRelease(String name, String remark, Integer year, Integer month) {
        List<EvaluationReport> reports = reportRepository.findByReportStatusAndYAndM(ReportStatus.PASS,year,month);
        List<Evaluation> list = evaluationRepository.findByEvaluationReportIn(reports);


//        List<Evaluation> list = evaluationRepository.findByYAndMAndStatusAndFundStatus(year, month, EvaluationStatus.PASS, FundStatus.NONE);

        if (list.size()==0){
            return;
        }

        FundReport fundReport = new FundReport();
        fundReport.setM(month);
        fundReport.setY(year);
        fundReport.setName(name);
        fundReport.setRemark(remark);
        fundReport.setTime(new Date());
        fundReportRepository.save(fundReport);

        for (Evaluation evaluation : list) {
            evaluation.setFundStatus(FundStatus.RELEASE);
            evaluationRepository.save(evaluation);
        }

        for (EvaluationReport report: reports){
            report.setReportStatus(ReportStatus.FUND);
            reportRepository.save(report);
        }
    }

    public List<Evaluation> getExportRecords(Integer year, Integer month, EvaluationStatus status) {
        return evaluationRepository.findByYAndMAndStatus(year, month, status);
    }

    public List<Evaluation> getExportRecords(Unit unit, Integer year, Integer month, EvaluationStatus status) {
        return evaluationRepository.findReportedByUnitAndYAndMAndStatus(unit, year, month, status);
    }

    @Override
    @Transactional
    public void passAllEvaluationOfUnit(Long unitId, Integer year, Integer month) {
        Unit unit = unitRepository.findOne(unitId);
        List<Evaluation> list = evaluationRepository.findByUnitAndYAndM(unit, year, month);
        for (Evaluation evaluation : list) {
            evaluation.setStatus(EvaluationStatus.PASS);
            evaluationRepository.save(evaluation);
        }

        EvaluationReport report = reportRepository.findByUnitAndYAndM(unit, year, month);
        report.setReportStatus(ReportStatus.PASS);
        reportRepository.save(report);
    }

    @Override
    @Transactional
    public void rejectAllEvaluationofUnit(Long unitId, Integer year, Integer month) {
        Unit unit = unitRepository.findOne(unitId);
        List<Evaluation> list = evaluationRepository.findByUnitAndYAndM(unit, year, month);
        for (Evaluation evaluation : list) {
            evaluation.setStatus(EvaluationStatus.FAIL);
            evaluationRepository.save(evaluation);
        }

        EvaluationReport report = reportRepository.findByUnitAndYAndM(unit, year, month);
        report.setReportStatus(ReportStatus.FAIL);
        reportRepository.save(report);
    }

    @Override
    public String hasEvaluation(Long appId, Integer year, Integer month) throws WSPException {
        Application application = applicationRepository.findOne(appId);
        if (application == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        Evaluation evaluation = evaluationRepository.findByApplicationAndYAndM(application, year, month);
        if (evaluation == null)
            return null;
        else
            return evaluation.getContent();
    }

    @Override
    public boolean canSubmitToSchool(Unit unit, Integer year, Integer month) {
        EvaluationReport report = reportRepository.findByUnitAndYAndM(unit, year, month);
        if (report != null && report.getReportStatus() != ReportStatus.FAIL) {
            return true;
        }
        Integer passNum = evaluationRepository.findPassNumber(unit, year, month, EvaluationStatus.REPORT);
        Integer hireNum = applicationRepository.getDuplicatedApplicationNumberOfUnit(unit, ApplicationStatus.WORKING);
//        if (hireNum==0){
//            return false;
//        }
        return passNum == hireNum;
    }
}
