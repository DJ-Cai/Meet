package net.dongjian.meet.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
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

        //只在主进程中初始化
        if (getApplicationInfo().packageName.equals(
                getCurProcessName(getApplicationContext()))) {
            //获取渠道
            //String flavor = FlavorHelper.getFlavor(this);
            //Toast.makeText(this, "flavor:" + flavor, Toast.LENGTH_SHORT).show();
            Framework.getFramework().initFramework(this);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess :
                activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
