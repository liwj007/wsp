package com.lwj.service;

import com.lwj.entity.Evaluation;
import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.exception.WSPException;
import com.lwj.status.EvaluationStatus;
import com.lwj.status.WorkAttitude;
import com.lwj.status.WorkResult;
import com.lwj.status.Workload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/26.
 */
public interface IEvaluationService {
    void passEvaluation(Long id, double money) throws WSPException;

    void submitToSchool(Long reportId, User unitAdmin, String name, String remark, Integer year, Integer month) throws WSPException;

    HashMap<String, Object> getReportEvaluationsForCheckForPage(Long unitId, Integer year, Integer month, Pageable pageable);

    HashMap<String, Object> getReportEvaluationsForPage(Unit unit, Integer year, Integer month, Pageable pageable);

    void checkEvaluation(Long id, Long jobId, Workload workload, WorkAttitude workAttitude, WorkResult workResult, String assessment, Double money, Integer year, Integer month) throws WSPException;

    void createEvaluation(Long appId, Integer year, Integer month, String content) throws WSPException;

    Page<Evaluation> getMyEvaluationForPage(User user, Pageable pageable);

    Page<Evaluation> getMyPassEvaluationForPage(User user, Pageable pageable);

    HashMap<String,Object> getFundList(Integer year, Integer month, Pageable pageable);

    void fundRelease(String name, String remark, Integer year, Integer month);

    List<Evaluation> getExportRecords(Integer year, Integer month, EvaluationStatus status);

    void rejectAllEvaluationofUnit(Long unitId, Integer year, Integer month);

    String hasEvaluation(Long jobId, Integer year, Integer month) throws WSPException;

    boolean canSubmitToSchool(Unit unit, Integer year, Integer month);

    List<Evaluation> getExportRecords(Unit unit, Integer year, Integer month, EvaluationStatus pass);

    void passAllEvaluationOfUnit(Long unitId, Integer year, Integer month);
}
