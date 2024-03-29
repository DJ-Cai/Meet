package com.dongjian.framwork.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * FileName: HeadZoomScrollView
 * Profile: 头部拉伸的View
 * 1.获取头部的View(操控的View)
 * 2.滑动事件的处理
 */
public class HeadZoomScrollView extends ScrollView {

    //头部View
    private View mZoomView;
    private int mZoomViewWidth;
    private int mZoomViewHeight;

    //是否在滑动
    private boolean isScrolling = false;
    //第一次按下的坐标
    private float firstPosition;
    //滑动系数
    private float mScrollRate = 0.3f;
    //回弹系数
    private float mReplyRate = 0.5f;

    public HeadZoomScrollView(Context context) {
        super(context);
    }

    public HeadZoomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadZoomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 布局加载完以后就可以进行获取操作了
     * 1.获取头部的View(操控的View)
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //getChildAt：返回组中指定位置的视图
        if (getChildAt(0) != null) {
            ViewGroup vg = (ViewGroup) getChildAt(0);
            //因为view是多层嵌套的，所以得判断是否是最底层的。是 才能赋值给mZoomView
            if (vg.getChildAt(0) != null) {
                mZoomView = vg.getChildAt(0);
            }
        }
    }

    /**
     * 2.滑动事件的处理
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //获取View的宽高
        if (mZoomViewWidth <= 0 || mZoomViewHeight <= 0) {
            mZoomViewWidth = mZoomView.getMeasuredWidth();
            mZoomViewHeight = mZoomView.getMeasuredHeight();
        }

        switch (ev.getAction()) {
            //滑动
            case MotionEvent.ACTION_MOVE:
                if (!isScrolling) {
                    //说明没有滑动
                    if (getScrollY() == 0) {
                        //没有滑动说明是第一次滑动 记录
                        firstPosition = ev.getY();
                    } else {
                        break;
                    }
                }
                //计算缩放值
                //公式：(当前的位置 - 第一次按下的位置) * 缩放系数
                int distance = (int) ((ev.getY() - firstPosition) * mScrollRate);
                if (distance < 0) {
                    break;
                }
                //到这里证明正在滑动
                isScrolling = true;
                setZoomView(distance);
                break;
            //回弹：滑动结束
            case MotionEvent.ACTION_UP:
                isScrolling = false;
                replyZoomView();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 回调动画
     */
    private void replyZoomView() {
        //计算下拉的缩放值再让属性动画根据这个值复原
        int distance = mZoomView.getMeasuredWidth() - mZoomViewWidth;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distance,0)
                .setDuration((long) (distance * mReplyRate));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setZoomView((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    /**
     * 缩放View：getLayoutParams
     *
     * @param zoom
     */
    private void setZoomView(float zoom) {
        if (mZoomViewWidth <= 0 || mZoomViewHeight <= 0) {
            return;
        }
        ViewGroup.LayoutParams lp = mZoomView.getLayoutParams();
        lp.width = (int) (mZoomViewWidth + zoom);
        // 现在的宽/原本的宽 得到 缩放比例  再 * 原本的高 得到缩放的高
        lp.height = (int) (mZoomViewHeight * ((mZoomViewWidth + zoom) / mZoomViewWidth));
        //设置间距：不然下拉过程中 头像会发生偏移
        //公式：- (lp.width - mZoomViewWidth) / 2
        ((MarginLayoutParams) lp).setMargins(-(lp.width - mZoomViewWidth) / 2, 0, 0, 0);
        mZoomView.setLayoutParams(lp);
    }
}
