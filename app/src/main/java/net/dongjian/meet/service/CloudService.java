package net.dongjian.meet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dongjian.framwork.cloud.CloudManager;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.SpUtils;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * 融云的云服务
 */
public class CloudService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        linkCloudServer();
    }

    /**
     * 连接云服务
     */
    private void linkCloudServer() {
        String token = SpUtils.getInstance().getString(Constants.SP_TOKEN,"");
        LogUtils.e("token : " + token);
        //连接服务
        CloudManager.getInstance().connect(token);
        //接收消息
        CloudManager.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                LogUtils.e("CloudService 的 linkCloudServer 的 message" + message);
                return false;
            }
        });
    }
}
