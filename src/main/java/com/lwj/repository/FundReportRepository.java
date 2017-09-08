package com.lwj.repository;

import com.lwj.entity.*;
import com.lwj.status.EvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by liwj0 on 2017/7/24.
 */
public interface FundReportRepository extends JpaRepository<FundReport, Long>, JpaSpecificationExecutor<FundReport> {

}
