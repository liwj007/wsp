package com.lwj.controller;

import com.lwj.annotation.UserRight;
import com.lwj.bo.ICPUser;
import com.lwj.data.ResponseData;
import com.lwj.service.IUserService;
import com.lwj.status.*;
import com.lwj.status.ErrorInfo;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.ebigdata.icp.pojo.Student;
import org.ebigdata.icp.pojo.User;
import org.ebigdata.icp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.ebigdata.icp.pojo.UserType.*;

/**
 * Created by liwj0 on 2017/7/18.
 */
@RestController
@RequestMapping(value = "/icp")
public class ICPController {

    @Autowired
    private IUserService userService;

    @UserRight(authorities = {Rights.SCHOOL})
    @RequestMapping(value = "/icp_user", method = RequestMethod.GET)
    public ResponseData getICPUsers(String token, @RequestParam(value = "type") int type) {
        if (type < 1 || type > 3) {
            ResponseData responseData = new ResponseData();
            responseData.setFail(ErrorInfo.PARAMS_ERROR);
            return responseData;
        }
        com.lwj.entity.User user = userService.getUserByToken(token);
        String apiToken = user.getApiToken();


        UserService icpUserService = new UserService();
        List<? extends User> res = new ArrayList<>();

        ICPOAuth icpoAuth = new ICPOAuth();
        icpoAuth.setUrl(CommonVariable.oauthServer);
        icpoAuth.setAccessToken(apiToken);

        switch (type) {
            case (SCHOOL_TYPE):
                res = icpUserService.getSchoolUsers(icpoAuth);
                break;
            case (COLLEGE_TYPE):
                res = icpUserService.getMyCollegeUserList(icpoAuth);
                break;
            case (INSTRUCTOR_TYPE):
                res = icpUserService.getMyInstructorList(icpoAuth);
                break;
        }
        List<ICPUser> list = new ArrayList<>();
        if (res != null && res.size() > 0) {
            for (User item : res) {
                com.lwj.entity.User tmp = userService.getUserByName(item.getUserName());
                if (tmp != null) {
                    list.add(new ICPUser(tmp));
                } else {
                    list.add(new ICPUser(item));
                }
            }
        }

        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(list);
        return responseData;
    }

    @UserRight(authorities = {Rights.UNIT, Rights.SCHOOL})
    @RequestMapping(value = "/get_student/{no}", method = RequestMethod.GET)
    public ResponseData getStudentByNo(String token, @PathVariable String no) {
        if (no == null || "".equals(no)) {
            ResponseData responseData = new ResponseData();
            responseData.setFail(ErrorInfo.PARAMS_ERROR);
            return responseData;
        }

        com.lwj.entity.User user = userService.getUserByToken(token);
        String apiToken = user.getApiToken();

        ICPOAuth icpoAuth = new ICPOAuth();
        icpoAuth.setUrl(CommonVariable.oauthServer);
        icpoAuth.setAccessToken(apiToken);

        UserService icpUserService = new UserService();
        User res = icpUserService.getUserDetailInfomation(icpoAuth, no, STUDENT_TYPE);

        userService.addNewUser(new com.lwj.entity.User((Student) res), null, Rights.STUDENT);

        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }
}
