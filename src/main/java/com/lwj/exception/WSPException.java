package com.lwj.exception;

import com.lwj.status.ErrorInfo;

/**
 * Created by liwj0 on 2017/7/26.
 */
public class WSPException extends Exception {
    private Integer errorCode;
    private ErrorInfo status;

    public WSPException(ErrorInfo status) {
        super(ErrorInfo.getMsgByCode(status.code));
        this.status = status;
        this.errorCode = status.code;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorInfo getStatus() {
        return status;
    }

    public void setStatus(ErrorInfo status) {
        this.status = status;
    }
}
