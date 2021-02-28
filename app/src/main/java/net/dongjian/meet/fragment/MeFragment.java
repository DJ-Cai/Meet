package net.dongjian.meet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongjian.framwork.base.BaseFragment;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.bmob.IMUser;
import com.dongjian.framwork.helper.GlideHelper;

import net.dongjian.meet.R;
import net.dongjian.meet.ui.MeInfoActivity;
import net.dongjian.meet.ui.NewFriendActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 我的
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView iv_me_photo;
    private TextView tv_nickname;
    private LinearLayout ll_me_info;
    private LinearLayout ll_new_friend;
    private LinearLayout ll_private_set;
    private LinearLayout ll_share;
    private LinearLayout ll_setting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

        iv_me_photo = view.findViewById(R.id.iv_me_photo);
        tv_nickname = view.findViewById(R.id.tv_nickname);

        ll_me_info = view.findViewById(R.id.ll_me_info);
        ll_new_friend = view.findViewById(R.id.ll_new_friend);
        ll_private_set = view.findViewById(R.id.ll_private_set);
        ll_share = view.findViewById(R.id.ll_share);
        ll_setting = view.findViewById(R.id.ll_setting);

        ll_me_info.setOnClickListener(this);
        ll_new_friend.setOnClickListener(this);
        ll_private_set.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        ll_setting.setOnClickListener(this);

        loadMeInfo();
    }

    /**
     * 加载我的个人信息
     */
    private void loadMeInfo() {
        IMUser imUser = BmobManager.getmInstance().getUser();
        GlideHelper.loadSmollUrl(getActivity(), imUser.getPhoto(), 100, 100, iv_me_photo);
        tv_nickname.setText(imUser.getNickName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_me_info:
                //个人信息
                startActivity(new Intent(getActivity(), MeInfoActivity.class));
                break;
            case R.id.ll_new_friend:
                //新朋友
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
                break;
            case R.id.ll_private_set:
                Toast.makeText(getActivity(), "待实现的隐私设置", Toast.LENGTH_SHORT).show();
                //隐私设置
//                startActivity(new Intent(getActivity(), PrivateSetActivity.class));
                break;
            case R.id.ll_share:
                Toast.makeText(getActivity(), "待实现的分享", Toast.LENGTH_SHORT).show();
                //分享
//                startActivity(new Intent(getActivity(), ShareImgActivity.class));
                break;
            case R.id.ll_setting:
                Toast.makeText(getActivity(), "待实现的设置", Toast.LENGTH_SHORT).show();
                //设置
//                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
        }
    }
}
