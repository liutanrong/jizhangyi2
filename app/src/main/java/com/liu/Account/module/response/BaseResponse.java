package com.liu.Account.module.response;

/**
 * Created by tanrong on 16/10/3.
 */
public class BaseResponse {
    private Integer hasError;
    private String errorMsg;

    public Integer getHasError() {
        return hasError;
    }

    public void setHasError(Integer hasError) {
        this.hasError = hasError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
