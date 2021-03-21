package com.dongjian.framwork;

import android.content.Context;

//import androidx.multidex.MultiDex;

import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.cloud.CloudManager;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.SpUtils;

import org.litepal.LitePal;

/**
 * Framework入口--单例设计模式：DCL
 */
public class Framework {

    private volatile static Framework mFramwork;

    private Framework(){

    }

    public static Framework getFramework(){
        if(mFramwork == null){
            synchronized (Framework.class){
                if(mFramwork == null){
                    mFramwork = new Framework();
                }
            }
        }
        return mFramwork;
    }

    public void initFramework(Context mContext){
        LogUtils.i("initFramework");
        SpUtils.getInstance().initSp(mContext);
        BmobManager.getmInstance().initBmob(mContext);
        CloudManager.getInstance().initCloud(mContext);
        LitePal.initialize(mContext);
    }

}
