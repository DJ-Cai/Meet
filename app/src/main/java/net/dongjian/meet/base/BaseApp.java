package net.dongjian.meet.base;

import android.app.Application;
import android.os.Handler;

import com.dongjian.framwork.Framework;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.utils.LogUtils;

public class BaseApp  extends Application {

    /**
     * Applicaiton的优化
     * 1、必要的组件在程序主页（MainActivity）去初始化---比如说Service服务
     * 2、非必要的组件可以在子线程中初始化 ---Dialog
     * 3、如果组件一定要在App中初始化，那么尽可能的延时
     */


    @Override
    public void onCreate() {
        super.onCreate();

        //用Handler进行延时，在子线程中进行初始化
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Framework.getFramwork().initFramework(BaseApp.this);
            }
        },2000);
    }
}
