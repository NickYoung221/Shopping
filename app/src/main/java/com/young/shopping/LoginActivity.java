package com.young.shopping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.young.application.MyApplication;
import com.young.entity.User;
import com.young.util.PathUrl;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 用户登录的界面
 */
public class LoginActivity extends AppCompatActivity {
    MyApplication myApplication;
    User user;

    @InjectView(R.id.eT_name)
    EditText eTName;
    @InjectView(R.id.eT_pwd)
    EditText eTPwd;
    @InjectView(R.id.cB_1)
    CheckBox cB1;
    @InjectView(R.id.tV_find)
    TextView tVFind;
    @InjectView(R.id.b_login)
    Button bLogin;
    @InjectView(R.id.tv_register)
    TextView tvRegister;
    @InjectView(R.id.iV_head)
    ImageView iVHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        initData();

    }

    void initData(){
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
    }

    @OnClick({R.id.cB_1, R.id.tV_find, R.id.b_login, R.id.tv_register, R.id.iV_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cB_1:
                break;
            case R.id.tV_find:
                break;
            case R.id.b_login://登录
                login();
                new Thread(){//因为多线程机制，可能user还没有被写入到application就finish了，所以这里延迟执行finish
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(500);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                break;
            case R.id.tv_register:
                break;
            case R.id.iV_head:
                break;
        }
    }

    //登录
    void login(){
        String phone = eTName.getText().toString();
        String userPwd = eTPwd.getText().toString();
        RequestParams requestParams = new RequestParams(PathUrl.appUrl+"/LoginServlet");
        requestParams.addBodyParameter("phone",phone);
        requestParams.addBodyParameter("userPwd",userPwd);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int userId = Integer.parseInt(result);
                if(userId==0){
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    getUserById(userId);//改变application里的user对象
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }
            @Override
            public void onCancelled(CancelledException cex) {

            }
            @Override
            public void onFinished() {

            }
        });
    }

    void getUserById(int userId){
        RequestParams requestParams = new RequestParams(PathUrl.appUrl+"/GetUserByIdServlet");
        requestParams.addQueryStringParameter("userId",userId+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                user = gson.fromJson(result,User.class);
                myApplication.setUser(user);  //在Application里维持一个新的User，这里要在获取完数据后立即更改（多线程机制）
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                user = null;
            }
            @Override
            public void onCancelled(CancelledException cex) {

            }
            @Override
            public void onFinished() {

            }
        });
    }



}
