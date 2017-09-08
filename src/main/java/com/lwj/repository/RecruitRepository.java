package com.lwj.repository;

import com.lwj.entity.Job;
import com.lwj.entity.Recruit;
import com.lwj.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/18.
 */
public interface RecruitRepository extends JpaRepository<Recruit, Long> , JpaSpecificationExecutor<Recruit> {
    @Modifying
    @Query("update Recruit u set u.endDate = ?2 where u.job = ?1 and u.endDate is null ")
    void endHire(Job job, Date endDate);

}
