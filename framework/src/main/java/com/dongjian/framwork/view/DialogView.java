package com.dongjian.framwork.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * 自定义Dialog
 * 1、传入自定义的布局；
 * 2、拿到Window父容器以后，修改Window的参数和位置
 */
public class DialogView extends Dialog {

    /**
     * 构造函数
     * @param mContext
     * @param layout 自定义的布局
     * @param style 主题ID（本来该传进来的第二个参数：themeResId）
     * @param gravity 位置
     */
    public DialogView(Context mContext,int layout , int style , int gravity) {
        super(mContext, style);
        //1、传入自定义的布局
        setContentView(layout);
        //2、拿到父容器以后，修改Window的参数和位置
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = gravity;
        window.setAttributes(layoutParams);

    }
}

