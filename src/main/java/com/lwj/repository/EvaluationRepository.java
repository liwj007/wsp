package com.lwj.repository;

import com.lwj.bo.EvaluationTemp;
import com.lwj.entity.*;
import com.lwj.status.EvaluationStatus;
import com.lwj.status.FundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.HashMap;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/24.
 */
public interface EvaluationRepository extends JpaRepository<Evaluation, Long>, JpaSpecificationExecutor<Evaluation> {
    Evaluation findByUserAndJobAndYAndM(User user, Job job, Integer year, Integer month);

    Evaluation findByApplicationAndYAndM(Application application, Integer year, Integer month);

    Page<Evaluation> findByYAndMAndStatus(Integer year, Integer month, EvaluationStatus status, Pageable pageable);

    Page<Evaluation> findByYAndMAndStatusAndFundStatus(Integer year, Integer month, EvaluationStatus status, FundStatus fundStatus, Pageable pageable);

    Page<Evaluation> findByEvaluationReportIn(List<EvaluationReport> report, Pageable pageable);

    List<Evaluation> findByEvaluationReportIn(List<EvaluationReport> report);

    @Query("select new com.lwj.bo.EvaluationTemp(count(distinct t.job),count(distinct t.unit),count(distinct t.user),sum(t.realMoney)) from Evaluation t where t.y = ?1 and t.m = ?2 and t.status = ?3 and t.fundStatus = ?4")
    EvaluationTemp findReportNumber(Integer year, Integer month, EvaluationStatus status, FundStatus fundStatus);



    List<Evaluation> findByYAndMAndStatus(Integer year, Integer month, EvaluationStatus status);

    Integer countByJobAndStatus(Job job, EvaluationStatus status);

    Integer countByJobAndFundStatus(Job job, FundStatus status);

    List<Evaluation> findByYAndMAndStatusAndFundStatus(Integer year, Integer month, EvaluationStatus status, FundStatus fundStatus);


    @Query("select count(t.id) from Evaluation t where t.job = ?1 and t.y = ?2 and t.m = ?3 and t.status = ?4")
    Long findNumberByJobAndYAndMAndStatus(Job job, Integer year, Integer month, EvaluationStatus status);

    @Query("select t from Evaluation t where t.job = ?1 and t.y = ?2 and t.m = ?3 and t.status >= ?4")
    List<Evaluation> findReportEvaluations(Job job, Integer year, Integer month, EvaluationStatus status);

    @Query("select count(t.id) from Evaluation t where t.job = ?1 and t.y = ?2 and t.m = ?3 and t.status >= ?4")
    Long statisticNumberReportEvaluation(Job job, Integer year, Integer month, EvaluationStatus status);

    @Query("select sum(t.realMoney) from Evaluation t where t.job = ?1 and t.y = ?2 and t.m = ?3 and t.status >= ?4")
    Double statisticMoneyReportEvaluation(Job job, Integer year, Integer month, EvaluationStatus status);

    @Query("select t from Evaluation t where t.unit = ?1 and t.y = ?2 and t.m = ?3 and t.status >= ?4")
    List<Evaluation> findReportedByUnitAndYAndMAndStatus(Unit unit, Integer year, Integer month, EvaluationStatus status);

    List<Evaluation> findByUnitAndYAndMAndStatus(Unit unit, Integer year, Integer month, EvaluationStatus status);

    List<Evaluation> findByUnitAndYAndM(Unit unit, Integer year, Integer month);

    @Query("select count(distinct t.user) from  Evaluation t where t.unit = ?1 and t.y = ?2 and t.m = ?3 and t.workResult = 3")
    Long findExcellentNumByUnitAndYAndM(Unit unit, Integer year, Integer month);

    @Query("select sum(t.realMoney) from  Evaluation t where t.unit = ?1 and t.y = ?2 and t.m = ?3 and t.status in (1,2,3)")
    Double findCurrentMoneyByUnitAndYAndM(Unit unit, Integer year, Integer month);

    @Query("select count(t.id) from Evaluation t where t.unit = ?1 and t.y = ?2 and t.m = ?3 and t.status >= ?4")
    Integer findReportNumber(Unit unit, Integer year, Integer month, EvaluationStatus status);

    @Query("select count(t.id) from Evaluation t where t.unit = ?1 and t.y = ?2 and t.m = ?3 and t.status = ?4 and t.status < 4")
    Integer findPassNumber(Unit unit, Integer year, Integer month, EvaluationStatus status);

    @Query("select sum(t.realMoney) from Evaluation t where t.unit = ?1 and t.y = ?2 and t.m = ?3 and t.status >= ?4")
    Double findReportMoney(Unit unit, Integer year, Integer month, EvaluationStatus status);
}
