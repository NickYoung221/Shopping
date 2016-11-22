package com.young.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.young.application.MyApplication;
import com.young.entity.Order;
import com.young.entity.User;
import com.young.shopping.AllOrdersActivity;
import com.young.shopping.LoginActivity;
import com.young.shopping.ModifyPersonInfoActivity;
import com.young.shopping.R;
import com.young.util.PathUrl;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/** 个人中心的Fragment
 * Created by yang on 2016/9/30 0030.
 */
public class PersonCenterFragment extends BaseFragment {
    MyApplication myApplication;
    User user;

    @InjectView(R.id.person_center_header_iv)
    ImageView personCenterHeaderIv;
    @InjectView(R.id.person_center_header_pnum)
    TextView personCenterHeaderPnum;
    @InjectView(R.id.iv_edit_person_info)
    ImageView ivEditPersonInfo;
    @InjectView(R.id.person_center_content_personinfo)
    RelativeLayout personCenterContentPersoninfo;
    @InjectView(R.id.iv_show_mycollect)
    ImageView ivShowMycollect;
    @InjectView(R.id.person_center_content_mycollect)
    RelativeLayout personCenterContentMycollect;
    @InjectView(R.id.iv_show_myorder)
    ImageView ivShowMyorder;
    @InjectView(R.id.person_center_content_myorder)
    RelativeLayout personCenterContentMyorder;
    @InjectView(R.id.iv_show_myaddress)
    ImageView ivShowMyaddress;
    @InjectView(R.id.person_center_content_myaddress)
    RelativeLayout personCenterContentMyaddress;

    public static final int login = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_person_center, null);
        ButterKnife.inject(this, v);
        return v;

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        myApplication = (MyApplication) getActivity().getApplication();
        user = myApplication.getUser();

        if(user.getImageUrl()!=null){
            personCenterHeaderPnum.setText(user.getNick());
        }
    }

    @Override
    public void initEvent() {
        setUserImage(); //从网络获取用户头像数据，显示在ImageView上面
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.person_center_header_iv, R.id.person_center_content_personinfo, R.id.person_center_content_mycollect,
            R.id.person_center_content_myorder, R.id.person_center_content_myaddress, R.id.person_center_header_pnum})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.person_center_header_iv:
                break;
            case R.id.person_center_content_personinfo:
                //跳转到修改个人信息的页面
                Intent intent2 = new Intent(getActivity(), ModifyPersonInfoActivity.class);
                getActivity().startActivityForResult(intent2,login);
                break;
            case R.id.person_center_content_mycollect:
                break;
            case R.id.person_center_content_myorder://跳转到订单页面
                Intent intent4 = new Intent(getActivity(), AllOrdersActivity.class);
                startActivity(intent4);
                break;
            case R.id.person_center_content_myaddress:
                break;
            case R.id.person_center_header_pnum: //跳转到登录页面
                if(user.getImageUrl()==null){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(intent,login);
                }
                break;
        }
    }

    //从网络获取用户头像数据，显示在ImageView上面
    public void setUserImage(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String imageUrl = PathUrl.imageUrl+user.getImageUrl();
                Log.i("Activity", "run: "+imageUrl);
                //设置图片样式
                ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true)
                        .setFailureDrawableId(R.mipmap.ic_launcher)//失败图片
                        .setLoadingDrawableId(R.mipmap.ic_launcher) //加载时的图片
                        .setCrop(true)   //是否裁剪？
                        .setAutoRotate(true).build();
                x.image().bind(personCenterHeaderIv,imageUrl,imageOptions);
            }
        }.start();
    }

    //Fragment对于用户是否可见时会执行该方法(ViewPager里嵌套Fragment执行该方法)
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser){  //当该页面对用户可见时，再获取服务器数据
//            Log.i("PersonCenterFragment", "initEvent: 33333333333333");
//            setUserImage();
//        }
//    }

    //Fragment显示或隐藏会回调该方法
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
//            Log.i("PersonCenterFragment", "initEvent: 22222222222222222");
            //initData();
            setUserImage();
        }
    }

}
