package net.dongjian.meet.base;

import android.app.Application;

import com.dongjian.framwork.Framework;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.utils.LogUtils;

public class BaseApp  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Framework.getFramwork().initFramework(this);
    }
}
