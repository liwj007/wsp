package com.lwj.repository;

import com.lwj.bo.UnitBO;
import com.lwj.entity.Unit;
import com.lwj.status.UnitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;


/**
 * Created by liwj0 on 2017/7/18.
 */
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByName(String name);

    @Query("select new com.lwj.bo.UnitBO(t.id,t.name,t.limitOfPeople,t.monthlySalaryCap,t.remark,t.status, COUNT(distinct a.applicant), COUNT(distinct b.id), COUNT(distinct c.id)) from Unit t left join t.applications a on a.applicationStatus = 2 left join t.administrator b  left join t.jobList c on c.status in (1,3,4,5) where  t.status=0 group by t.id")
    Page<UnitBO> findALLUnitsWithStatistic(Pageable pageable);

    @Query("select new com.lwj.bo.UnitBO(t.id,t.name,t.limitOfPeople,t.monthlySalaryCap,t.remark,t.status, COUNT(distinct a.applicant), COUNT(distinct b.id), COUNT(distinct c.id)) from Unit t left join t.applications a on  a.applicationStatus = 2 left join t.administrator b  left join t.jobList c on c.status in (1,3,4,5) where  t.status=0  group by t.id")
    List<UnitBO> findALLUnitsWithStatistic();

    Page<Unit> findAllByStatusNot(UnitStatus status,Pageable pageable);

    Page<Unit> findAllByStatusNotAndCreateDateLessThan(UnitStatus status, Date date,Pageable pageable);

    List<Unit> findAllByStatusNot(UnitStatus status);
}
