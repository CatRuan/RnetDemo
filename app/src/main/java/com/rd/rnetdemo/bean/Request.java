package com.rd.rnetdemo.bean;

/**
 * Created by ruand on 2017/6/28.
 */

public class Request {
    private int phone;
    private String key;
    private String dtype;

    public Request(int phone, String dtype, String key) {
        this.phone = phone;
        this.dtype = dtype;
        this.key = key;
    }

    public Request() {

    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
