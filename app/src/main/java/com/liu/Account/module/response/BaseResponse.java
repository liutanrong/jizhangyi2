package com.liu.Account.module.response;

/**
 * Created by tanrong on 16/10/3.
 */
public class BaseResponse {
    private Integer exeSuccess;
    private String errorMsg;

    public Integer getExeSuccess() {
        return exeSuccess;
    }

    public void setExeSuccess(Integer exeSuccess) {
        this.exeSuccess = exeSuccess;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
