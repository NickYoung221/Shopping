package com.young.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.young.application.MyApplication;
import com.young.shopping.R;

/** 修改姓名的Fragment
 * Created by yang on 2016/10/11 0011.
 */
public class EditNameFragment extends BaseFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_name,null);

        EditText etName= (EditText) v.findViewById(R.id.et_edit_name);
        //初始化姓名
        etName.setText(((MyApplication)getActivity().getApplication()).getUser().getUserName());

        return v;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }
}
