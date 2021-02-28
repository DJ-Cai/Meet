package net.dongjian.meet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongjian.framwork.base.BaseFragment;

import net.dongjian.meet.R;

public class SquareFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_empty_view, null);
        return view;
    }
}
