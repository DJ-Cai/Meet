package net.dongjian.meet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dongjian.framwork.adapter.CloudTagAdapter;
import com.dongjian.framwork.base.BaseFragment;
import com.moxun.tagcloudlib.view.TagCloudView;

import net.dongjian.meet.R;
import net.dongjian.meet.ui.AddFriendActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 星球
 */
public class StarFragment extends BaseFragment implements View.OnClickListener {

    private ImageView iv_camera;
    private ImageView iv_add;
    //3DTagCloudView是一个基于ViewGroup实现的控件，支持将一组View展示为一个3D球形集合，并支持全方向滚动。
    private TagCloudView mCloudView;

    private LinearLayout ll_random;
    private LinearLayout ll_soul;
    private LinearLayout ll_fate;
    private LinearLayout ll_love;

    private CloudTagAdapter mCloudTagAdapter;
    private List<String> mFruitList = new ArrayList<>();

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

        //3dCloudTag:填充list内容
        for(int i = 0 ; i < 100 ; i++){
            mFruitList.add("Fruit" + i);
        }
        //3dCloudTag:数据绑定
        mCloudTagAdapter = new CloudTagAdapter(getActivity(),mFruitList);
        mCloudView.setAdapter(mCloudTagAdapter);

        //3dCloudTag:监听点击事件
        mCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
                Toast.makeText(getActivity(), "position" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //扫描
            case R.id.iv_camera:
                break;
            //添加好友
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            //随机匹配算法
            case R.id.ll_random:
                break;
            //灵魂匹配算法
            case R.id.ll_soul:
                break;
            //
            case R.id.ll_fate:
                break;
            //
            case R.id.ll_love:
                break;


        }

    }
}
