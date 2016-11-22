package com.young.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.young.application.MyApplication;
import com.young.entity.Cart;
import com.young.entity.Product;
import com.young.entity.User;
import com.young.util.PathUrl;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 商品详情的页面
 */
public class ProductInfoActivity extends AppCompatActivity {

    MyApplication myApplication;
    User user;
    public static final int resultCode = 4;

    @InjectView(R.id.prod_info_cart)
    Button prodInfoCart;
    @InjectView(R.id.prod_info_nowbuy)
    Button prodInfoNowbuy;
    @InjectView(R.id.prod_info_bottom)
    RelativeLayout prodInfoBottom;
    @InjectView(R.id.prod_image)
    ImageView prodImage;
    @InjectView(R.id.prod_info_tv_des)
    TextView prodInfoTvDes;
    @InjectView(R.id.prod_info_tv_price)
    TextView prodInfoTvPrice;
    @InjectView(R.id.prod_info_tv_pnum)
    TextView prodInfoTvPnum;
    @InjectView(R.id.prod_info_tv_fapiao)
    TextView prodInfoTvFapiao;
    @InjectView(R.id.prod_info_tv_baoyou)
    TextView prodInfoTvBaoyou;
    @InjectView(R.id.prod_info_tv_buynum)
    TextView prodInfoTvBuynum;
    @InjectView(R.id.subbt)
    Button subbt;
    @InjectView(R.id.edt)
    TextView edt;
    @InjectView(R.id.addbt)
    Button addbt;
    @InjectView(R.id.prod_info_tv_prod_record)
    TextView prodInfoTvProdRecord;
    @InjectView(R.id.lv_user_remark)
    ListView lvUserRemark;
    @InjectView(R.id.prod_info_tv_prod_comment)
    TextView prodInfoTvProdComment;
    @InjectView(R.id.prod_info_linearly)
    LinearLayout prodInfoLinearly;
    @InjectView(R.id.prod_info_scrollView)
    ScrollView prodInfoScrollView;
    @InjectView(R.id.id_prod_list_iv_left)
    ImageView idProdListIvLeft;

    TextView titleBarReddot;
    RelativeLayout titleBarRlCartView;

    Product product;   //商品信息
    int allNumber;     //所有购物车中商品数量


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        ButterKnife.inject(this);
        initView();
        initData();
        initEvent();
    }

    //初始化View，一般用来找控件
    void initView() {
        titleBarReddot = (TextView) findViewById(R.id.title_bar_reddot);//购物车图片上显示的数量
        titleBarRlCartView = (RelativeLayout) findViewById(R.id.title_bar_rl_cartview);//购物车图标的布局

    }

    //初始化界面
    void initData() {
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();

        //获取传过来的ProductInfo
        Intent intent = getIntent();
        product = intent.getParcelableExtra("productInfo");
        if (product != null) {
            //设置商品信息
            prodInfoTvDes.setText(product.getName());
            prodInfoTvPrice.setText(product.getPrice() + "");
            prodInfoTvPnum.setText("已售出" + product.getProductSaleNumber() + "件");
            //...
        }
        //获取网络数据，显示用户加入购物车的商品的总数量
        RequestParams requestParams = new RequestParams(PathUrl.appUrl + "/QueryCartServlet");
        requestParams.addQueryStringParameter("userId", user.getUserId() + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Cart>>() {
                }.getType();
                List<Cart> carts = gson.fromJson(result, type);
                if (carts != null && carts.size() > 0) {
                    //取出购物车中所有物品的总数
                    for (Cart cart : carts) {
                        allNumber += cart.getCollectNumber();
                    }
                    //显示在图标上
                    setTitleBarRedDotText();
                } else {
                    titleBarReddot.setVisibility(View.GONE); //设置TextView不可见
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

    //初始化事件
    void initEvent() {
        //购物车图标所在布局的点击事件
        titleBarRlCartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(resultCode);  //设置结果码
                finish();
            }
        });

        //设置返回键的点击事件
        idProdListIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //titleBarReddot显示出购物车里所有商品总数
    void setTitleBarRedDotText() {
        titleBarReddot.setVisibility(View.VISIBLE);//设置TextView可见
        titleBarReddot.setText(allNumber + "");  //设置显示数据
    }


    @OnClick({R.id.prod_info_cart, R.id.prod_info_nowbuy, R.id.subbt, R.id.addbt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prod_info_cart://点击加入购物车
                //改变购物车图标显示的文本值
                allNumber += Integer.parseInt(edt.getText().toString());
                setTitleBarRedDotText();

                //动画效果
                TranslateAnimation translateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.shake);
                titleBarRlCartView.startAnimation(translateAnimation);//开始平移动画

                //添加到服务器
                RequestParams requestParams = new RequestParams(PathUrl.appUrl + "/InsertCartServlet");
                //传参数：购物车对象
                Cart cart = new Cart(product, user.getUserId(), Integer.parseInt(edt.getText().toString()), new Timestamp(System.currentTimeMillis()));
                //将Cart转化为Json
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                String cartJson = gson.toJson(cart);
                requestParams.addBodyParameter("cartInfo", cartJson);
                x.http().post(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

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


                break;
            case R.id.prod_info_nowbuy: //点击跳转到支付页面
                Intent intent = new Intent(this, GoPayActivity.class);
                startActivity(intent);
                break;
            case R.id.subbt:
                //减少数量，条件：至少有2个
                if (Integer.parseInt(edt.getText().toString()) > 1) {
                    edt.setText(Integer.parseInt(edt.getText().toString()) - 1 + "");
                }
                break;
            case R.id.addbt:
                //增加数量
                edt.setText(Integer.parseInt(edt.getText().toString()) + 1 + "");
                break;
        }
    }


}
