package com.young.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.young.application.MyApplication;
import com.young.entity.Product;
import com.young.entity.User;
import com.young.entity.bean.InsertOrderBean;
import com.young.util.CommonAdapter;
import com.young.util.MyOrderMap;
import com.young.util.PathUrl;
import com.young.util.ViewHolder;
import com.young.widget.NoScrollListview;
import com.young.widget.TitleBar;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 结算订单的页面
 */
public class ProductOrderActivity extends AppCompatActivity {

    @InjectView(R.id.order_goumai)
    Button orderGoumai;
    @InjectView(R.id.order_info_ceil_order)
    Button orderInfoCeilOrder;
    @InjectView(R.id.order_total_money)
    TextView orderTotalMoney;
    @InjectView(R.id.order_info_state_img)
    ImageView orderInfoStateImg;
    @InjectView(R.id.order_dizhi_left_tupian)
    ImageView orderDizhiLeftTupian;
    @InjectView(R.id.order_dizhi_username)
    TextView orderDizhiUsername;
    @InjectView(R.id.order_dizhi_phonenum)
    TextView orderDizhiPhonenum;
    @InjectView(R.id.order_dizhi_detaildizhi)
    TextView orderDizhiDetaildizhi;
    @InjectView(R.id.order_dizhi_detailinfo)
    RelativeLayout orderDizhiDetailinfo;
    @InjectView(R.id.order_dizhi_right_tupian)
    ImageView orderDizhiRightTupian;
    @InjectView(R.id.order_dizhi)
    RelativeLayout orderDizhi;
    @InjectView(R.id.order_prod_liepiao)
    TextView orderProdLiepiao;
    @InjectView(R.id.order_prod_yunfei)
    TextView orderProdYunfei;
    @InjectView(R.id.order_prod_yunfei_money)
    TextView orderProdYunfeiMoney;
    @InjectView(R.id.order_prod_yunfeitotal)
    RelativeLayout orderProdYunfeitotal;
    @InjectView(R.id.order_count_total_money)
    TextView orderCountTotalMoney;
    @InjectView(R.id.order_count_heji)
    TextView orderCountHeji;
    @InjectView(R.id.order_count_money)
    RelativeLayout orderCountMoney;
    @InjectView(R.id.order_prod_fapiao_left)
    TextView orderProdFapiaoLeft;
    @InjectView(R.id.order_prod_fapiao_right)
    TextView orderProdFapiaoRight;
    @InjectView(R.id.order_prod_youhuiquan_left)
    TextView orderProdYouhuiquanLeft;
    @InjectView(R.id.order_prod_youhuiquan_right)
    TextView orderProdYouhuiquanRight;
    @InjectView(R.id.order_scroll_listview)
    NoScrollListview orderScrollListview;
    ImageView return_image;//返回按钮

    MyApplication myApplication;
    User user;
    Handler handler = new Handler();

    Map<Product, Integer> orderMap; //存放商品对象和数量的Map
    double totalPrice = 0.0;      //商品的总价格
    int totalNumber = 0;          //商品总数量
    Map<Product, Integer> details; //传给数据库的map，Product对象只有2个属性
    CommonAdapter<Map<String,String>> prodOrderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order);
        ButterKnife.inject(this);

        initView();
        initData();
        initEvent();
    }

    void initView(){
        TitleBar titleBar = (TitleBar) findViewById(R.id.titlebar);
        return_image = titleBar.getLeftImage();

    }

    //初始化数据
    void initData() {
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();

        Intent intent = getIntent();
        MyOrderMap myOrderMap = intent.getParcelableExtra("myOrderMap");
        orderMap = myOrderMap.getOrderMap();
        List<Map<String,String>> prodMsgList = new ArrayList<>(); //存放商品名、单价、数量的集合，每个元素是Map
        for (Map.Entry<Product, Integer> map : orderMap.entrySet()) {
            totalPrice += map.getKey().getPrice() * map.getValue();   //计算出总价
            totalNumber += map.getValue();                           //计算出总数量

            Map<String,String> eachMap = new HashMap<>();
            eachMap.put("prodName",map.getKey().getName());       //商品名
            eachMap.put("prodPrice",map.getKey().getPrice()+""); //商品单价
            eachMap.put("prodNumber",map.getValue()+"");         //商品数量

            prodMsgList.add(eachMap);

        }
        if (orderMap != null) {
            orderCountTotalMoney.setText("￥" + totalPrice); //页面的合计的价格
            orderTotalMoney.setText("￥" + totalPrice);      //页面的实付的价格
            orderCountHeji.setText("共"+totalNumber+"件商品    合计:"); //设置商品数量

            if(prodOrderAdapter==null){
                prodOrderAdapter = new CommonAdapter<Map<String, String>>(this,prodMsgList,R.layout.product_order_item) {
                    @Override
                    public void convert(ViewHolder viewHolder, Map<String, String> stringStringMap, int position) {
                        TextView tv_prodName = viewHolder.getViewById(R.id.tv_product_order_name);
                        tv_prodName.setText(stringStringMap.get("prodName")); // 商品名
                        TextView tv_prodPrice = viewHolder.getViewById(R.id.tv_product_order_price);
                        tv_prodPrice.setText(stringStringMap.get("prodPrice")); //商品价格
                        TextView tv_prodNumber = viewHolder.getViewById(R.id.tv_product_order_number);
                        tv_prodNumber.setText(stringStringMap.get("prodNumber"));  //商品数量
                    }
                };
                orderScrollListview.setAdapter(prodOrderAdapter);
            }else{
                prodOrderAdapter.notifyDataSetChanged();
            }

        }


    }

    void initEvent(){
        return_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //延迟0.5秒后执行finish,这里不需要了，因为在跳转的时候已经清空checkbox选中状态了，不需要再用这种方法了
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                },500);
                finish();
            }
        });
    }

    @OnClick({R.id.order_goumai, R.id.order_info_ceil_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_goumai://点击购买，传递数据给服务器端，服务器接收数据并生成订单
                if(totalNumber!=0) {
                    RequestParams requestParams = new RequestParams(PathUrl.appUrl + "/OrderInsertServlet");
                    InsertOrderBean insertOrderBean = new InsertOrderBean(user.getUserId(), 1, totalPrice, orderMap);
                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                    String orderInfo = gson.toJson(insertOrderBean);
                    //Gson gson = new Gson();//这样解析Map会出错，要使用下面的gson ↓↓↓
                    //Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                    //String detailsJson = gson.toJson(orderMap);
                    requestParams.addQueryStringParameter("orderInfo", orderInfo);
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(ProductOrderActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
                            finish();
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
                }else {
                    Toast.makeText(ProductOrderActivity.this, "未选择任何商品！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.order_info_ceil_order:
                break;

        }
    }
}
