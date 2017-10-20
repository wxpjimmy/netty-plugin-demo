package com.jimmy.ratelimit.data;

/**
 * Created by wangxiaopeng on 2017/10/20.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class TestData {
    private int age;
    private String name;

    public TestData(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return this.age;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "{" + name + ": " + age + "}";
    }
}
