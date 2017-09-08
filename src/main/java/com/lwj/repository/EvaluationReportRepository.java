package com.lwj.repository;

import com.lwj.entity.Application;
import com.lwj.entity.EvaluationReport;
import com.lwj.entity.Unit;
import com.lwj.status.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by liwj0 on 2017/7/25.
 */
public interface EvaluationReportRepository extends JpaRepository<EvaluationReport, Long>, JpaSpecificationExecutor<EvaluationReport> {
    EvaluationReport findByUnitAndYAndM(Unit unit, Integer year, Integer month);

    List<EvaluationReport> findByReportStatusAndYAndM(ReportStatus status, Integer year, Integer month);
}
