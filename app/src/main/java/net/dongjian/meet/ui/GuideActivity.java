package net.dongjian.meet.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.dongjian.framwork.base.BasePageAdapter;
import com.dongjian.framwork.base.BaseUIActivity;

import net.dongjian.meet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 * 1、ViewPager ： 适配器 | 帧动画播放
 * 2、小圆点
 * 3、歌曲的播放
 * 4、属性动画 ： 旋转
 * 5、跳转
 */
public class GuideActivity extends BaseUIActivity {

    //界面控件:音乐图片、跳过文字、四个白点、ViewPager
    private ImageView iv_music_switch;
    private TextView tv_guide_skip;
    private ImageView iv_guide_point_1;
    private ImageView iv_guide_point_2;
    private ImageView iv_guide_point_3;
    private ImageView iv_guide_point_4;
    private ViewPager mViewPager;

    private View view1;
    private View view2;
    private View view3;
    private View view4;

    //1-1、ViewPager - 适配器
    private List<View> mPageList = new ArrayList<>();
    private BasePageAdapter mPagerAdapter;

    //1-2、ViewPager - 帧动画
    //他们的findViewById走的是对于view了
    private ImageView iv_guide_night_1;
    private ImageView iv_guide_smile_1;
    private ImageView iv_guide_night_2;
    private ImageView iv_guide_smile_2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
    }

    private void initView(){
        iv_music_switch = findViewById(R.id.iv_music_switch);
        tv_guide_skip = findViewById(R.id.tv_guide_skip);
        iv_guide_point_1 = findViewById(R.id.iv_guide_point_1);
        iv_guide_point_2 = findViewById(R.id.iv_guide_point_2);
        iv_guide_point_3 = findViewById(R.id.iv_guide_point_3);
        iv_guide_point_4 = findViewById(R.id.iv_guide_point_4);
        mViewPager = findViewById(R.id.mViewPager);

        view1 = View.inflate(this, R.layout.layout_pager_guide_1, null);
        view2 = View.inflate(this, R.layout.layout_pager_guide_2, null);
        view3 = View.inflate(this, R.layout.layout_pager_guide_3, null);
        view4 = View.inflate(this,R.layout.layout_pager_guide_4,null);

        mPageList.add(view1);
        mPageList.add(view2);
        mPageList.add(view3);
        mPageList.add(view4);

        //优化： 缓存-预加载（不然老是多次尝试关闭）
        mViewPager.setOffscreenPageLimit(mPageList.size());

        //1-1、创建适配器
        mPagerAdapter = new BasePageAdapter(mPageList);
        mViewPager.setAdapter(mPagerAdapter);

        //1-2-1、帧动画部分
        iv_guide_night_1 = view1.findViewById(R.id.iv_guide_smile_1);
        iv_guide_smile_1 = view2.findViewById(R.id.iv_guide_night_1);
        iv_guide_night_2 = view3.findViewById(R.id.iv_guide_smile_2);
        iv_guide_smile_2 = view4.findViewById(R.id.iv_guide_night_2);

        //1-2-2、播放帧动画
        AnimationDrawable animNight_1 = (AnimationDrawable) iv_guide_night_1.getBackground();
        animNight_1.start();
        AnimationDrawable animSmile_1 = (AnimationDrawable) iv_guide_smile_1.getBackground();
        animSmile_1.start();
        AnimationDrawable animNight_2 = (AnimationDrawable) iv_guide_night_2.getBackground();
        animNight_2.start();
        AnimationDrawable animSmile_2 = (AnimationDrawable) iv_guide_smile_2.getBackground();
        animSmile_2.start();
    }


}
