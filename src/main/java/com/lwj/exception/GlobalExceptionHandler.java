package com.lwj.exception;

import com.lwj.data.ResponseData;
import com.lwj.status.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liwj0 on 2017/7/8.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("", e);
        ResponseData r = new ResponseData();
        r.setMessage(e.getMessage());
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            r.setCode(404);
            r.setMessage("无法找到指定的请求链接");
        } else  if (e instanceof WSPException) {
            r.setCode(((WSPException) e).getErrorCode());
            r.setMessage(e.getMessage());
        } else if (e instanceof BindException) {
            r.setCode(ErrorInfo.PARAMS_ERROR.code);
            r.setMessage(ErrorInfo.PARAMS_ERROR.desc);
        } else {
            if (e.getCause() instanceof WSPException){
                WSPException tmp = (WSPException) e.getCause();
                r.setCode(tmp.getErrorCode());
                r.setMessage(tmp.getMessage());
            }else{
                r.setCode(500);
                r.setMessage(e.getMessage());
            }
        }
        r.setData(null);
        r.setStatus(false);
        return r;
    }
}
