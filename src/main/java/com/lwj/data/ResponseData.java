package com.lwj.data;

import com.lwj.status.ErrorInfo;

/**
 * Created by liwj0 on 2017/7/8.
 */
public class ResponseData {
    private int code;
    private boolean status;
    private String message;
    private Object data;


    public void setSuccessData(Object data) {
        this.code = 200;
        this.status = true;
        this.data = data;
    }

    public void setFail(ErrorInfo status) {
        this.code = status.code;
        this.message = ErrorInfo.getMsgByCode(code);
        this.status = false;
    }

//    public void setFail(int code, String message) {
//        this.code = code;
//        this.message = message;
//        this.status = false;
//    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
