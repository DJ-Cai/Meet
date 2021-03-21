package com.dongjian.framwork.db;


import com.dongjian.framwork.utils.LogUtils;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * FileName: LitePalHelper
 * Profile: 本地数据库帮助类
 */
public class LitePalHelper {

    private static volatile LitePalHelper mInstnce = null;

    private LitePalHelper() {

    }

    public static LitePalHelper getInstance() {
        if (mInstnce == null) {
            synchronized (LitePalHelper.class) {
                if (mInstnce == null) {
                    mInstnce = new LitePalHelper();
                }
            }
        }
        return mInstnce;
    }

    /**
     * 保存基类,传入support 因为所有要保存的都继承LitePalSupport
     *
     * @param support
     */
    private void baseSave(LitePalSupport support) {
        support.save();
    }

    /**
     * 保存别的用户发送过来的 请求添加好友  消息
     *
     * @param msg
     * @param id
     */
    public void saveNewFriend(String msg, String id) {
        LogUtils.e("保存新朋友   LitePalHelper.saveNewFriend");
        //填充好newFriend表之后就可以进行保存操作了
        NewFriend newFriend = new NewFriend();
        newFriend.setMsg(msg);
        newFriend.setId(id);
        newFriend.setIsAgree(-1);
        newFriend.setSaveTime(System.currentTimeMillis());
        baseSave(newFriend);
    }

//    /**
//     * 保存通话记录
//     *
//     * @param userId
//     * @param mediaType
//     * @param callStatus
//     */
//    public void saveCallRecord(String userId, int mediaType, int callStatus) {
//        CallRecord callRecord = new CallRecord();
//        callRecord.setUserId(userId);
//        callRecord.setMediaType(mediaType);
//        callRecord.setCallStatus(callStatus);
//        callRecord.setCallTime(System.currentTimeMillis());
//        baseSave(callRecord);
//    }

    /**
     * 查询的基类：将所有相关消息查询出来并返回
     *
     * @param cls
     * @return
     */
    private List<? extends LitePalSupport> baseQuery(Class cls) {
        return LitePal.findAll(cls);
    }

    /**
     * 查询  别的用户发送过来的 请求添加好友  消息
     *
     * @return
     */
    public List<NewFriend> queryNewFriend() {
        return (List<NewFriend>) baseQuery(NewFriend.class);
    }

//    /**
//     * 查询通话记录
//     *
//     * @return
//     */
//    public List<CallRecord> queryCallRecord() {
//        return (List<CallRecord>) baseQuery(CallRecord.class);
//    }

    /**
     * 更新新朋友的数据库状态：同意  或 拒绝
     *
     * @param userId
     * @param agree
     */
    public void updateNewFriend(String userId, int agree) {
        NewFriend newFriend = new NewFriend();
        newFriend.setIsAgree(agree);
        newFriend.updateAll("userId = ?", userId);
    }
}
