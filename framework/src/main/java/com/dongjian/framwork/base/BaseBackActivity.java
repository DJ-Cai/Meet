package com.dongjian.framwork.base;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

public class BaseBackActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //安卓版本大于5.0就显示返回键
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //将“显示主页”设置为“启用”
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //清除actionBar和View之间的阴影
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //判断菜单id = home 就可以finish  --- 即返回主页
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
