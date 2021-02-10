package com.dongjian.framwork;

import android.content.Context;

import com.dongjian.framwork.manager.BmobManager;
import com.dongjian.framwork.utils.SpUtils;

/**
 * Framework入口--单例设计模式：DCL
 */
public class Framework {

    private volatile static Framework mFramwork;

    private Framework(){

    }

    public static Framework getFramwork(){
        if(mFramwork == null){
            synchronized (Framework.class){
                if(mFramwork == null){
                    mFramwork = new Framework();
                }
            }
        }
        return mFramwork;
    }

    public void initFramework(Context mCOntext){
        SpUtils.getInstance().initSp(mCOntext);
        BmobManager.getmInstance().initBmob(mCOntext);
    }

}
