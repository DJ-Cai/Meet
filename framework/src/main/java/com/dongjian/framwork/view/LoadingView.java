package com.dongjian.framwork.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongjian.framwork.manager.DialogManager;
import com.dongjian.framwork.utils.AnimUtils;

import net.dongjian.framework.R;

/**
 * 加载提示框
 */
public class LoadingView {

    private DialogView mLoadingView;
    private ImageView iv_loading;
    private TextView tv_loading_text;
    private ObjectAnimator animator;

    /**
     * LoadingView的构造函数：负责加载loading的图片和使之旋转
     *
     * @param mContext
     */
    public LoadingView(Context mContext) {
        //通过Dialog来显示LoadingView
        mLoadingView = DialogManager.getInstance().initView(mContext, R.layout.dialog_loding);
        //拿到这个LoadingView的主要元素，方便后续的操作
        iv_loading = mLoadingView.findViewById(R.id.iv_loding);
        tv_loading_text = mLoadingView.findViewById(R.id.tv_loding_text);
        //使之旋转
        animator = AnimUtils.rotation(iv_loading);
    }

    /**
     * 设置这个Loading里的text元素里的提示文本
     *
     * @param text
     */
    public void setLoadingText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tv_loading_text.setText(text);
        }
    }

    public void show() {
        animator.start();
        DialogManager.getInstance().show(mLoadingView);
    }

    public void show(String text) {
        animator.start();
        setLoadingText(text);
        DialogManager.getInstance().show(mLoadingView);
    }

    public void hide() {
        animator.pause();
        DialogManager.getInstance().hide(mLoadingView);
    }

    /**
     * 外部是否可以点击消失
     *
     * @param flag
     */
    public void setCancelable(boolean flag) {
        mLoadingView.setCancelable(flag);
    }
}
