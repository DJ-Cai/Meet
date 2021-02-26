package net.dongjian.meet.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.RequiresApi;
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

import net.dongjian.meet.R;

import java.util.ArrayList;
import java.util.List;


import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**添加好友的逻辑
 * 1.查询申请好友的列表
 * 2.通过适配器显示出来
 * 3.如果同意则添加对方为自己的好友
 * 4.并且发送给对方自定义的消息
 * 5.对方将我添加到好友列表
 */
public class NewFriendActivity extends BaseBackActivity {

    private ViewStub item_empty_view;
    private RecyclerView mNewFriendView;

    private Disposable disposable;

    private CommonAdapter<NewFriend> mNewFriendAdapter;
    private List<NewFriend> mList = new ArrayList<>();

    //对方用户
    IMUser imUser;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        initView();
        queryNewFriend();
    }

    private void initView() {
        mNewFriendView = (RecyclerView) findViewById(R.id.mNewFriendView);

        mNewFriendView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        mNewFriendView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mNewFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<NewFriend>() {
            @Override
            public void onBindViewHolder(NewFriend model, CommonViewHolder viewHolder, int type, int position) {
                //根据ID查询用户信息
                BmobManager.getmInstance().queryObjectIdUser(model.getId(), new FindListener<IMUser>() {
                    @Override
                    public void done(List<IMUser> list, BmobException e) {
                        if(e == null){
                            imUser = list.get(0);
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
                         * 4、对方建构添加到好友列表
                         * 5、刷新好友列表
                         */
                        //1、点击同意后刷新当前的Item
                        updateItem(position,0);
                        //2、将好友添加到自己的好友列表
                        BmobManager.getmInstance().addFriend(imUser, new SaveListener<String>() {
                            @Override
                            //3、通知对方--我已经同意了
                            public void done(String s, BmobException e) {
                                CloudManager.getInstance().sendTextMessage("",CloudManager.TYPE_ARGEED_FRIEND,imUser.getObjectId());
                                //刷新好友列表
                                EventManager.post(EventManager.FLAG_UPDATE_FRIEND_LIST);
                            }
                        });
                    }
                });

                //拒绝
                viewHolder.getView(R.id.ll_no).setOnClickListener(new View.OnClickListener() {
                    @Override
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
     * 查询新朋友
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void queryNewFriend(){
        /**
         * 在子线程中获取好友申请列表，
         * 在主线程中更新UI
         * 用RxJava
         */
        disposable = Observable.create(new ObservableOnSubscribe<List<NewFriend>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<NewFriend>> emitter) throws Exception {
                //通过发射器来执行下一步（子线程）
                emitter.onNext(LitePalHelper.getInstance().queryNewFriend());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NewFriend>>() {
                    @Override
                    public void accept(List<NewFriend> newFriends) {
                        //更新UI
                        if (CommonUtils.isNotEmpty(newFriends)) {
                            //添加   刷新
                            mList.addAll(newFriends);
                            mNewFriendAdapter.notifyDataSetChanged();
                        } else {
                            //显示好友列表，隐藏那个空view
                            showViewStub();
                            mNewFriendView.setVisibility(View.GONE);
                        }
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable.isDisposed()){
            disposable.dispose();
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
