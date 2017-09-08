package com.lwj.repository;

import com.lwj.entity.Job;
import com.lwj.entity.Unit;
import com.lwj.status.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by liwj0 on 2017/7/18.
 */
public interface JobRepository extends JpaRepository<Job, Long> , JpaSpecificationExecutor<Job> {
    List<Job> findByUnit(Unit unit);

    List<Job> findByUnitAndStatus(Unit unit, JobStatus status);
}
