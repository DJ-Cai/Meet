package com.dongjian.framwork.helper;

import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.bmob.FateSet;
import com.dongjian.framwork.bmob.IMUser;
import com.dongjian.framwork.utils.CommonUtils;
import com.dongjian.framwork.utils.LogUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * FileName: PairFriendHelper
 * Founder: LiuGuiLin
 * Profile: 匹配好友
 */
public class PairFriendHelper {

    private static volatile PairFriendHelper mInstance = null;

    //延迟时间 单位s
    private static final int DELAY_TIME = 2;

    //随机类
    private Random mRandom;
    //自己的ID
    private String meUserId;
    //自己的对象
    private IMUser meIMUser;

    //RxJava
    private Disposable mDisposable;

    //轮询次数
    private int FateNumber = 0;

    //接口
    private OnPairResultListener onPairResultListener;

    public void setOnPairResultListener(OnPairResultListener onPairResultListener) {
        this.onPairResultListener = onPairResultListener;
    }

    private PairFriendHelper() {
        mRandom = new Random();
        meIMUser = BmobManager.getmInstance().getUser();
        meUserId = meIMUser.getObjectId();
    }

    public static PairFriendHelper getInstance() {
        if (mInstance == null) {
            synchronized (PairFriendHelper.class) {
                if (mInstance == null) {
                    mInstance = new PairFriendHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 0：从用户组随机抽取一位好友
     * 1：深度匹配：资料的相似度
     * 2：缘分 同一时刻搜索的
     * 3：年龄相似的异性
     *
     * @param index
     */
    public void pairUser(int index, List<IMUser> list) {
        switch (index) {
            case 0:
                randomUser(list);
                break;
            case 1:
                soulUser(list);
                break;
            case 2:
                fateUser();
                break;
            case 3:
                loveUser(list);
                break;
        }
    }

    /**
     * 恋爱匹配
     *
     * @param list
     */
    private void loveUser(List<IMUser> list) {

        /**
         * 1.抽取所有的用户
         * 2.根据性别抽取出异性
         * 3.根据年龄再抽取
         * 4.可以有一些附加条件：爱好 星座 ~~
         * 5.计算出来
         */
        //2.根据性别抽取出异性
        List<IMUser> _love_user = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            IMUser imUser = list.get(i);

            //过滤自己
            if (imUser.getObjectId().equals(meUserId)) {
                //跳过本次循环
                continue;
            }

            //异性
            if (imUser.isSex() != meIMUser.isSex()) {
                _love_user.add(imUser);
            }
        }

        //异性保存成功
        if (CommonUtils.isNotEmpty(_love_user)) {
            final List<String> _love_id = new ArrayList<>();
            //计算年龄
            for (int i = 0; i < _love_user.size(); i++) {
                IMUser imUser = _love_user.get(i);

                if (Math.abs(imUser.getAge() - meIMUser.getAge()) <= 3) {
                    _love_id.add(imUser.getObjectId());
                }
            }
            if (_love_id.size() > 0) {
                //在这里增加更多的判断条件
                //延迟发送
                rxJavaParingResult(new OnRxJavaResultListener() {
                    @Override
                    public void rxJavaResult() {
                        int _r = mRandom.nextInt(_love_id.size());
                        onPairResultListener.OnPairListener(_love_id.get(_r));
                    }
                });
            } else {
                onPairResultListener.OnPairFailListener();
            }
        } else {
            onPairResultListener.OnPairFailListener();
        }
    }

    /**
     * 缘分匹配好友
     * * 1.创建库FateSet：将同时进行缘分匹配的用户添加进去
     * * 2.将自己添加进去
     * * 3.轮询查找用户
     * * 4.15s刷新
     * * 5.查询到了之后则反馈给外部
     * * 6.将自己删除
     */
    private void fateUser() {

        //2.先把自己添加进缘分池
        BmobManager.getmInstance().addFateSet(new SaveListener<String>() {
            @Override
            public void done(final String s, BmobException e) {
                if (e == null) {
                    //然后再每隔一秒轮询去查找有无一起在缘分池中的用户
                    mDisposable = Observable.interval(1, TimeUnit.SECONDS)
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    queryFateSet(s);
                                }
                            });
                }
            }
        });
    }

    /**
     * 查询缘分池
     *
     * @param id
     */
    private void queryFateSet(final String id) {
        BmobManager.getmInstance().queryFateSet(new FindListener<FateSet>() {
            @Override
            //对查询结果进行处理
            public void done(List<FateSet> list, BmobException e) {
                //每次轮询，轮询次数自增1
                FateNumber++;
                if (e == null) {
                    if (CommonUtils.isNotEmpty(list)) {
                        //如果>1才说明有人在匹配
                        if (list.size() > 1) {
                            //有结果了，需要结束轮询进程
                            disposable();
                            //过滤自己
//                            LogUtils.e("过滤自己");
                            for (int i = 0; i < list.size(); i++) {
                                FateSet fateSet = list.get(i);
                                if (fateSet.getUserId().equals(meUserId)) {
                                    list.remove(i);
                                    break;
                                }
                            }
                            //对最终结果随机取一个
                            int _r = mRandom.nextInt(list.size());
                            //抛出最终结果
                            onPairResultListener.OnPairListener(list.get(_r).getUserId());
                            //在缘分池中删除自己
                            deleteFateSet(id);
                            //轮询结果 置0
                            FateNumber = 0;
                        } else {
//                            LogUtils.e("FateNumber:" + FateNumber + "没有过滤自己");
                            //超时：轮询超过15次，，即超过15秒
                            if (FateNumber >= 15) {
                                //结束轮询
                                disposable();
                                //删除缘分池中的自己
                                deleteFateSet(id);
                                //返回失败
                                onPairResultListener.OnPairFailListener();
                                FateNumber = 0;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 删除指定的缘分池用户
     *
     * @param id 用户id
     */
    private void deleteFateSet(String id) {
        BmobManager.getmInstance().delFateSet(id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtils.i("Delete");
                }
            }
        });
    }

    /**
     * 灵魂匹配好友
     * 四要素：星座 年龄 爱好 状态
     *
     * @param list
     */
    private void soulUser(List<IMUser> list) {
        //结果集
        List<String> _list_objectId = new ArrayList<>();
        // 四要素：星座 年龄 爱好 状态
        for (int i = 0; i < list.size(); i++) {
            IMUser imUser = list.get(i);

            //过滤自己
            if (imUser.getObjectId().equals(meUserId)) {
                //跳过本次循环
                continue;
            }

            //匹配星座
            if (imUser.getConstellation().equals(meIMUser.getConstellation())) {
                _list_objectId.add(imUser.getObjectId());
            }

            //匹配年龄
            if (imUser.getAge() == meIMUser.getAge()) {
                _list_objectId.add(imUser.getObjectId());
            }

            //匹配爱好
            if (imUser.getHobby().equals(meIMUser.getHobby())) {
                _list_objectId.add(imUser.getObjectId());
            }

            //单身状态
            if (imUser.getStatus().equals(meIMUser.getStatus())) {
                _list_objectId.add(imUser.getObjectId());
            }
        }

        //计算重复的ID----重复的越多，匹配度越高

        //定义Map
        Map<String, Integer> _map = new HashMap<>();
        //遍历List
        for (String str : _list_objectId) {
            //定义基础的次数
            Integer i = 1;
            //根据ID获取map的键值 如果不等于空 说明有数据 并且在原基础上自增1
            if (_map.get(str) != null) {
                i = _map.get(str) + 1;
            }
            //如果等于空，添加1进去
            _map.put(str, i);
        }

        //获得最佳的对象： 传入匹配度（map的value） 和 匹配集合map
        final List<String> _soul_list = mapComperTo(4, _map);

        if (CommonUtils.isNotEmpty(_soul_list)) {
            //计算
            rxJavaParingResult(new OnRxJavaResultListener() {
                @Override
                public void rxJavaResult() {
                    //在结果集里随机挑选一个userId进行返回
                    int _r = mRandom.nextInt(_soul_list.size());
                    onPairResultListener.OnPairListener(_soul_list.get(_r));
                }
            });
        } else {
            onPairResultListener.OnPairFailListener();
        }
    }

    /**
     * 随机匹配好友
     *
     * @param list
     */
    private void randomUser(final List<IMUser> list) {
        /**
         * 1.获取到全部的用户组---传参进来的list
         * 2.过滤自己
         * 3.开始随机
         * 4.根据随机的数值拿到对应的对象ID
         * 5.接口回传
         */

        //2、过滤自己
        for (int i = 0; i < list.size(); i++) {
            //对象ID == 我的ID
            if (list.get(i).getObjectId().equals(meUserId)) {
                list.remove(i);
            }
        }

        //4后的步骤：处理结果
        rxJavaParingResult(new OnRxJavaResultListener() {
            @Override
            public void rxJavaResult() {
                //接收2s后的通知
                //3、随机数
                int _r = mRandom.nextInt(list.size());
                IMUser imUser = list.get(_r);
                if (imUser != null) {
                    //5.接口回传--将获得的userId结果抛出
                    onPairResultListener.OnPairListener(imUser.getObjectId());
                }
            }
        });
    }


    /**
     * Map计算将传入的size对应的key传出
     *
     * @param size
     * @param _map
     */
    private List<String> mapComperTo(int size, Map<String, Integer> _map) {
        //结果集---可能多个匹配成功，所以返回一个集合
        List<String> _list_key = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : _map.entrySet()) {
            String _key = entry.getKey();
            Integer _values = entry.getValue();
            if (_values == size) {
                _list_key.add(_key);
            }
        }
        //如果结果集为空，则代表此匹配度下无用户，降低匹配度
        if (_list_key.size() == 0) {
            size = size - 1;
            if (size == 0) {
                return null;
            }
            return mapComperTo(size, _map);
        }
        return _list_key;
    }


    /**
     * 异步线程处理结果
     *
     * @param listener
     */
    private void rxJavaParingResult(final OnRxJavaResultListener listener) {
        //延迟2秒,子线程操作，主线程处理
        mDisposable = Observable
                .timer(DELAY_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //返回结果
                        listener.rxJavaResult();
                    }
                });
    }

    //将结果传递出去
    public interface OnPairResultListener {

        //匹配成功
        void OnPairListener(String userId);

        //匹配失败
        void OnPairFailListener();
    }

    //记录RxJava的返回结果
    public interface OnRxJavaResultListener {

        void rxJavaResult();
    }

    /**
     * 销毁
     */
    public void disposable() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        }
    }
}
