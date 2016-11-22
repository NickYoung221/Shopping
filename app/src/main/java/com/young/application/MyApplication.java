package com.young.application;

import android.app.Application;

import com.young.entity.Address;
import com.young.entity.User;

import org.xutils.x;

/**
 * Created by yang on 2016/10/1 0001.
 */
public class MyApplication extends Application {

    private User user;// = new User(6,"王晓明");//还没写登陆功能，这里暂定用户id为6

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);

    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }




}
