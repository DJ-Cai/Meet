package com.dongjian.framwork.bmob;

import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Bmob管理类：主要就是包装一下Bmob支持的方法
 * 1、单例设计模式
 */
public class BmobManager {

    private static final String BMOB_SDK_ID = "fe48a541b5d83a582790189443337933";

    //1、单例设计模式
    private static volatile BmobManager mInstance = null;

    private BmobManager() {

    }

    //获得Bmob实例对象
    public static BmobManager getmInstance() {
        if (mInstance == null) {
            synchronized (BmobManager.class) {
                if (mInstance == null) {
                    mInstance = new BmobManager();
                }
            }
        }
        return mInstance;
    }

    //初始化Bmob
    public void initBmob(Context mContent) {
        Bmob.initialize(mContent,BMOB_SDK_ID);
    }

    /**
     * 获取本地对象
     * @return
     */
    public IMUser getUser(){
        return BmobUser.getCurrentUser(IMUser.class);
    }


    /**
     * 发送短信验证码
     * @param phone    指定手机号码
     * @param listener 回调
     */
    public void requestSMS(String phone, QueryListener<Integer> listener) {
        //给phone发送template内容
        BmobSMS.requestSMSCode(phone, "", listener);
    }

    /**
     * 通过手机号码注册或登录
     * @param phone    手机号码
     * @param code     短信验证码
     * @param listener 对结果的回调
     */
    public void signOrLoginByMobilePhone(String phone, String code, LogInListener<IMUser> listener) {
        BmobUser.signOrLoginByMobilePhone(phone, code, listener);
    }

}
