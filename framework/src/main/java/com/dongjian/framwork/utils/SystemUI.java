package com.dongjian.framwork.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;


public class SystemUI {

    //实现沉浸式状态栏
    public static void fixSystemUI(Activity mActivity){
        //1、判断当前SDK版本
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //2、获取最顶层View，然后设置系统UI的可见性
            mActivity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            //3、设置状态栏的颜色
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //4、ActionBar在AndroidManifest里设置为NoActionBar即可
        }
    }


}
