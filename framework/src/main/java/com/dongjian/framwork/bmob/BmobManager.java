package com.dongjian.framwork.bmob;

import android.content.Context;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Bmob管理类：主要就是包装一下Bmob支持的方法
 * 1、单例设计模式
 */
public class BmobManager {

//    private static final String BMOB_SDK_ID = "fe48a541b5d83a582790189443337933"; //自己的key
    private static final String BMOB_SDK_ID = "f8efae5debf319071b44339cf51153fc";

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
     * 判断Bmob中user的登录状态
     * @return
     */
    public boolean isLogin(){
        return BmobUser.isLogin();
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

    /**
     * 更新用户第一次上传到头像和昵称
     * 1、上传文件，拿到地址
     * 2、更新用户信息
     * @param file
     * @param nickName
     */
    public void uploadFirstPhotoAndNickname(File file , final String nickName, final OnUploadPhotoListener onUploadPhotoListener){
        final IMUser imUser = getUser();
        //上传文件
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                //上传成功
                if(e == null){
                    //更新用户信息
                    imUser.setNickName(nickName);
                    imUser.setPhoto(bmobFile.getUrl());
                    imUser.setTokenNickName(nickName);
                    imUser.setTokenPhoto(bmobFile.getFileUrl());
                    imUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                onUploadPhotoListener.OnUpdateDone();
                            } else {
                                onUploadPhotoListener.OnUpdateFail(e);
                            }
                        }
                    });
                }
            }
        });
    }

    //让外部能拿到用户更新的结果，进行对应的操作
    public interface OnUploadPhotoListener {

        void OnUpdateDone();

        void OnUpdateFail(BmobException e);
    }

}
