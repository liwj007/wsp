package com.lwj.service;

import com.lwj.bo.UnitBO;
import com.lwj.bo.UnitForEvaluation;
import com.lwj.entity.Unit;
import com.lwj.entity.User;
import com.lwj.exception.WSPException;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/25.
 */
public interface IUnitService {
    Page<UnitForEvaluation> getUnitsForEvaluation(Integer year, Integer month, Pageable pageable);

    void create(Unit unit) throws WSPException;

    void update(Unit unit, Long id);

    void unAuthorizeAdmin(String userName);

    void authorizeAdmin(ICPOAuth icpoAuth, String userName, Integer userType, Long unitId);

    List<UnitBO> getAllUnits();

    Page<UnitBO> getAllUnitsForPage(Pageable pageable);

    UnitBO getUnitById(Long id);

    Unit getOriginUnitById(Long id);

    List<User> getAdministrators(Long id);

    void closeUnit(User user, Long unitId) throws WSPException;

    HashMap<String,Object> getExcellentNum(Unit unit, Integer year,Integer month);

    Page<UnitBO> getAllUnitsHistoryForPage(Pageable pageable);
}
