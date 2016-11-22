package com.young.shopping;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.young.fragment.EditNameFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 修改昵称、性别通用的界面
 */
public class EditInfoActivity extends AppCompatActivity {

    @InjectView(R.id.imageView1)
    ImageView imageView1;
    @InjectView(R.id.textView1)
    TextView textView1;
    @InjectView(R.id.save)
    Button save;
    @InjectView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @InjectView(R.id.fl_info)
    FrameLayout flInfo;

    String flag;

    EditNameFragment editNameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ButterKnife.inject(this);

        editNameFragment=new EditNameFragment();

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        //判断flag：修改姓名，性别
        switch (flag) {
            case "name":
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fl_info, editNameFragment).commit();

                break;
            case "sex":

                break;

        }

    }

    //返回前一个界面

    @OnClick({R.id.save, R.id.imageView1})
    public void onClick(View view) {
        //判断：修改是什么信息
        switch (flag){
            case "name":
                //获取EditNameFragment中输入框的值
                EditText etName= (EditText) editNameFragment.getView().findViewById(R.id.et_edit_name);
                String name=  etName.getText().toString();

                //传值给ModifyActivity; 关闭本身的activity

                Intent intent=new Intent();
                intent.putExtra("name",name);
                setResult(RESULT_OK,intent);

                finish();
                break;
        }
        if(view.getId()==R.id.imageView1){
            finish();
        }
    }




}
