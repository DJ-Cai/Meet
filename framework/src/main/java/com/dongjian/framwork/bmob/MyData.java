package com.dongjian.framwork.bmob;

import cn.bmob.v3.BmobObject;

public class MyData extends BmobObject {

    private String name;
    private int sex; // 0难1女

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
