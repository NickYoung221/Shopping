package com.young.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yang on 2016/9/30 0030.
 */
public abstract class BaseFragment extends Fragment{

    //找控件
    //界面数据初始化
    //设置事件

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initData();
        initEvent();

    }

    public abstract void initView(); //找控件
    public abstract void initData();//设置界面初始值
    public abstract void initEvent();//设置控件的事件




}
