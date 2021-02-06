package com.dongjian.framwork.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dongjian.framwork.utils.SystemUI;

/**
 * 单一的界面功能：沉浸式
 */
public class BaseUIActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUI.fixSystemUI(this);
    }
}
