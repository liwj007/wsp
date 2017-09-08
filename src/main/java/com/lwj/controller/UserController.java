package com.lwj.controller;

import com.lwj.annotation.UserRight;
import com.lwj.data.ResponseData;
import com.lwj.entity.User;
import com.lwj.service.IUserService;
import com.lwj.status.CommonVariable;
import com.lwj.status.Rights;
import org.ebigdata.icp.pojo.ICPOAuth;
import org.ebigdata.icp.pojo.Message;
import org.ebigdata.icp.pojo.Student;
import org.ebigdata.icp.pojo.UserType;
import org.ebigdata.icp.service.OAuthService;
import org.ebigdata.icp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liwj0 on 2017/7/27.
 */
@RestController
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseData allUsers() throws IOException {
        List<User> users = userService.getAll();
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(users);
        return responseData;
    }


    @UserRight(authorities = {})
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseData logout(String token) throws IOException {
        User user = userService.getUserByToken(token);
        userService.logout(user);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(CommonVariable.oauthServer);
        return responseData;
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public ResponseData getVersion(String token) throws IOException {
        String version = CommonVariable.VERSION;
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(version);
        return responseData;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void getAccessToken(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getParameter("code");

            Message msg = new OAuthService().getAccessToken(CommonVariable.oauthServer,
                    CommonVariable.clientId, CommonVariable.clientSecret, code);
            String accessToken = null;
            ICPOAuth icpoAuth = new ICPOAuth();
            if (msg != null && msg.getState() == 0) {
                accessToken = msg.getContentValue("access_token").toString();

                icpoAuth.setAccessToken(accessToken);
                icpoAuth.setUrl(CommonVariable.oauthServer);

                UserService icpUserService = new UserService();

                int userType = icpUserService.getUserRoleType(icpoAuth);
                org.ebigdata.icp.pojo.User user;
                int userRight = Rights.NEW;
                switch (userType) {
                    case UserType.SCHOOL_TYPE:
                        user = icpUserService.getSchoolUser(icpoAuth);
                        userRight = Rights.UNIT;
                        break;
                    case UserType.COLLEGE_TYPE:
                        user = icpUserService.getCollegeUser(icpoAuth);
                        userRight = Rights.UNIT;
                        break;
                    case UserType.INSTRUCTOR_TYPE:
                        user = icpUserService.getInstructorUser(icpoAuth);
                        userRight = Rights.UNIT;
                        break;
                    case UserType.PRODUCT_ADMIN_TYPE:
                        user = icpUserService.getSchoolUser(icpoAuth);
                        userRight = Rights.SCHOOL;
                        break;
                    case UserType.STUDENT_TYPE:
                        user = icpUserService.getStudentUser(icpoAuth);
                        userRight = Rights.STUDENT;
                        break;
                    default:
                        throw new Exception("获取用户角色失败");
                }

//                session.setAttribute("token", accessToken);

                //新增登录信息
                String token;
                if (user instanceof Student) {
                    token = userService.addNewUser(new User((Student) user), accessToken, userRight);
                } else {
                    token = userService.addNewUser(new User(user), accessToken, userRight);
                }


//                Cookie cookie = new Cookie("token", accessToken);
                Cookie cookie = new Cookie("token", token);
                cookie.setMaxAge(30 * 60);
                cookie.setPath("/");
                response.addCookie(cookie);

                response.sendRedirect(CommonVariable.wspServer);

//                response.sendRedirect(CommonVariable.wspServer + "?api_url=" + CommonVariable.apiServer);

            } else {
                response.sendRedirect(CommonVariable.oauthServer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @UserRight(authorities = {})
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseData getUserInfo(String token) {
        String type = "";
        User user = userService.getUserByToken(token);

        switch (user.getUserRight()) {
            case Rights.NEW:
                type = "";
                break;
            case Rights.UNIT:
                type = "unit";
                break;
            case Rights.SCHOOL:
                type = "school";
                break;
            case Rights.STUDENT:
                type = "student";
                break;
        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("user", user);
        res.put("type", type);
        ResponseData responseData = new ResponseData();
        responseData.setSuccessData(res);
        return responseData;
    }
}
