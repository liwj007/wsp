package com.lwj.filter;

import com.lwj.annotation.UserRight;
import com.lwj.entity.User;
import com.lwj.exception.WSPException;
import com.lwj.service.IUserService;
import com.lwj.status.CommonVariable;
import com.lwj.status.ErrorInfo;
import com.lwj.status.Rights;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by liwj0 on 2017/7/28.
 */
@Aspect   //定义一个切面
@Configuration
public class AuthenticationAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IUserService userService;

    @Pointcut("execution(* com.lwj.controller..*.*(..))")
    public void executeService() {

    }

    @Around("executeService()")
    public Object doAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        if (targetMethod.getName().equals("getAccessToken")
                || targetMethod.getName().equals("getVersion")) {
            return joinPoint.proceed();
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        String token = request.getParameter("token");


        if (token != null && !"".equals(token)) {

            User user = userService.getUserByToken(token);
            if (user == null) {
                throw new WSPException(ErrorInfo.NO_LOGIN);
            }

            UserRight userRight = targetMethod.getAnnotation(UserRight.class);
            if (userRight == null) {
                Date date = new Date();
                user.setExpire(date.getTime() + CommonVariable.tokenOutPeriod);
                userService.updateUser(user);
                return joinPoint.proceed();
            }


            Date now = new Date();
            if (now.getTime() > user.getExpire()) {
                throw new WSPException(ErrorInfo.TOKEN_EXP);
            }

            if (user != null) {
                if (userRight.authorities().length > 0) {
                    int[] authorities = userRight.authorities();
                    Set<Integer> authSet = new HashSet<>();
                    for (int authority : authorities) {
                        authSet.add(authority);
                    }


                    Integer role = user.getUserRight();

                    if (authSet.contains(Rights.UNIT) && role == Rights.UNIT) {
                        if (user.getUnit() == null) {
                            throw new WSPException(ErrorInfo.NO_RIGHT);
                        }
                    }

                    if (role != null) {
                        if (authSet.contains(role)) {
                            Date date = new Date();
                            user.setExpire(date.getTime() + CommonVariable.tokenOutPeriod);
                            userService.updateUser(user);
                            return joinPoint.proceed();
                        }
                    }
                    throw new WSPException(ErrorInfo.NO_RIGHT);
                }
                Date date = new Date();
                user.setExpire(date.getTime() + CommonVariable.tokenOutPeriod);
                userService.updateUser(user);
                return joinPoint.proceed();
            }
        } else {
            throw new WSPException(ErrorInfo.NO_LOGIN2);
        }

        throw new WSPException(ErrorInfo.NO_RIGHT);
    }
}

//        System.out.println("我是前置通知!!!");
//                //获取目标方法的参数信息
//                Object[] obj = joinPoint.getArgs();
//                //AOP代理类的信息
//                joinPoint.getThis();
//                //代理的目标对象
//                joinPoint.getTarget();
//                //用的最多 通知的签名
//                Signature signature = joinPoint.getSignature();
//                //代理的是哪一个方法
//                System.out.println(signature.getName());
//                //AOP代理类的名字
//                System.out.println(signature.getDeclaringTypeName());
//                //AOP代理类的类（class）信息
//                signature.getDeclaringType();
//                //获取RequestAttributes
//                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//                //从获取RequestAttributes中获取HttpServletRequest的信息
//                HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
//                //如果要获取Session信息的话，可以这样写：
//                //HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
//                Enumeration<String> enumeration = request.getParameterNames();
//        while (enumeration.hasMoreElements()) {
//        String parameter = enumeration.nextElement();
//        System.out.println(parameter+","+request.getParameter(parameter));


