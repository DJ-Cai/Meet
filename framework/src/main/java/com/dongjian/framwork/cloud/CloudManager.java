package com.dongjian.framwork.cloud;

import android.content.Context;

import com.dongjian.framwork.utils.LogUtils;

import io.rong.imlib.RongIMClient;

/**
 * 融云管理
 */
public class CloudManager {
    //URL
    public static final String URL = "http://api-cn.ronghub.com/user/getToken.json";
    //APP-KEY
    public static final String KEY = "lmxuhwagl6uwd";
    //APP_SECRET
    public static final String SECRET = "soiBHijiAXwfE6";

    private static volatile CloudManager cloudManager = null;

    private CloudManager(){}

    public static CloudManager getInstance(){
        if(cloudManager == null){
            synchronized(CloudManager.class){
                if(cloudManager == null){
                    cloudManager = new CloudManager();
                }
            }
        }
        return cloudManager;
    }

    /**
     * 初始化SDK
     * @param mContext
     */
    public void  initCloud(Context mContext){
        RongIMClient.init(mContext);
    }

    /**
     * 连接融云服务
     * @param token
     */
    public void connect(String token){
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

            @Override
            public void onSuccess(String s) {
                LogUtils.e("连接成功" + s);
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
                LogUtils.e("连接失败" + connectionErrorCode);
            }

            @Override
            //这里官方文档给的方法应该是onTokenIncorrect()
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {
                LogUtils.e(" 数据库当前状态 " + databaseOpenStatus);
            }


        });
    }

    /**
     * 断开连接：断开和融云的连接后，有新消息时，仍然能够收到推送通知
     */
    public void disconnect(){
        RongIMClient.getInstance().disconnect();
    }

    /**
     * 退出登录：断开连接后，有新消息时，不想收到任何推送通知
     */
    public void logout(){
        RongIMClient.getInstance().logout();
    }

    /**
     * 接收消息的监听器
     * @param listener
     */
    public void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener){
        RongIMClient.setOnReceiveMessageListener(listener);
    }
}
