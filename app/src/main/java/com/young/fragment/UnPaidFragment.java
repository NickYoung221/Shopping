package com.young.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.young.application.MyApplication;
import com.young.entity.GoodsOrderState;
import com.young.entity.Order;
import com.young.entity.OrderDetail;
import com.young.shopping.R;
import com.young.util.CommonAdapter;
import com.young.util.PathUrl;
import com.young.util.ViewHolder;
import com.young.widget.NoScrollListview;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 代付款页面的Fragment（这个页面写的是通用的，把一些方法抽出来单独写）
 * Created by yang on 2016/10/10 0010.
 */
public class UnPaidFragment extends BaseFragment {

    @InjectView(R.id.frag_allorders_listview)
    ListView fragAllordersListview;

    View view; //Fragment页面的View
    List<Order> orders = new ArrayList<>(); //从服务器获取的订单信息
    CommonAdapter<Order> orderAdapter;
    private static final int unPay = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_allorders, null);

            ButterKnife.inject(this, view);

        }

        return view;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        getOrderData();
    }

    @Override
    public void initEvent() {

    }

    //获得页面显示数据
    void getOrderData() {
        RequestParams requestParams = new RequestParams(PathUrl.appUrl + "/QueryOrdersServlet");
        requestParams.addQueryStringParameter("userId", ((MyApplication) getActivity().getApplication()).getUser().getUserId() + "");
        requestParams.addQueryStringParameter("orderStatusId", 1 + "");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                List<Order> newOrders = gson.fromJson(result, new TypeToken<List<Order>>() {}.getType());
                orders.clear();
                orders.addAll(newOrders);

                //设置ListView的数据源
                if (orderAdapter == null) {
                    orderAdapter = new CommonAdapter<Order>(getActivity(),orders,R.layout.frag_allorders_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, Order order, int position) {
                            //设置控件取值
                            initItemView(viewHolder,order,position);
                        }
                    };
                    fragAllordersListview.setAdapter(orderAdapter);
                } else {
                    orderAdapter.notifyDataSetChanged();
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

    //初始化页面ListView的Item里控件的取值
    void initItemView(ViewHolder viewHolder, Order order, int position){
        TextView tvTime=viewHolder.getViewById(R.id.frag_allorders_item_time);
        TextView tvOrderState=viewHolder.getViewById(R.id. frag_allorders_item_trade);
        TextView tvBuyNumber=viewHolder.getViewById(R.id.frag_allorders_item_buynum);
        TextView tvTotalMoney=viewHolder.getViewById(R.id.frag_allorders_item_money);
        Button btnLeft=viewHolder.getViewById(R.id.btn_item_left);
        Button btnRight=viewHolder.getViewById(R.id.btn_item_right);
        NoScrollListview noScrollListview=viewHolder.getViewById(R.id.frag_allorders_item_listView);

        //显示订单时间
        Timestamp timestamp = order.getGoodsCreateTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(timestamp);//把timestamp格式后面的毫秒数去掉
        tvTime.setText("订单时间：" + dateString);
        //显示订单状态
        tvOrderState.setText(order.getGoodsOrderState().getGoodsOrderStates());
        //显示总数量
        int totalNumber = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            totalNumber += orderDetail.getGoodsNum();//获取每个商品的数量
        }
        tvBuyNumber.setText("共" + totalNumber + "件");
        //显示总价格
        tvTotalMoney.setText("实付:￥" + order.getGoodsTotalPrice());

        //设置里面嵌套的ListView的适配器
        CommonAdapter<OrderDetail> orderDetailAdapter = null;
        if(orderDetailAdapter==null){
            orderDetailAdapter = new CommonAdapter<OrderDetail>(getActivity(),order.getOrderDetails(),R.layout.order_product_item) {
                @Override
                public void convert(ViewHolder viewHolder, OrderDetail orderDetail, int position) {
                    //商品名称
                    TextView tvProdName = viewHolder.getViewById(R.id.order_item_info_des);
                    tvProdName.setText(orderDetail.getProduct().getName());
                    //商品价格
                    TextView tvPrice = viewHolder.getViewById(R.id.order_item_info_price);
                    tvPrice.setText("￥" + orderDetail.getGoodsPrice());
                    //购买数量
                    TextView tvBuyNumber = viewHolder.getViewById(R.id.order_item_info_buynum);
                    tvBuyNumber.setText("x" + orderDetail.getGoodsNum());
                }
            };
            noScrollListview.setAdapter(orderDetailAdapter);
        }else{
            orderDetailAdapter.notifyDataSetChanged();
        }

        //设置按钮的初始显示内容
        btnShow(order.getGoodsOrderState().getGoodsOrderStateId(),btnLeft,btnRight);

        //按钮点击事件
        btnClick(order,position,btnLeft,btnRight);

    }

    //根据订单状态，判断按钮是否显示，按钮的文本，按钮的点击事件
    void btnShow(int orderStateId, Button btnLeft, Button btnRight){
        switch (orderStateId){
            case unPay:
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.VISIBLE);
                btnLeft.setText("取消订单");
                btnRight.setText("付款");
                break;
            default:
                btnLeft.setVisibility(View.GONE);
                btnLeft.setVisibility(View.GONE);
                break;
        }
    }

    //左边按钮和右边按钮的点击事件
    void btnClick(final Order order, final int position, Button btnLeft, Button btnRight){
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断订单状态
                switch (order.getGoodsOrderState().getGoodsOrderStateId()){
                    case unPay:
                        //点击后取消订单(更新订单状态，更新界面)
                        changeState(order.getGoodsOrderId(),6,"交易关闭",position);
                        break;
                }
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (order.getGoodsOrderState().getGoodsOrderStateId()){
                    case unPay:
                        //点击付款
                        //跳转到付款页面.......

                        //付款成功后
                        changeState(order.getGoodsOrderId(),2,"待发货",position);
                        break;
                }
            }
        });
    }

    //更新订单状态，更新界面
    public void changeState(int orderId, final int newStateId, final String newStateName,final int position){
        RequestParams requestParams=new RequestParams(PathUrl.appUrl+"/OrderUpdateServlet");
        requestParams.addBodyParameter("orderId",orderId+"");
        requestParams.addBodyParameter("newState",newStateId+"");
        //更新订单，更新界面
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //更新数据源里的信息
                //orders.get(position).setGoodsOrderState(new GoodsOrderState(newStateId,newStateName));
                orders.remove(position);
                orderAdapter.notifyDataSetChanged();//更新界面
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        //orderAdapter = null;//把orderAdapter回收
        if(view != null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
    }

}
