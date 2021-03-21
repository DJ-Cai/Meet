package net.dongjian.meet.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dongjian.framwork.adapter.CommonAdapter;
import com.dongjian.framwork.adapter.CommonViewHolder;
import com.dongjian.framwork.base.BaseFragment;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.bmob.IMUser;
import com.dongjian.framwork.cloud.CloudManager;
import com.dongjian.framwork.event.EventManager;
import com.dongjian.framwork.event.MessageEvent;
import com.dongjian.framwork.gson.TextBean;
import com.dongjian.framwork.utils.CommonUtils;
import com.dongjian.framwork.utils.LogUtils;
import com.google.gson.Gson;


import net.dongjian.meet.R;
import net.dongjian.meet.model.ChatRecordModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * FileName: ChatRecordFragment
 * Founder: LiuGuiLin
 * Profile: 聊天记录
 */
public class ChatRecordFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View item_empty_view;
    private RecyclerView mChatRecordView;
    //下拉刷新
    private SwipeRefreshLayout mChatRecordRefreshLayout;

    private CommonAdapter<ChatRecordModel> mChatRecordAdapter;
    private List<ChatRecordModel> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_record, null);
        initView(view);
        return view;
    }

    private void initView(final View view) {

        item_empty_view = view.findViewById(R.id.item_empty_view);

        mChatRecordRefreshLayout = view.findViewById(R.id.mChatRecordRefreshLayout);
        mChatRecordView = view.findViewById(R.id.mChatRecordView);
        //下拉刷新
        mChatRecordRefreshLayout.setOnRefreshListener(this);
        //线性布局+分割线
        mChatRecordView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatRecordView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        //设置适配器
        mChatRecordAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<ChatRecordModel>() {
            @Override
            public void onBindViewHolder(final ChatRecordModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setImageUrl(getActivity(), R.id.iv_photo, model.getUrl());
                viewHolder.setText(R.id.tv_nickname, model.getNickName());
                viewHolder.setText(R.id.tv_content, model.getEndMsg());
                viewHolder.setText(R.id.tv_time, model.getTime());

                if(model.getUnReadSize() == 0){
                    viewHolder.getView(R.id.tv_un_read).setVisibility(View.GONE);
                }else{
                    viewHolder.getView(R.id.tv_un_read).setVisibility(View.VISIBLE);
                    viewHolder.setText(R.id.tv_un_read, model.getUnReadSize() + "");
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ChatActivity.startActivity(getActivity(),
//                                model.getUserId(),model.getNickName(),model.getUrl());
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_chat_record_item;
            }
        });
        mChatRecordView.setAdapter(mChatRecordAdapter);

        //避免重复
        queryChatRecord();
    }

    /**
     * 查询聊天记录
     */
    private void queryChatRecord() {
        //都需要刷新
        mChatRecordRefreshLayout.setRefreshing(true);
        //在融云那里获取消息记录，
        CloudManager.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            //成功
            @Override
            public void onSuccess(List<Conversation> conversations) {
                LogUtils.e("ChatRecordFragmment.queryChatRecord.onSuccess");
                //将刷新false
                mChatRecordRefreshLayout.setRefreshing(false);
                if (CommonUtils.isNotEmpty(conversations)) {
                    //防止数据重复
                    if (mList.size() > 0) {
                        mList.clear();
                    }
                    for (int i = 0; i < conversations.size(); i++) {
                        //从消息记录里拿到具体对话对象--》 目标id
                        final Conversation c = conversations.get(i);
                        String id = c.getTargetId();
                        //在Bmob里查询对象的信息
                        BmobManager.getmInstance().queryObjectIdUser(id, new FindListener<IMUser>() {
                            @Override
                            public void done(List<IMUser> list, BmobException e) {
                                if (e == null) {
                                    if (CommonUtils.isNotEmpty(list)) {
                                        //填充CharRecoreModel---会话对象
                                        IMUser imUser = list.get(0);
                                        ChatRecordModel chatRecordModel = new ChatRecordModel();
                                        chatRecordModel.setUserId(imUser.getObjectId());
                                        chatRecordModel.setUrl(imUser.getPhoto());
                                        chatRecordModel.setNickName(imUser.getNickName());
                                        //获取会话的时间
                                        chatRecordModel.setTime(new SimpleDateFormat("HH:mm:ss")
                                                .format(c.getReceivedTime()));
                                        //获取未读数量
                                        chatRecordModel.setUnReadSize(c.getUnreadMessageCount());
                                        //获取最后一条消息（因为可能是图片、位置什么的，所以不能简单的直接获取）
                                        String objectName = c.getObjectName();
                                        if (objectName.equals(CloudManager.MSG_TEXT_NAME)) {
                                            //普通消息
                                            TextMessage textMessage = (TextMessage) c.getLatestMessage();
                                            String msg = textMessage.getContent();
                                            //需要用gson转化一下
                                            TextBean bean = new Gson().fromJson(msg, TextBean.class);
                                            if (bean.getType().equals(CloudManager.TYPE_TEXT)) {
                                                chatRecordModel.setEndMsg(bean.getMsg());
                                                mList.add(chatRecordModel);
                                            }
                                        } else if (objectName.equals(CloudManager.MSG_IMAGE_NAME)) {
                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_img));
                                            mList.add(chatRecordModel);
                                        } else if (objectName.equals(CloudManager.MSG_LOCATION_NAME)) {
                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_location));
                                            mList.add(chatRecordModel);
                                        }
                                        mChatRecordAdapter.notifyDataSetChanged();

                                        if(mList.size() > 0){
                                            item_empty_view.setVisibility(View.GONE);
                                            mChatRecordView.setVisibility(View.VISIBLE);
                                        }else{
                                            item_empty_view.setVisibility(View.VISIBLE);
                                            mChatRecordView.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }else{
                    //要隐藏刷新ing的动画
                    mChatRecordRefreshLayout.setRefreshing(false);
                    item_empty_view.setVisibility(View.VISIBLE);
                    mChatRecordView.setVisibility(View.GONE);
                }
            }

            //失败
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.i("onError" + errorCode);
                //要隐藏刷新ing的动画
                mChatRecordRefreshLayout.setRefreshing(false);
            }
        });
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        //在刷新就去再查
        if (mChatRecordRefreshLayout.isRefreshing()) {
            queryChatRecord();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queryChatRecord();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case EventManager.FLAG_UPDATE_FRIEND_LIST:
                if (mChatRecordRefreshLayout.isRefreshing()) {
                    queryChatRecord();
                }
                break;
        }
    }
}
