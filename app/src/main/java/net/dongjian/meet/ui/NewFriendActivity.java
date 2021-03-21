package net.dongjian.meet.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dongjian.framwork.adapter.CommonAdapter;
import com.dongjian.framwork.adapter.CommonViewHolder;
import com.dongjian.framwork.base.BaseBackActivity;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.bmob.IMUser;
import com.dongjian.framwork.cloud.CloudManager;
import com.dongjian.framwork.db.LitePalHelper;
import com.dongjian.framwork.db.NewFriend;
import com.dongjian.framwork.event.EventManager;
import com.dongjian.framwork.utils.CommonUtils;
import com.dongjian.framwork.utils.LogUtils;

import net.dongjian.meet.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class NewFriendActivity extends BaseBackActivity {

    /**添加好友的逻辑
     * 1.查询申请好友的列表
     * 2.通过适配器显示出来
     * 3.如果同意则添加对方为自己的好友
     * 4.并且发送给对方自定义的消息
     * 5.对方将我添加到好友列表
     */

    private ViewStub item_empty_view;
    private RecyclerView mNewFriendView;

    private Disposable disposable;

    private CommonAdapter<NewFriend> mNewFriendAdapter;
    private List<NewFriend> mList = new ArrayList<>();

    //对方用户
    IMUser imUser;

    private List<IMUser> mUserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        initView();

    }

    private void initView() {
        mNewFriendView = (RecyclerView) findViewById(R.id.mAllFriendView);

        mNewFriendView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        mNewFriendView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mNewFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<NewFriend>() {
            @Override
            public void onBindViewHolder(final NewFriend model, final CommonViewHolder viewHolder, int type,final int position) {
                //根据ID查询用户信息
                BmobManager.getmInstance().queryObjectIdUser(model.getId(), new FindListener<IMUser>() {
                    @Override
                    public void done(List<IMUser> list, BmobException e) {
                        if(e == null){
                            imUser = list.get(0);
                            mUserList.add(imUser);
                            viewHolder.setImageUrl(NewFriendActivity.this, R.id.iv_photo,
                                    imUser.getPhoto());
                            viewHolder.setImageResource(R.id.iv_sex, imUser.isSex() ?
                                    R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                            viewHolder.setText(R.id.tv_nickname, imUser.getNickName());
                            viewHolder.setText(R.id.tv_age, imUser.getAge()
                                    + getString(R.string.text_search_age));
                            viewHolder.setText(R.id.tv_desc, imUser.getDesc());
                            viewHolder.setText(R.id.tv_msg, model.getMsg());
                            //isAgree == -1 :需要显示勾叉来让用户决定是否同意该好友请求
                            //isAgree == 0  / 1:隐藏勾叉，显示结果，但0/1显示的结果文本内容不一样
                            if (model.getIsAgree() == 0) {
                                viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                viewHolder.setText(R.id.tv_result, getString(R.string.text_new_friend_agree));
                            } else if (model.getIsAgree() == 1) {
                                viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                viewHolder.setText(R.id.tv_result, getString(R.string.text_new_friend_no_agree));
                            }
                        }
                    }
                });

                //同意
                viewHolder.getView(R.id.ll_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 1、点击同意后刷新当前的Item
                         * 2、将好友添加到自己的好友列表
                         * 3、通知对方--我已经同意了
                         * 4、对方将我添加到好友列表
                         * 5、刷新好友列表
                         */
                        //1、点击同意后刷新当前的Item
                        updateItem(position,0);
                        //将好友添加到自己的好友列表
                        //构建一个ImUSER
                        IMUser friendUser = new IMUser();
                        friendUser.setObjectId(model.getId());
                        //2、将好友添加到自己的好友列表
                        BmobManager.getmInstance().addFriend(friendUser, new SaveListener<String>() {
                            @Override
                            //3、通知对方--我已经同意了
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    //保存成功
                                    //通知对方：msg，消息类型，对方id
                                    CloudManager.getInstance().sendTextMessage("有人同意了你的好友请求哟",CloudManager.TYPE_ARGEED_FRIEND, imUser.getObjectId());
                                    //刷新好友列表
                                    EventManager.post(EventManager.FLAG_UPDATE_FRIEND_LIST);
                                }
                            }
                        });
                    }
                });

                //拒绝
                viewHolder.getView(R.id.ll_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    //更新之后就没事了
                    public void onClick(View v) {
                        updateItem(position,1);
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_new_friend_item;
            }
        });
        mNewFriendView.setAdapter(mNewFriendAdapter);
        queryNewFriend();
    }

    /**
     * 更新item
     * @param position
     * @param i
     */
    private void updateItem(int position, int i) {
        //拿到newFriend
        NewFriend newFriend = mList.get(position);
        //更新数据库
        LitePalHelper.getInstance().updateNewFriend(newFriend.getId(), i);
        //更新本地的数据源--mList
        newFriend.setIsAgree(i);
        mList.set(position, newFriend);
        //刷新
        mNewFriendAdapter.notifyDataSetChanged();
    }

    /**
     * 查询  别的用户发送过来的 请求添加好友  消息 (需要切换子进程去查)
     */
    private void queryNewFriend() {
        LogUtils.e("NewFriendActivity.queryNewFriend");
        /**
         * 在子线程中获取好友申请列表然后在主线程中更新我们的UI
         * RxJava 线程调度
         */
        disposable = Observable.create(new ObservableOnSubscribe<List<NewFriend>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NewFriend>> emitter) throws Exception {
                ////通过发射器来执行下一步（子线程）

//                emitter.onNext(LitePalHelper.getInstance().queryNewFriend());
                LogUtils.e("NewFriendActivity 这里是有问题的：LitePal不能完成这个任务,好像是融云的问题");
                List<NewFriend> list = LitePal.findAll(NewFriend.class);
                LogUtils.e("NewFriendActivity.queryNewFriend.subscribe拿到数据");
                emitter.onNext(list);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NewFriend>>() {
                    @Override
                    public void accept(List<NewFriend> newFriends) throws Exception {
                        //更新UI
                        if (CommonUtils.isNotEmpty(newFriends)) {
                            LogUtils.e("NewFriendActivity.queryNewFriend . notEmpty");
                            //有消息，则需要加进list数据源里，然后适配器才可以填入，这里刷一下就好
                            mList.addAll(newFriends);
                            mNewFriendAdapter.notifyDataSetChanged();
                        } else {
                            LogUtils.e("NewFriendActivity.queryNewFriend . Empty");
                            //显示空view 隐藏那个空的好友列表RecyclerView
                            showViewStub();
                            mNewFriendView.setVisibility(View.GONE);
                        }
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    /**
     * 显示懒加载布局
     */
    private void showViewStub(){
        item_empty_view = findViewById(R.id.item_empty_view);
        item_empty_view.inflate();
    }
}
