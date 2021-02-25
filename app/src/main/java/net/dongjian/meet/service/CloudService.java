package net.dongjian.meet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dongjian.framwork.cloud.CloudManager;
import com.dongjian.framwork.db.LitePalHelper;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.gson.TextBean;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.SpUtils;
import com.google.gson.Gson;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

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
                parsingImMessage(message);
                return false;
            }
        });
    }

    /**
     * 解析消息体
     * @param message
     */
    private void parsingImMessage(Message message) {
        LogUtils.i("message:" + message);
        String objectName = message.getObjectName();
        //文本消息
        if (objectName.equals(CloudManager.MSG_TEXT_NAME)) {
            //获取消息主体
            TextMessage textMessage = (TextMessage) message.getContent();
            String content = textMessage.getContent();
            LogUtils.i("content:" + content);
            TextBean textBean = new Gson().fromJson(content, TextBean.class);
            //普通消息
            if (textBean.getType().equals(CloudManager.TYPE_TEXT)) {

                //添加好友消息
            } else if (textBean.getType().equals(CloudManager.TYPE_ADD_FRIEND)) {
                //存入数据库 Bmob RongCloud 都没有提供存储方法
                //使用另外的方法来实现 存入本地数据库LitePal
                LogUtils.i("添加好友消息");
                LitePalHelper.getInstance().saveNewFriend(textBean.getMsg(),message.getSenderUserId());
            }else if(textBean.getType().equals(CloudManager.TYPE_ARGEED_FRIEND)){

            }
        }
    }
}
