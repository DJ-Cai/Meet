package com.dongjian.framwork.bmob;

import cn.bmob.v3.BmobUser;

/**
 * 用户类
 */
public class IMUser extends BmobUser {
    /**
     * 将token部分和基本信息分开：因为token包含三个参数：用户ID、头像、昵称
     * 这三个获取参数的方式应该是固定的，
     * 如果用基本信息里的信息的话，用户进行修改的话，那么token也就会产生变化
     */

    //获取token的头像
    private String tokenPhoto;
    //获取token的昵称
    private String tokenNickName;

    //下面是基本信息
    //头像
    private String photo;
    //昵称
    private String nickName;
    //性别 true = 男 fasle = 女
    private boolean sex = true;
    //简介
    private String desc;
    //年龄
    private int age = 0;
    //生日
    private String brithday;
    //星座
    private String constellation;
    //爱好
    private String hobby;
    //状态
    private String status;

    public String getTokenPhoto() {
        return tokenPhoto;
    }

    public void setTokenPhoto(String tokenPhoto) {
        this.tokenPhoto = tokenPhoto;
    }

    public String getTokenNickName() {
        return tokenNickName;
    }

    public void setTokenNickName(String tokenNickName) {
        this.tokenNickName = tokenNickName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBrithday() {
        return brithday;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
