package com.dongjian.framwork.gson;

public class TokenBean {

    /**
     * "code":200,"userId":"adfc4f4019","token":"8bqzo8Pb67rxa2sFcwKedUMR8lsJ8SKvSjV4Rjy2Fys=@pwbt.cn.rongnav.com;pwbt.cn.rongcfg.com"
     */

    private int code;
    private String userId;
    private String token;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

