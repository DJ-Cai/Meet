package net.dongjian.meet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongjian.framwork.base.BaseFragment;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.bmob.IMUser;
import com.dongjian.framwork.helper.PairFriendHelper;
import com.dongjian.framwork.utils.CommonUtils;
import com.dongjian.framwork.view.DialogView;
import com.dongjian.framwork.view.LoadingView;
import com.moxun.tagcloudlib.view.TagCloudView;

import net.dongjian.meet.R;
import net.dongjian.meet.adapter.CloudTagAdapter;
import net.dongjian.meet.model.StarModel;
import net.dongjian.meet.ui.AddFriendActivity;
import net.dongjian.meet.ui.UserInfoActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 星球
 */
public class StarFragment extends BaseFragment implements View.OnClickListener {

    private ImageView iv_camera;
    private ImageView iv_add;
    //3DTagCloudView是一个基于ViewGroup实现的控件，支持将一组View展示为一个3D球形集合，并支持全方向滚动。
    private TagCloudView mCloudView;

    private LoadingView mLoadingView;

    private DialogView mNullDialogView;
    private TextView tv_null_text;

    private LinearLayout ll_random;
    private LinearLayout ll_soul;
    private LinearLayout ll_fate;
    private LinearLayout ll_love;

    private CloudTagAdapter mCloudTagAdapter;
    private List<StarModel> mStarList = new ArrayList<>();

    //连接状态
    //private TextView tv_connect_status;

    //全部用户
    private List<IMUser> mAllUserList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        iv_camera = view.findViewById(R.id.iv_camera);
        iv_add = view.findViewById(R.id.iv_add);

        mCloudView = view.findViewById(R.id.mCloudView);

        mLoadingView = new LoadingView(getActivity());

        ll_random = view.findViewById(R.id.ll_random);
        ll_soul = view.findViewById(R.id.ll_soul);
        ll_fate = view.findViewById(R.id.ll_fate);
        ll_love = view.findViewById(R.id.ll_love);

        iv_camera.setOnClickListener(this);
        iv_add.setOnClickListener(this);

        ll_random.setOnClickListener(this);
        ll_soul.setOnClickListener(this);
        ll_fate.setOnClickListener(this);
        ll_love.setOnClickListener(this);

        //3dCloudTag:数据绑定
        mCloudTagAdapter = new CloudTagAdapter(getActivity(), mStarList);
        mCloudView.setAdapter(mCloudTagAdapter);

        //3dCloudTag:监听点击事件
        mCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
                startUserInfo(mStarList.get(position).getUserId());
            }
        });

        //绑定接口---四大算法的结果回调
        PairFriendHelper.getInstance().setOnPairResultListener(new PairFriendHelper.OnPairResultListener() {

            //获取成功，则跳转到得到的userId的用户界面
            @Override
            public void OnPairListener(String userId) {
                startUserInfo(userId);
            }

            @Override
            public void OnPairFailListener() {
                mLoadingView.hide();
                Toast.makeText(getActivity(), getString(R.string.text_pair_null), Toast.LENGTH_SHORT).show();
            }
        });

        //3dView的好友显示
        loadStarUser();

    }

    /**
     * 跳转用户信息
     *
     * @param userId
     */
    private void startUserInfo(String userId) {
        mLoadingView.hide();
        UserInfoActivity.startActivity(getActivity(), userId);
    }

    /**
     * 加载星球用户
     */
    private void loadStarUser() {
        /**
         * 我们从用户库中取抓取一定的好友进行匹配
         */
        BmobManager.getmInstance().queryAllUser(new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isNotEmpty(list)) {
                        //把旧数据进行清理，不然可能会重复
                        if (mAllUserList.size() > 0) {
                            mAllUserList.clear();
                        }
                        if (mStarList.size() > 0) {
                            mStarList.clear();
                        }
                        //赋值新数据
                        mAllUserList = list;

                        //这里是所有的用户 只适合现在的小批量（大用户得用到大数据了）
                        int index = 50;
                        if (list.size() <= 50) {
                            index = list.size();
                        }
                        //直接填充
                        for (int i = 0; i < index; i++) {
                            IMUser imUser = list.get(i);
                            saveStarUser(imUser.getObjectId(),
                                    imUser.getNickName(),
                                    imUser.getPhoto());
                        }
                        mCloudTagAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * 保存星球用户
     *
     * @param userId
     * @param nickName
     * @param photoUrl
     */
    private void saveStarUser(String userId, String nickName, String photoUrl) {
        StarModel model = new StarModel();
        model.setUserId(userId);
        model.setNickName(nickName);
        model.setPhotoUrl(photoUrl);
        mStarList.add(model);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //扫描
            case R.id.iv_camera:
                Toast.makeText(getActivity(), "待实现", Toast.LENGTH_SHORT).show();
                break;
            //添加好友
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            //随机匹配算法
            case R.id.ll_random:
                pairUser(0);
                break;
            case R.id.ll_soul:
//
//                if(TextUtils.isEmpty(BmobManager.getmInstance().getUser().getConstellation())){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_1));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }
//
//                if(BmobManager.getmInstance().getUser().getAge() == 0){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_2));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }
//
//                if(TextUtils.isEmpty(BmobManager.getmInstance().getUser().getHobby())){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_3));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }
//
//                if(TextUtils.isEmpty(BmobManager.getmInstance().getUser().getStatus())){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_4));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }

                //灵魂匹配
                pairUser(1);
                break;
            case R.id.ll_fate:
                //缘分匹配
                pairUser(2);
                break;
            case R.id.ll_love:
                //恋爱匹配
                pairUser(3);
                break;


        }

    }

    private void pairUser(int i) {
        switch (i) {
            case 0:
                mLoadingView.show(getString(R.string.text_pair_random));
                break;
            case 1:
                mLoadingView.show(getString(R.string.text_pair_soul));
                break;
            case 2:
                mLoadingView.show(getString(R.string.text_pair_fate));
                break;
            case 3:
                mLoadingView.show(getString(R.string.text_pair_love));
                break;
        }
        //开始计算
        if (CommonUtils.isNotEmpty(mAllUserList)) {
            //计算
            PairFriendHelper.getInstance().pairUser(i, mAllUserList);
        } else {
            mLoadingView.hide();
        }
    }
}
