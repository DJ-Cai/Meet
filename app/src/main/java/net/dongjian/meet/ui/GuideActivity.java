package net.dongjian.meet.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.dongjian.framwork.base.BasePageAdapter;
import com.dongjian.framwork.base.BaseUIActivity;
import com.dongjian.framwork.manager.MediaPlayerManager;
import com.dongjian.framwork.utils.AnimUtils;

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
public class GuideActivity extends BaseUIActivity implements View.OnClickListener {

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

    //歌曲部分
    private MediaPlayerManager mGuideMusic;

    //音乐图标的旋转
    private ObjectAnimator mAnim;

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

        //音乐事件：点击音乐图案，音乐暂停。
        iv_music_switch.setOnClickListener(this);
        //text事件：跳过
        tv_guide_skip.setOnClickListener(this);

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

        //2、小圆点逻辑--监听ViewPager的滑动事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        
        //3、歌曲的逻辑
        startMusic();

        //4、旋转属性动画
        mAnim = AnimUtils.rotation(iv_music_switch);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击的是音乐按钮
            case R.id.iv_music_switch:
                //要判断当前音乐状态，如果是正在播放，则暂停；如果是暂停，则播放
                if(mGuideMusic.MEDIA_STATUS == MediaPlayerManager.MEDIA_STATUS_PAUSE){
                    //设置图标图片为正在播放的图片
                    iv_music_switch.setImageResource(R.drawable.img_guide_music);
                    //属性动画开始播放
                    mAnim.start();
                    //音乐继续播放
                    mGuideMusic.continuePlay();
                }else if(mGuideMusic.MEDIA_STATUS == MediaPlayerManager.MEDIA_STATUS_PLAY){
                    iv_music_switch.setImageResource(R.drawable.img_guide_music_off);
                    mAnim.pause();
                    mGuideMusic.pausePlay();
                }
                break;
            //点击的是跳过按钮
            case R.id.tv_guide_skip:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
        }
    }


    /**
     *动态选择小圆点
     */
    private void selectedPoint(int position){
        switch (position){
            case 0 :
                iv_guide_point_1.setImageResource(R.drawable.img_guide_point_p);
                iv_guide_point_2.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_3.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_4.setImageResource(R.drawable.img_guide_point);
                break;
            case 1 :
                iv_guide_point_1.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_2.setImageResource(R.drawable.img_guide_point_p);
                iv_guide_point_3.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_4.setImageResource(R.drawable.img_guide_point);
                break;
            case 2 :
                iv_guide_point_1.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_2.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_3.setImageResource(R.drawable.img_guide_point_p);
                iv_guide_point_4.setImageResource(R.drawable.img_guide_point);
                break;
            case 3 :
                iv_guide_point_1.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_2.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_3.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_4.setImageResource(R.drawable.img_guide_point_p);
                break;
        }
    }

    /**
     *播放音乐
     */
    private void startMusic() {
        mGuideMusic = new MediaPlayerManager();
        mGuideMusic.setLooping(true);
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.test);
        mGuideMusic.startPlay(file);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuideMusic.stopPlay();
    }
}
