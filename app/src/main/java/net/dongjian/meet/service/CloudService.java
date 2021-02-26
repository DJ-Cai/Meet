package net.dongjian.meet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.cloud.CloudManager;
import com.dongjian.framwork.db.LitePalHelper;
import com.dongjian.framwork.db.NewFriend;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.event.EventManager;
import com.dongjian.framwork.gson.TextBean;
import com.dongjian.framwork.utils.CommonUtils;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.SpUtils;
import com.google.gson.Gson;


import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * 融云的云服务
 */
public class CloudService extends Service {


    private Disposable disposable;
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

                //查询数据库，如果有重复的就不添加
                disposable = Observable.create(new ObservableOnSubscribe<List<NewFriend>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<List<NewFriend>> emitter) throws Exception {
                        emitter.onNext(LitePalHelper.getInstance().queryNewFriend());
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<NewFriend>>() {
                            @Override
                            public void accept(List<NewFriend> newFriends) throws Exception {
                                boolean isHave = false;
                                if(CommonUtils.isNotEmpty(newFriends)){
                                    for (int i = 0; i < newFriends.size(); i++) {
                                        NewFriend newFriend = newFriends.get(i);
                                        //如果这个ID已经在message里面了，则
                                        if(message.getSenderUserId().equals(newFriend.getId())){
                                            isHave = true;
                                            break;
                                        }

                                    }
                                    //不重复，才添加
                                    if(!isHave){
                                        LitePalHelper.getInstance().saveNewFriend(textBean.getMsg(),message.getSenderUserId());
                                    }
                                }
                            }
                        });
                //同意添加好友
            }else if(textBean.getType().equals(CloudManager.TYPE_ARGEED_FRIEND)){
                //1、添加到好友列表
                BmobManager.getmInstance().addFriend(message.getSenderUserId(), new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                         if(e == null){
                             //2、刷新好友列表
                             EventManager.post(EventManager.FLAG_UPDATE_FRIEND_LIST);
                         }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable.isDisposed()){
            disposable.dispose();
        }
    }
}
