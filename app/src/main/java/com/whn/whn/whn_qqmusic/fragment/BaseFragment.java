package com.whn.whn.whn_qqmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by fullcircle on 2017/1/15.
 */

public abstract class BaseFragment extends Fragment {


    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), null);
        }
//        else{
//            Log.e(getClass().getSimpleName(),"rootView复用");
//        }
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    /**
     * 获取当前fragment的布局文件的id
     *
     * @return
     */
    public abstract int getLayoutId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    protected abstract void initView(View view);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
