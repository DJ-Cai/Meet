package com.dongjian.framwork.db;

import org.litepal.crud.LitePalSupport;

/**
 * 新朋友的类 --- 用在保存到到litepal中
 */
public class NewFriend extends LitePalSupport {

    //留言
    private String msg;
    //对方的id
    private String id;
    //根据时间排序
    private long saveTime;
    //状态：待确认、接收、拒绝(-1、0、1)
    private int isAgree = -1;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(int isAgree) {
        this.isAgree = isAgree;
    }
}
