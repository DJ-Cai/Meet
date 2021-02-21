package com.dongjian.framwork.helper;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * 关于文件的帮助类
 */
public class FileHelper {

    private static volatile FileHelper mFileHelper = null;

    private FileHelper(){}

    public static FileHelper getInstance(){
        if(mFileHelper == null){
            synchronized (FileHelper.class){
                if(mFileHelper == null){
                    mFileHelper = new FileHelper();
                }
            }
        }
        return mFileHelper;
    }

    /**
     * 跳转到相机
     */
    public void toCamera(Context mContext){
        //安卓自带的跳转
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }
}
