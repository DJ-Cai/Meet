package com.dongjian.framwork.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

/**
 * PagerAdapter基类
 */
public class BasePageAdapter extends PagerAdapter {

    private List<View> mList;

    public BasePageAdapter(List<View> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 创建Item
     */
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //1、将ViewGroup强转成ViewPager 然后添加视图（view通过list里的位置获得）
        ((ViewPager) container).addView(mList.get(position));
        //返回添加的视图
        return mList.get(position);
    }

    /**
     * 销毁Item
     */
    @Override
    public void destroyItem(ViewGroup container, int position,Object object) {
        ((ViewPager) container).removeView(mList.get(position));
        super.destroyItem(container, position, object);
    }
}
