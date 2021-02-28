package com.dongjian.framwork.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dongjian.framwork.event.EventManager;
import com.dongjian.framwork.event.MessageEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里做统一的所有活动的功能----权限管理
 * 1、判断单个、集合 权限是否有被申请
 * 2、申请单个、集合 权限
 * 3、onRequestPermissionsResult对整个申请结果进行操作：
 *          失败的放入失败列表中，调用对应的成功或失败回调
 *
 */
public class BaseActivity extends AppCompatActivity {

    //申请运行时权限的Code
    private static final int PERMISSION_REQUEST_CODE = 1000;
    //申请窗口权限的Code
    public static final int PERMISSION_WINDOW_REQUEST_CODE = 1001;

    //需要申请的权限合集
    private String[] mStrPermission = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    //保存没有同意的权限---未申请权限
    private List<String> mPerList = new ArrayList<>();
    //保存没有同意的失败权限--申请失败权限
    private List<String> mPerNoList = new ArrayList<>();


    private int requestCode;
    //外部回调的接口
    private OnPermissionsResult permissionsResult;


    /**
     * 请求权限---用下面的函数来做的简单版
     * @param permissionsResult
     */
    protected void request(OnPermissionsResult permissionsResult) {
        //如果检查后有未申请权限
        if (!checkPermissionsAll()) {
            requestPermissionsAll(permissionsResult);
        }
    }


    /**
     * 判断单个权限是否有申请
     * @param permission
     * @return
     */
    protected  boolean checkPermission(String permission){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int check = checkSelfPermission(permission);
            return check == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    /**
     * 判断整个要申请权限合集是否有申请
     * @return 合集中是否有未申请的，有就返回false，
     */
    protected boolean checkPermissionsAll(){
        //先清空一下未申请权限的合集
        mPerList.clear();
        //遍历待申请合集，没申请的权限加入到未申请合集中
        for(int i = 0 ; i < mStrPermission.length ; i++){
            boolean check =  checkPermission(mStrPermission[i]);
            if(!check){
                mPerList.add(mStrPermission[i]);
            }
        }
        return mPerList.size() > 0 ? false : true;
    }

    /**
     * 请求单个权限
     * @param mPermissions
     */
    protected void requestPermission(String[] mPermissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(mPermissions,PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 请求所有未申请权限
     * @param permissionsResult
     */
    protected void requestPermissionsAll(OnPermissionsResult permissionsResult){
        this.permissionsResult = permissionsResult;
        //将未申请权限的list集合转为String数组
        requestPermission((String[]) mPerList.toArray(new String[mPerList.size()]));
    }

    //外部收到反馈后要进行处理
    protected interface OnPermissionsResult {
        void OnSuccess();

        void OnFail(List<String> noPermissions);
    }

    /**
     * 对权限申请结果的操作
     * @param requestCode   请求码
     * @param permissions   申请的权限合集
     * @param grantResults  授予的结果合集
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //先将以前申请失败的权限列表清空
        mPerNoList.clear();
        if (requestCode == requestCode) {
            if (grantResults.length > 0) {
                //遍历结果合集，有失败的就加入到失败权限列表中
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        //你有失败的权限
                        mPerNoList.add(permissions[i]);
                    }
                }
                //如果调用过requestPermissionsAll，有下面这个接口；则需要执行对应的方法
                if (permissionsResult != null) {
                    if (mPerNoList.size() == 0) {
                        permissionsResult.OnSuccess();
                    } else {
                        permissionsResult.OnFail(mPerNoList);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    /**
//     * 判断窗口权限
//     * @return
//     */
//    protected boolean checkWindowPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return Settings.canDrawOverlays(this);
//        }
//        return true;
//    }
//
//    /**
//     * 请求窗口权限
//     */
//    protected void requestWindowPermissions() {
//        Toast.makeText(this, "申请窗口权限，暂时没做UI交互", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION
//                , Uri.parse("package:" + getPackageName()));
//        startActivityForResult(intent, PERMISSION_WINDOW_REQUEST_CODE);
//    }

    /**
     * EventBus的步骤：
     * 1.注册
     * 2.声明注册方法 onEvent
     * 3.发送事件
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
//        switch (event.getType()) {
//            case EventManager.EVENT_RUPDATE_LANGUAUE:
//                LanguaueUtils.updateLanguaue(this);
//                recreate();
//                break;
//        }
    }
}
