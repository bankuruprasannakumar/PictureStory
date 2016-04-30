package com.picturestory.service.response;

/**
 * Created by bankuru on 21/8/15.
 */
public class ResponseData {
    private boolean success;
    private int errorCode;
    private String errorMessage;
    private String data;

    public ResponseData() {
        success = true;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
