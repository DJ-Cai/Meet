package com.dongjian.framwork.bmob;

import android.content.Context;

import com.dongjian.framwork.utils.CommonUtils;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Bmob管理类：主要就是包装一下Bmob支持的方法
 * 1、单例设计模式
 */
public class BmobManager {

    private static final String BMOB_SDK_ID = "fe48a541b5d83a582790189443337933"; //自己的key
//    private static final String BMOB_SDK_ID = "f8efae5debf319071b44339cf51153fc";

    private static volatile BmobManager mInstance = null;

    private BmobManager() {

    }

    //获得Bmob实例对象
    public static BmobManager getmInstance() {
        if (mInstance == null) {
            synchronized (BmobManager.class) {
                if (mInstance == null) {
                    mInstance = new BmobManager();
                }
            }
        }
        return mInstance;
    }

    //初始化Bmob
    public void initBmob(Context mContent) {
        Bmob.initialize(mContent, BMOB_SDK_ID);
    }

    /**
     * 判断Bmob中user的登录状态
     *
     * @return
     */
    public boolean isLogin() {
        return BmobUser.isLogin();
    }


    /**
     * 获取本地对象
     *
     * @return
     */
    public IMUser getUser() {
        return BmobUser.getCurrentUser(IMUser.class);
    }


    /**
     * 发送短信验证码
     *
     * @param phone    指定手机号码
     * @param listener 回调
     */
    public void requestSMS(String phone, QueryListener<Integer> listener) {
        //给phone发送template内容
        BmobSMS.requestSMSCode(phone, "", listener);
    }

    /**
     * 通过手机号码注册或登录
     *
     * @param phone    手机号码
     * @param code     短信验证码
     * @param listener 对结果的回调
     */
    public void signOrLoginByMobilePhone(String phone, String code, LogInListener<IMUser> listener) {
        BmobUser.signOrLoginByMobilePhone(phone, code, listener);
    }

    /**
     * 更新用户第一次上传到头像和昵称
     * 1、上传文件，拿到地址
     * 2、更新用户信息
     *
     * @param file
     * @param nickName
     */
    public void uploadFirstPhotoAndNickname(File file, final String nickName, final OnUploadPhotoListener onUploadPhotoListener) {
        final IMUser imUser = getUser();
        //上传文件
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                //上传成功
                if (e == null) {
                    //更新用户信息
                    imUser.setNickName(nickName);
                    imUser.setPhoto(bmobFile.getUrl());
                    imUser.setTokenNickName(nickName);
                    imUser.setTokenPhoto(bmobFile.getFileUrl());
                    imUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                onUploadPhotoListener.OnUpdateDone();
                            } else {
                                onUploadPhotoListener.OnUpdateFail(e);
                            }
                        }
                    });
                }
            }
        });
    }

    //让外部能拿到用户更新头像和昵称的结果，进行对应的操作
    public interface OnUploadPhotoListener {

        void OnUpdateDone();

        void OnUpdateFail(BmobException e);
    }

    /**
     * 查询操作比较多，这里做个基类
     *
     * @param key      列名
     * @param values   列值
     * @param listener 对外公布的接口，让外部获取查询结果：一个list结果集和一个BmobException
     */
    public void baseQuery(String key, String values, FindListener<IMUser> listener) {
        BmobQuery<IMUser> query = new BmobQuery<>();
        query.addWhereEqualTo(key, values);
        query.findObjects(listener);
    }

    /**
     * 查询所有用户
     * @param listener
     */
    public void queryAllUser(FindListener<IMUser> listener){
        BmobQuery<IMUser> query = new BmobQuery<>();
        query.findObjects(listener);
    }

    /**
     * 通过电话号码查询用户
     */
    public void queryPhoneUser(String phone, FindListener<IMUser> listener) {
        //查询mobilePhoneNumber列中 = phone 的
        baseQuery("mobilePhoneNumber", phone, listener);
    }

    /**
     * 根据objectId查询用户
     *
     * @param objectId
     * @param listener
     */
    public void queryObjectIdUser(String objectId, FindListener<IMUser> listener) {
        baseQuery("objectId", objectId, listener);
    }

    /**
     * 查询我的好友
     *
     * @param listener
     */
    public void queryMyFriends(FindListener<Friend> listener) {
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("user", getUser());
        query.findObjects(listener);
    }
//
//
//
//    /**
//     * 查询所有的广场的数据
//     *
//     * @param listener
//     */
//    public void queryAllSquare(FindListener<SquareSet> listener) {
//        BmobQuery<SquareSet> query = new BmobQuery<>();
//        query.findObjects(listener);
//    }
//
//    /**
//     * 查询私有库
//     *
//     * @param listener
//     */
//    public void queryPrivateSet(FindListener<PrivateSet> listener) {
//        BmobQuery<PrivateSet> query = new BmobQuery<>();
//        query.findObjects(listener);
//    }
//
//    /**
//     * 查询缘分池
//     *
//     * @param listener
//     */
//    public void queryFateSet(FindListener<FateSet> listener) {
//        BmobQuery<FateSet> query = new BmobQuery<>();
//        query.findObjects(listener);
//    }

    /**
     * 添加好友
     *
     * @param imUser
     * @param listener
     */
    public void addFriend(IMUser imUser, SaveListener<String> listener) {
        Friend friend = new Friend();
        friend.setUser(getUser());
        friend.setFriendUser(imUser);
        friend.save(listener);
    }

    /**
     * 通过ID添加好友
     *
     * @param id
     * @param listener
     */
    public void addFriend(String id, final SaveListener<String> listener) {
        queryObjectIdUser(id, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isNotEmpty(list)) {
                        IMUser imUser = list.get(0);
                        addFriend(imUser, listener);
                    }
                }
            }
        });
    }
//
//    /**
//     * 添加私有库
//     *
//     * @param listener
//     */
//    public void addPrivateSet(SaveListener<String> listener) {
//        PrivateSet set = new PrivateSet();
//        set.setUserId(getUser().getObjectId());
//        set.setPhone(getUser().getMobilePhoneNumber());
//        set.save(listener);
//    }
//
//    /**
//     * 添加到缘分池中
//     *
//     * @param listener
//     */
//    public void addFateSet(SaveListener<String> listener) {
//        FateSet set = new FateSet();
//        set.setUserId(getUser().getObjectId());
//        set.save(listener);
//    }
//
//    /**
//     * 删除缘分池
//     *
//     * @param id
//     * @param listener
//     */
//    public void delFateSet(String id, UpdateListener listener) {
//        FateSet set = new FateSet();
//        set.setObjectId(id);
//        set.delete(listener);
//    }
//
//    /**
//     * 删除私有库
//     *
//     * @param id
//     * @param listener
//     */
//    public void delPrivateSet(String id, UpdateListener listener) {
//        PrivateSet set = new PrivateSet();
//        set.setObjectId(id);
//        set.delete(listener);
//    }
//
//    /**
//     * 发布广场
//     *
//     * @param mediaType 媒体类型
//     * @param text      文本
//     * @param path      路径
//     */
//    public void pushSquare(int mediaType, String text, String path, SaveListener<String> listener) {
//        SquareSet squareSet = new SquareSet();
//        squareSet.setUserId(getUser().getObjectId());
//        squareSet.setPushTime(System.currentTimeMillis());
//
//        squareSet.setText(text);
//        squareSet.setMediaUrl(path);
//        squareSet.setPushType(mediaType);
//        squareSet.save(listener);
//    }


//
//    /**
//     * 删除好友
//     *
//     * @param id
//     * @param listener
//     */
//    public void deleteFriend(final String id, final UpdateListener listener) {
//        /**
//         * 从自己的好友列表中删除
//         * 如果需要，也可以从对方好友中删除
//         */
//        queryMyFriends(new FindListener<Friend>() {
//            @Override
//            public void done(List<Friend> list, BmobException e) {
//                if (e == null) {
//                    if (CommonUtils.isEmpty(list)) {
//                        for (int i = 0; i < list.size(); i++) {
//                            if (list.get(i).getFriendUser().getObjectId().equals(id)) {
//                                Friend friend = new Friend();
//                                friend.setObjectId(list.get(i).getObjectId());
//                                friend.delete(listener);
//                            }
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    public void addUpdateSet() {
//        UpdateSet updateSet = new UpdateSet();
//        updateSet.setVersionCode(2);
//        updateSet.setPath("---");
//        updateSet.setDesc("---");
//        updateSet.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                LogUtils.i("s:" + s + "e:" + e.toString());
//            }
//        });
//    }
//
//    /**
//     * 查询更新
//     *
//     * @param listener
//     */
//    public void queryUpdateSet(FindListener<UpdateSet> listener) {
//        BmobQuery<UpdateSet> bmobQuery = new BmobQuery<>();
//        bmobQuery.findObjects(listener);
//    }
}
