package com.young.shopping;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.young.application.MyApplication;
import com.young.entity.User;
import com.young.util.PathUrl;
import com.young.widget.TitleBar;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 修改个人信息的页面
 */
public class ModifyPersonInfoActivity extends AppCompatActivity {

    MyApplication myApplication;
    User user;

    @InjectView(R.id.tv_headimage)
    TextView tvHeadimage;
    @InjectView(R.id.iv_head_image)
    ImageView ivHeadImage;
    @InjectView(R.id.iv_edit_head_image)
    ImageView ivEditHeadImage;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_real_name)
    TextView tvRealName;
    @InjectView(R.id.iv_edit_name)
    ImageView ivEditName;
    @InjectView(R.id.tv_sex)
    TextView tvSex;
    @InjectView(R.id.tv_real_sex)
    TextView tvRealSex;
    @InjectView(R.id.iv_edit_sex)
    ImageView ivEditSex;
    @InjectView(R.id.rl_edit_sex)
    RelativeLayout rlEditSex;
    @InjectView(R.id.tv_pwd)
    TextView tvPwd;
    @InjectView(R.id.iv_edit_pwd)
    ImageView ivEditPwd;
    @InjectView(R.id.rl_edit_pwd)
    RelativeLayout rlEditPwd;
    @InjectView(R.id.rl_edit_head_image)
    RelativeLayout rlEditHeadImage;
    @InjectView(R.id.rl_edit_name)
    RelativeLayout rlEditName;
    ImageView return_image;//返回按钮

    //private File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + getPhotoFileName());
    //private Uri imageUri = Uri.fromFile(file);

    private static final int SELECT_FROM_ALBUM = 1;
    private static final int SELECT_FROM_CAMERA = 2;
    private static final int CROP = 3;

    private static final int updateName = 4;
    private static final int updateSex = 5;

    private File file;  //相机拍摄照片和视频的标准目录
    private Uri imageUri;       //头像的uri
    private String imageUrl;   //头像存在服务器的url地址


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_person_info);
        ButterKnife.inject(this);

//        File file = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
//        imageUri = Uri.fromFile(file);

        initView();
        initData();
        initEvent();

        //判断sd卡是否存在，
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ){
            //目录，文件名Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            String photoFileName = getPhotoFileName();
            file=new File(Environment.getExternalStorageDirectory(),photoFileName);
            imageUri= Uri.fromFile(file);
            imageUrl = "/"+user.getUserId()+"/"+photoFileName;//头像存在服务器的url地址
        }

    }

    void initView(){
        TitleBar titleBar = (TitleBar) findViewById(R.id.titleBar_personalInfo);
        return_image = titleBar.getLeftImage();
    }

    void initData() {
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();

    }

    void initEvent() {
        return_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.rl_edit_head_image, R.id.rl_edit_name, R.id.rl_edit_sex, R.id.rl_edit_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_edit_head_image: //点击修改头像
                String[] items = {"从相册选取", "相机"};
                //点击出现弹框
                new AlertDialog.Builder(this).setTitle("选择图片来源").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0: //从相册选取
                                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //用下面的方式
                                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                                //intent.setType("image/*");  //设置图片类型
                                //startActivityForResult(Intent.createChooser(intent,"选择图片"),SELECT_FROM_ALBUM);

                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent,SELECT_FROM_ALBUM);
                                break;
                            case 1: //从相机选取
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));  //???
                                startActivityForResult(intent2, SELECT_FROM_CAMERA);
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.rl_edit_name:
                //修改姓名
                Intent intent=new Intent(this,EditInfoActivity.class);
                intent.putExtra("flag","name");
                startActivityForResult(intent,updateName);//请求码
                break;
            case R.id.rl_edit_sex:
                //修改性别
                Intent intent2=new Intent(this,EditInfoActivity.class);
                intent2.putExtra("flag","sex");
                startActivityForResult(intent2,updateSex);//请求码
                break;
            case R.id.rl_edit_pwd:
                break;
        }
    }

    //startActivityForResult()方法的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case SELECT_FROM_ALBUM: //从相册选取
                if(data!=null){
                    crop(data.getData());
                }
                break;
            case SELECT_FROM_CAMERA: //相机拍照选取
                crop(Uri.fromFile(file));
                break;
            case CROP: //裁剪
                if(data!=null){
                    Bundle bundle = data.getExtras();
                    if(bundle!=null){
                        Bitmap bitmap = bundle.getParcelable("data");
                        showImage(bitmap);
                    }
                }
                break;
            case updateName: //修改名字
                //修改姓名返回
                //获取修改的姓名值
                String name=data.getStringExtra("name");
                //显示在tv上
                tvRealName.setText(name);
                break;
            case updateSex: //修改性别

                break;
        }

    }

    //获取文件路径
    private String getPhotoFileName() {        //文件名：当前时间.png
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    //裁剪图片
    public void crop(Uri uri){
        // intent.setType("image/*");
        //裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        //宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //定义宽和高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        //是否要返回值
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP);
    }

    //裁剪图片
//    void crop(Intent intent){
//        //裁剪
//        intent.putExtra("crop", "true");
//        //宽高比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        //定义宽和高
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);
//        //图片是否缩放
//        intent.putExtra("scale", true);
//        //是否要返回值
//        intent.putExtra("return-data", false);
//        //把图片存放到imageUri
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        //图片输出格式
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true); // no face detection
//    }

    //显示图片，上传服务器
    public void showImage(Bitmap bitmap){
        ivHeadImage.setImageBitmap(bitmap); //iv显示图片
        saveImage(bitmap); //保存文件
        new Thread(){
            @Override
            public void run() {
                super.run();
                setImageUrl(); //修改数据库里用户的头像地址
                uploadImage(); //上传服务器
            }
        }.start();
    }

    //将bitmap保存在文件中
    public void saveImage(Bitmap bitmap){
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(fos!=null){
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //上传图片到Servlet
    public void uploadImage(){
        RequestParams requestParams=new RequestParams(PathUrl.appUrl+"/UploadImageServlet");
        requestParams.setMultipart(true);
        requestParams.addBodyParameter("userId",user.getUserId()+"");
        requestParams.addBodyParameter("file",file);

        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(file.exists()){
                    file.delete();//删除文件，不占用用户存储空间
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

    //修改数据库里用户的图片地址
    void setImageUrl(){
        RequestParams requestParams = new RequestParams(PathUrl.appUrl+"/UpdateUserServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        requestParams.addQueryStringParameter("imageUrl",imageUrl);

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                user.setImageUrl(imageUrl);
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


}
