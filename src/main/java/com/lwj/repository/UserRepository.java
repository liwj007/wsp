package com.lwj.repository;

import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.status.Rights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by liwj0 on 2017/7/17.
 */
@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    User findByUserName(String userName);

    User findByToken(String token);

    @Modifying
    @Query("update User u set u.unit = ?2 where u.userName = ?1 and u.userRight = ?3")
    int modifyUnitId(String userName, Unit unit, int right);
}
