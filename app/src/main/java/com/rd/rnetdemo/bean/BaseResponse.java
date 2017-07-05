package com.rd.rnetdemo.bean;

/**
 * Created by ruand on 2017/6/28.
 */

public class BaseResponse<T> {

    private String resultcode;
    private String reason;
    private T result;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
