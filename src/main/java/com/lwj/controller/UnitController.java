package com.lwj.controller;

import com.lwj.annotation.UserRight;
import com.lwj.bo.UnitBO;
import com.lwj.bo.UnitForEvaluation;
import com.lwj.data.ResponseData;
import com.lwj.entity.*;
import com.lwj.exception.WSPException;
import com.lwj.service.IUnitService;
import com.lwj.service.IUserService;
import com.lwj.status.*;
import com.lwj.status.ErrorInfo;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.lwj.status.StaticParams.PAGE_SIZE;

/**
 * Created by liwj0 on 2017/7/18.
 */
@RestController
@RequestMapping(value = "/units")
public class UnitController {

    @Autowired
    private IUnitService unitService;

    @Autowired
    private IUserService userService;

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/evaluation", method = RequestMethod.GET)
    public ResponseData getUnitsForEvaluation(Integer year, Integer month,
                                              @PageableDefault(value = PAGE_SIZE, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) throws WSPException {
        if (year == null || month == null) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        Page<UnitForEvaluation> res = unitService.getUnitsForEvaluation(year, month, pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/close", method = RequestMethod.PUT)
    public ResponseData closeUnit(String token, @RequestParam(value = "id") Long unitId) throws WSPException {
        if (unitId == null || unitId <= 0) {
            throw new WSPException(ErrorInfo.PARAMS_ERROR);
        }
        User user = userService.getUserByToken(token);
        unitService.closeUnit(user, unitId);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(null);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseData create(@ModelAttribute Unit unit) {
        if (unit.getName() != null && unit.getLimitOfPeople() > 0 && unit.getMonthlySalaryCap() > 0) {
            try {
                unitService.create(unit);
                ResponseData responseData = new ResponseData();
                responseData.setSuccessData(unit.getId());
                return responseData;
            } catch (WSPException e) {
                ResponseData responseData = new ResponseData();
                responseData.setFail(e.getStatus());
                return responseData;
            }
        } else {
            ResponseData responseData = new ResponseData();
            responseData.setFail(ErrorInfo.PARAMS_ERROR);
            return responseData;
        }
    }

    @UserRight(authorities = Rights.SCHOOL)
    @RequestMapping(value = "/{unitId}", method = RequestMethod.PUT)
    public ResponseData update(@PathVariable long unitId, @ModelAttribute Unit unit) {
        if (unitId > 0 && unit.getName() != null && unit.getLimitOfPeople() > 0 && unit.getMonthlySalaryCap() > 0) {
            unitService.update(unit, unitId);
            ResponseData responseData = new ResponseData();
            responseData.setSuccessData(null);
            return responseData;
        } else {
            ResponseData responseData = new ResponseData();
            responseData.setFail(ErrorInfo.PARAMS_ERROR);
            return responseData;
        }
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/authorize", method = RequestMethod.DELETE)
    public ResponseData unAuthorize(String userName) {
        if (userName == null || "".equals(userName)) {
            ResponseData responseData = new ResponseData();
            responseData.setFail(ErrorInfo.PARAMS_ERROR);
            return responseData;
        } else {
            unitService.unAuthorizeAdmin(userName);
            ResponseData responseData = new ResponseData();
            responseData.setSuccessData(null);
            return responseData;
        }
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/authorize", method = RequestMethod.PUT)
    public ResponseData authorize(String token, @RequestParam(value = "name") String userName, @RequestParam(value = "unitId") Long unitId,
                                  @RequestParam(value = "userType") Integer userType) {
        if (userName == null || "".equals(userName) || unitId == null || unitId <= 0 || userType == null) {
            ResponseData responseData = new ResponseData();
            responseData.setFail(ErrorInfo.PARAMS_ERROR);
            return responseData;
        } else {
            User user = userService.getUserByToken(token);
            ICPOAuth icpoAuth = new ICPOAuth();
            icpoAuth.setUrl(CommonVariable.oauthServer);
            icpoAuth.setAccessToken(user.getApiToken());

            unitService.authorizeAdmin(icpoAuth, userName, userType, unitId);

            ResponseData responseData = new ResponseData();
            responseData.setSuccessData(null);
            return responseData;

        }

    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseData getUnits(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UnitBO> res = unitService.getAllUnitsForPage(pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public ResponseData getUnitsHistory(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UnitBO> res = unitService.getAllUnitsHistoryForPage(pageable);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT})
    @RequestMapping(value = "/excellent_num", method = RequestMethod.GET)
    public ResponseData getExcellentNum(String token,
                                        @RequestParam(value = "year") Integer year,
                                        @RequestParam(value = "month") Integer month) {
        User user = userService.getUserByToken(token);
        Unit unit = user.getUnit();

        HashMap<String, Object> res = unitService.getExcellentNum(unit, year, month);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseData getAllUnits() {
        List<UnitBO> res = unitService.getAllUnits();
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/{id}/administrators", method = RequestMethod.GET)
    public ResponseData getAdministrators(@PathVariable long id) {
        List<User> res = unitService.getAdministrators(id);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }

}
