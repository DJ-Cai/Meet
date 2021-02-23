package com.dongjian.framwork.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * FileName: CommonAdapter
 * Founder: LiuGuiLin
 * Profile: 万能的适配器
 */
public class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    //T：model
    private List<T> mList;

    private OnBindDataListener<T> onBindDataListener;
    private OnMoreBindDataListener<T> onMoreBindDataListener;

    //构造方法
    public CommonAdapter(List<T> mList, OnBindDataListener<T> onBindDataListener) {
        this.mList = mList;
        this.onBindDataListener = onBindDataListener;
    }

    //构造方法
    public CommonAdapter(List<T> mList, OnMoreBindDataListener<T> onMoreBindDataListener) {
        this.mList = mList;
        this.onBindDataListener = onMoreBindDataListener;
        this.onMoreBindDataListener = onMoreBindDataListener;
    }

    //绑定数据的接口   将数据往外抛，让外部进行处理
    public interface OnBindDataListener<T> {
        //mList.get(position), ViewHolder, getItemViewType(position), position
        void onBindViewHolder(T model, CommonViewHolder viewHolder, int type, int position);
        //根据type不同传入不同的layoutID
        int getLayoutId(int type);
    }

    //绑定多类型的数据---适配多type
    public interface OnMoreBindDataListener<T> extends OnBindDataListener<T> {
        int getItemType(int position);
    }

    @Override
    public int getItemViewType(int position) {
        //判断是否是多类型的，是的话返回type，不是返回0即可
        if (onMoreBindDataListener != null) {
            return onMoreBindDataListener.getItemType(position);
        }
        return 0;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //先拿到layoutID
        int layoutId = onBindDataListener.getLayoutId(viewType);
        //通过lauoutId 拿到ViewHolder
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(parent, layoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        onBindDataListener.onBindViewHolder(
                mList.get(position), holder, getItemViewType(position), position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
