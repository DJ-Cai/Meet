package net.dongjian.meet.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dongjian.framwork.base.BaseBackActivity;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.bmob.IMUser;
import com.dongjian.framwork.utils.CommonUtils;
import com.dongjian.framwork.utils.LogUtils;

import net.dongjian.meet.R;
import net.dongjian.meet.adapter.AddFriendAdapter;
import net.dongjian.meet.model.AddFriendModel;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 添加好友的界面
 * 1、模拟用户数据
 * 2、根据条件查询 查询用户
 * 3、推荐好友
 */
public class AddFriendActivity extends BaseBackActivity implements View.OnClickListener {

    private LinearLayout ll_to_contact;
    private EditText et_phone;
    private ImageView iv_search;
    private RecyclerView mSearchResultView;

    //查询为空的时候的View
    private View include_empty_view;

    //RecyclerView的适配器和数据源
    private AddFriendAdapter mAddFriendAdapter;
    private List<AddFriendModel> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initView();
    }

    private void initView() {

        ll_to_contact = (LinearLayout) findViewById(R.id.ll_to_contact);
        et_phone = (EditText) findViewById(R.id.et_phone);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        mSearchResultView = (RecyclerView) findViewById(R.id.mSearchResultView);

        include_empty_view = findViewById(R.id.include_empty_view);

        ll_to_contact.setOnClickListener(this);
        iv_search.setOnClickListener(this);

        //RecyclerView列表的实现
        //设置是线性布局
        mSearchResultView.setLayoutManager(new LinearLayoutManager(this));
        //设置下划线
        mSearchResultView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //适配器
        mAddFriendAdapter = new AddFriendAdapter(this, mList);
        mSearchResultView.setAdapter(mAddFriendAdapter);

        //RecyclerView的点击事件
        mAddFriendAdapter.setOnClickListener(new AddFriendAdapter.OnClickListener() {
            @Override
            public void OnClick(int position) {
                Toast.makeText(AddFriendActivity.this, "position" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //跳转到从通讯录导入
            case R.id.ll_to_contact:
                break;
            //查询电话号码
            case R.id.iv_search:
                queryPhoneUser();
                break;
        }
    }

    /**
     * 通过电话号码查询
     */
    private void queryPhoneUser() {
        //1、获取电话号码
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_null), Toast.LENGTH_SHORT).show();
            return;
        }
        //2、进行查询
        BmobManager.getmInstance().queryPhoneUser(phone, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e != null) {
                    Toast.makeText(AddFriendActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                //查询到结果
                if (CommonUtils.isNotEmpty(list)) {
                    //电话号码不可重复，所以只有一个查询结果
                    IMUser imUser = list.get(0);
                    //显示recyclerView -- 显示recyclerView显示，空view隐藏
                    include_empty_view.setVisibility(View.GONE);
                    mSearchResultView.setVisibility(View.VISIBLE);

                    //查询有数据后就清空--后续这个list
                    mList.clear();
                    //加载标题和内容，刷新适配器
                    addTitle(getString(R.string.text_add_friend_title));
                    addContent(imUser);
                    mAddFriendAdapter.notifyDataSetChanged();
                    //加载推荐好友栏目
                    pushUser();
                } else {
                    //显示空数据 -- 空view显示，recyclerView隐藏
                    include_empty_view.setVisibility(View.VISIBLE);
                    mSearchResultView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 推荐 好友
     */
    private void pushUser() {
        //查询所有好友，取一部分
        BmobManager.getmInstance().queryAllUser(new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isNotEmpty(list)) {
                        addTitle("系统推荐给您的好友");
                        int num = list.size() > 10 ? list.size() : 10;
                        for (int i = 5; i < num; i++) {
                            addContent(list.get(i));
                        }
                        mAddFriendAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * 添加标题
     *
     * @param title 外部传进来的标题栏的标题
     */
    private void addTitle(String title) {
        AddFriendModel model = new AddFriendModel();
        model.setType(AddFriendAdapter.TYPE_TITLE);
        //标题内容是传进来的内容
        model.setTitle(title);
        mList.add(model);
    }

    /**
     * 添加内容
     *
     * @param
     */
    private void addContent(IMUser imUser) {
        AddFriendModel model = new AddFriendModel();
        model.setType(AddFriendAdapter.TYPE_CONTENT);
        model.setUserId(imUser.getObjectId());
        model.setPhoto(imUser.getPhoto());
        model.setSex(imUser.isSex());
        model.setAge(imUser.getAge());
        model.setNickName(imUser.getNickName());
        model.setDesc(imUser.getDesc());
        mList.add(model);
    }
}
