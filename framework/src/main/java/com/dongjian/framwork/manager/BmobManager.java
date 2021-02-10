package com.dongjian.framwork.manager;

import android.content.Context;

import cn.bmob.v3.Bmob;

/**
 * Bmob管理类
 * 1、单例设计模式
 */
public class BmobManager {

    private static final String BMOB_SDK_ID = "fe48a541b5d83a582790189443337933";

    //1、单例设计模式
    private static volatile BmobManager mInstance = null;

    private BmobManager(){

    }

    public static BmobManager getmInstance(){
        if(mInstance == null){
            synchronized(BmobManager.class){
                if(mInstance == null){
                    mInstance = new BmobManager();
                }
            }
        }
        return mInstance;
    }

    //初始化Bmob
    public void initBmob(Context mContent){
        Bmob.initialize(mContent,BMOB_SDK_ID);
    }

}
