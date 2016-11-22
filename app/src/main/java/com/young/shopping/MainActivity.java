package com.young.shopping;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.young.application.MyApplication;
import com.young.entity.User;
import com.young.fragment.MainPageFragment;
import com.young.fragment.PersonCenterFragment;
import com.young.fragment.ShopingCartFragment;

import c.b.BP;

public class MainActivity extends AppCompatActivity {

    //Appid
    String APPID = "11e93b5e5fad3225bebec3fde45839e7";
    // 此为支付插件的官方最新版本号,请在更新时留意更新说明
    int PLUGINVERSION = 7;

    Fragment[] fragments;
    MainPageFragment mainPageFragment;//主页
    ShopingCartFragment shopingCartFragment;//购物车
    PersonCenterFragment personCenterFragment ;//个人中心
    //按钮的数组，一开始第一个按钮被选中
    Button[] tabs;

    int oldIndex;//用户看到的item
    int newIndex;//用户即将看到的item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BP.init();
        BP.init(this,APPID);
        MyApplication myApplication = (MyApplication) getApplication();
        myApplication.setUser(new User(1,"name"));

        //初始化fragment
        mainPageFragment=new MainPageFragment();
        shopingCartFragment=new ShopingCartFragment();
        personCenterFragment=new PersonCenterFragment();

        //所有fragment的数组
        fragments=new Fragment[]{mainPageFragment,shopingCartFragment,personCenterFragment};

        //设置按钮的数组
        tabs=new Button[3];
        tabs[0]=(Button) findViewById(R.id.btn_main_page);//主页的button
        tabs[1]=(Button) findViewById(R.id.btn_shoping_cart);//主页的button
        tabs[2]=(Button) findViewById(R.id.btn_person_center);//主页的button
        //界面初始显示第一个fragment;添加第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0]).commit();
        //初始时，按钮1选中
        tabs[0].setSelected(true);
    }

    //按钮的点击事件:选中不同的按钮，不同的fragment显示
    public void onTabClicked(View view) {

        //点击按钮时，表示选中不同的项
        switch(view.getId()){
            case R.id.btn_main_page:
                newIndex=0;//选中第一项
                break;
            case R.id.btn_shoping_cart:
                newIndex=1;//选中第二项
                break;
            case R.id.btn_person_center:
                newIndex=2;//选中第三项
                break;
        }

        switchFragment();
    }

    //切换显示的Fragment
    void switchFragment(){
        FragmentTransaction transaction;
        //如果选择的项不是当前选中项，则替换；否则，不做操作
        if(newIndex!=oldIndex){
            transaction=getSupportFragmentManager().beginTransaction();

            transaction.hide(fragments[oldIndex]);//隐藏当前显示项

            //如果选中项没有加过，则添加
            if(!fragments[newIndex].isAdded()){
                //添加fragment
                transaction.add(R.id.fragment_container,fragments[newIndex]);
            }
            //显示当前选择项
            //transaction.show(fragments[newIndex]).commit(); //要使用下面的方法
            transaction.show(fragments[newIndex]).commitAllowingStateLoss();
        }
        //之前选中的项，取消选中
        tabs[oldIndex].setSelected(false);
        //当前选择项，按钮被选中
        tabs[newIndex].setSelected(true);

        //当前选择项变为选中项
        oldIndex=newIndex;

    }

    /*  @Override //这种方法不行，会报错
    protected void onRestart() {
        super.onRestart();
        Log.i("MainActivity", "onRestart: ");
        newIndex=1;
        switchFragment();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MainActivity", "onActivityResult1: "+requestCode+"---"+resultCode);
        if(requestCode==MainPageFragment.requestCode&&resultCode==ProductInfoActivity.resultCode){
            newIndex = 1;
            switchFragment();
        }else if(requestCode==ShopingCartFragment.requestCode){
            //onRestart(); 不能重新加载页面
            //表明是从购物车结算界面返回到ShopingCartFragment
            //shopingCartFragment.clearCheckStatus(); //清空所有选中状态,(在跳转完的时候执行了,这里就不需要了)
        }else if(requestCode==PersonCenterFragment.login){
            personCenterFragment.initData();//重新初始化数据
            personCenterFragment.setUserImage();//网络获取用户头像数据，显示在ImageView上面
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //用户按下返回键（最后退出该应用）会执行该方法
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Log.i("MainActivity", "onBackPressed: ");
    }

    
}
