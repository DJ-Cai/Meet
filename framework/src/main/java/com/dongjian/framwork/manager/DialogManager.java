package com.dongjian.framwork.manager;

import android.content.Context;
import android.view.Gravity;

import com.dongjian.framwork.view.DialogView;

import net.dongjian.framework.R;

/**
 * Dialog提示框管理类
 */
public class DialogManager {

    private static volatile DialogManager mInstance = null;

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        if (mInstance == null) {
            synchronized (DialogManager.class) {
                if (mInstance == null) {
                    mInstance = new DialogManager();
                }
            }
        }
        return mInstance;
    }

    public DialogView initView(Context mContext, int layout, int gravity) {
        return new DialogView(mContext, layout, R.style.Theme_Dialog, gravity);
    }

    /**
     * 上面函数的重载：Dialog位置默认居中
     */
    public DialogView initView(Context mContext, int layout) {
        return new DialogView(mContext, layout, R.style.Theme_Dialog, Gravity.CENTER);
    }

    /**
     * 提示框的状态：展示
     *
     * @param view
     */
    public void show(DialogView view) {
        if (view != null) {
            if (!view.isShowing()) {
                view.show();
            }
        }
    }

    /**
     * 提示框的状态：隐藏
     *
     * @param view
     */
    public void hide(DialogView view) {
        if (view != null) {
            if (view.isShowing()) {
                view.dismiss();
            }
        }
    }
}
