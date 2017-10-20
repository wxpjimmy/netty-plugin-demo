package com.jimmy.ratelimit.data;

/**
 * Created by wangxiaopeng on 2017/10/20.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class ErrorData {
    private String errMsg;
    private TestData testData;

    public ErrorData(String errMsg, TestData testData) {
        this.errMsg = errMsg;
        this.testData = testData;
    }

    @Override
    public String toString() {
        return "ErrorData{" +
                "errMsg='" + errMsg + '\'' +
                ", testData=" + testData +
                '}';
    }
}
