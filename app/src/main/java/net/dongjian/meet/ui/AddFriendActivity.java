package net.dongjian.meet.ui;

import android.Manifest;
import android.content.Intent;
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

import com.dongjian.framwork.adapter.CommonAdapter;
import com.dongjian.framwork.adapter.CommonViewHolder;
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

    //多type：标题和内容
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_CONTENT = 1;

    private LinearLayout ll_to_contact;
    private EditText et_phone;
    private ImageView iv_search;
    private RecyclerView mSearchResultView;

    //查询为空的时候的View
    private View include_empty_view;

    //RecyclerView的适配器和数据源
    private CommonAdapter<AddFriendModel> mAddFriendAdapter;
    private List<AddFriendModel> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initView();
    }

    private void initView() {

//        ll_to_contact = (LinearLayout) findViewById(R.id.ll_to_contact);
        et_phone = (EditText) findViewById(R.id.et_phone);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        mSearchResultView = (RecyclerView) findViewById(R.id.mSearchResultView);

        include_empty_view = findViewById(R.id.include_empty_view);

//        ll_to_contact.setOnClickListener(this);
        iv_search.setOnClickListener(this);

        //RecyclerView列表的实现
        //设置是线性布局
        mSearchResultView.setLayoutManager(new LinearLayoutManager(this));
        //设置下划线
        mSearchResultView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //适配器
        //原本：mAddFriendAdapter = new AddFriendAdapter(this, mList); 然后再通过addFriendAdapter进行系列操作
        //现在：通过万能适配Adpater来做
        mAddFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnMoreBindDataListener<AddFriendModel>() {
            @Override
            public int getItemType(int position) {
                return mList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(AddFriendModel model, CommonViewHolder viewHolder, int type, int position) {
                if (type == TYPE_TITLE) {
                    viewHolder.setText(R.id.tv_title, model.getTitle());
                } else if (type == TYPE_CONTENT) {
                    //设置头像、性别、昵称、年龄、描述
                    viewHolder.setImageUrl(AddFriendActivity.this, R.id.iv_photo, model.getPhoto());
                    viewHolder.setImageResource(R.id.iv_sex,
                            model.isSex() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                    viewHolder.setText(R.id.tv_nickname, model.getNickName());
                    viewHolder.setText(R.id.tv_age, model.getAge() + getString(R.string.text_search_age));
                    viewHolder.setText(R.id.tv_desc, model.getDesc());

                    //设置点击事件
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            UserInfoActivity.startActivity(AddFriendActivity.this,model.getUserId());
                        }
                    });
                }
            }

            @Override
            public int getLayoutId(int type) {
                if(type == TYPE_TITLE){
                    return R.layout.layout_search_title_item;
                }else if(type == TYPE_CONTENT){
                    return R.layout.layout_search_user_item;
                }
                return 0;
            }
        });
        mSearchResultView.setAdapter(mAddFriendAdapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            //跳转到从通讯录导入
//            case R.id.ll_to_contact:
//                //先进行权限判断，因为用户可能一开始同意了权限申请，但是后面又自己设置了拒绝，所以这里需要再次进行权限判断
//                if(checkPermission(Manifest.permission.READ_CONTACTS)){
//                    //startActivity(new Intent(this,ContactFirendActivity.class));
//                }else{
//                    requestPermission(new String[] {Manifest.permission.READ_CONTACTS});
//                }
//                break;
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
        model.setType(TYPE_TITLE);
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
        model.setType(TYPE_CONTENT);
        model.setUserId(imUser.getObjectId());
        model.setPhoto(imUser.getPhoto());
        model.setSex(imUser.isSex());
        model.setAge(imUser.getAge());
        model.setNickName(imUser.getNickName());
        model.setDesc(imUser.getDesc());
        mList.add(model);
    }
}
