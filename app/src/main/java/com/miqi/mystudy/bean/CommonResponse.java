package com.miqi.mystudy.bean;

/**
 * Created by aoxuqiang on 2017/12/29.
 */

public class CommonResponse {
    public int code;
    public String msg;

    @Override
    public String toString() {
        return "CommonResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
