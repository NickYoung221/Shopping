package com.young.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import org.xutils.ex.HttpException;
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
 * 所有订单的Fragment
 * Created by yang on 2016/10/10 0010.
 */
public class OrderAllFragment extends BaseFragment {

    @InjectView(R.id.frag_allorders_listview)
    ListView fragAllordersListview;
    @InjectView(R.id.frag_allorders_rl)
    RelativeLayout fragAllordersRl;

    View view; //Fragment页面的View
    CommonAdapter<Order> orderAdapter;
    List<Order> orders = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_allorders, null);//设置显示的是一个Listview
            ButterKnife.inject(this, view);

        }
        //找到listview，设置listview的item显示内容；
        //Log.i("OrderAllFragment", "onCreateView: ");

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

    //获取订单数据
    public void getOrderData() {
        //Log.i("OrderAllFragment", "getOrderData: "+orderAdapter);
        RequestParams requestParams = new RequestParams(PathUrl.appUrl + "/QueryOrdersServlet");

        requestParams.addQueryStringParameter("userId", ((MyApplication) getActivity().getApplication()).getUser().getUserId() + "");
        requestParams.addQueryStringParameter("orderStatusId", 0 + "");//状态为0表示查询全部的订单信息
        //获取网络数据，获取到之后，设置数据源
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();//设置日期格式（24小时）
                List<Order> newOrders = gson.fromJson(result, new TypeToken<List<Order>>() {}.getType());
                orders.clear();
                orders.addAll(newOrders);//添加新的集合

                //fragAllordersListview.setEmptyView(fragAllordersRl);//设置没有数据时，显示
                if (orderAdapter == null) {
                    orderAdapter = new CommonAdapter<Order>(getActivity(), orders, R.layout.frag_allorders_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, final Order item, final int position) {
                            //订单时间
                            TextView tvOrderTime = viewHolder.getViewById(R.id.frag_allorders_item_time);
                            Timestamp timestamp = item.getGoodsCreateTime();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String dateString = dateFormat.format(timestamp);//把timestamp格式后面的毫秒数去掉
                            tvOrderTime.setText("订单时间：" + dateString);
                            //订单状态
                            TextView tvOrderState = viewHolder.getViewById(R.id.frag_allorders_item_trade);
                            tvOrderState.setText(item.getGoodsOrderState().getGoodsOrderStates());//获取状态名称
                            //总数量
                            TextView tvTotalNumber = viewHolder.getViewById(R.id.frag_allorders_item_buynum);
                            int totalNumber = 0;
                            for (OrderDetail orderDetail : item.getOrderDetails()) {
                                totalNumber += orderDetail.getGoodsNum();//获取每个商品的数量
                            }
                            tvTotalNumber.setText("共" + totalNumber + "件");

                            //总价格
                            TextView tvTotalPrice = viewHolder.getViewById(R.id.frag_allorders_item_money);
                            tvTotalPrice.setText("实付:￥" + item.getGoodsTotalPrice());

                            //商品详情
                            NoScrollListview nsl = viewHolder.getViewById(R.id.frag_allorders_item_listView);
                            //设置数据源:显示的是商品详情信息

                            nsl.setAdapter(new CommonAdapter<OrderDetail>(getActivity(), item.getOrderDetails(), R.layout.order_product_item) {
                                @Override
                                public void convert(ViewHolder viewHolder, OrderDetail item, int position) {

                                    TextView tvProdName = viewHolder.getViewById(R.id.order_item_info_des);//商品名称
                                    tvProdName.setText(item.getProduct().getName());
                                    //商品价格
                                    TextView tvPrice = viewHolder.getViewById(R.id.order_item_info_price);
                                    tvPrice.setText("￥" + item.getGoodsPrice());

                                    //购买数量
                                    TextView tvBuyNumber = viewHolder.getViewById(R.id.order_item_info_buynum);
                                    tvBuyNumber.setText("x" + item.getGoodsNum());


                                }
                            });

                            //具体按钮显示（文本），及点击事件
                            Button btnRight = viewHolder.getViewById(R.id.btn_item_right);

                            Button btnLeft = viewHolder.getViewById(R.id.btn_item_left);

                            //根据订单状态，判断当前显示的文本
                            switch (item.getGoodsOrderState().getGoodsOrderStates()) {
                                case "待付款":
                                    btnLeft.setVisibility(View.VISIBLE);
                                    btnRight.setVisibility(View.VISIBLE);
                                    btnLeft.setText("取消订单");
                                    btnRight.setText("付款");
                                    break;
                                case "待发货":
                                    btnLeft.setVisibility(View.GONE);
                                    btnRight.setVisibility(View.VISIBLE);
                                    btnRight.setText("确认收货");
                                case "待收货":
                                    btnLeft.setVisibility(View.GONE);
                                    btnRight.setVisibility(View.VISIBLE);
                                    btnRight.setText("确认收货");
                                    break;
                                case "待评价":
                                    btnLeft.setVisibility(View.GONE);
                                    btnRight.setVisibility(View.VISIBLE);
                                    btnRight.setText("评价");
                                    break;
                                case "交易成功":
                                    btnLeft.setVisibility(View.GONE);
                                    btnRight.setVisibility(View.GONE);
                                    break;
                                case "交易关闭":
                                    btnLeft.setVisibility(View.GONE);
                                    btnRight.setVisibility(View.GONE);
                                    break;
                                default:
                                    btnLeft.setVisibility(View.GONE);
                                    btnRight.setVisibility(View.GONE);
                                    break;


                            }

                            //设置按钮点击事件（左边按钮）
                            btnLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switch (item.getGoodsOrderState().getGoodsOrderStates()) {
                                        case "待付款":
                                            //操作：取消订单
                                            updateOrder(item.getGoodsOrderId(), 6);

                                            //取消订单：更新当前订单的状态（传参：订单id，当前状态，需要改的状态，返回当前状态的订单记录）

                                            //当前的订单集合中的数据源发生改变；直接更新界面
                                            //调用adapter方法更新集合
                                            orders.get(position).setGoodsOrderState(new GoodsOrderState(6, "交易关闭"));
                                            orderAdapter.notifyDataSetChanged();
                                            break;
                                    }
                                }
                            });

                            btnRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switch (item.getGoodsOrderState().getGoodsOrderStates()) {
                                        case "待付款":
                                            //付款：
                                            //跳转到付款页面........
                                            updateOrder(item.getGoodsOrderId(),2);

                                            //当前的订单集合中的数据源发生改变；直接更新界面
                                            //调用adapter方法更新集合
                                            orders.get(position).setGoodsOrderState(new GoodsOrderState(2,"待发货"));
                                            orderAdapter.notifyDataSetChanged();
                                            break;
                                        case "待发货":
                                            //操作：卖家-发货 ， 买家-催卖家发货
                                            updateOrder(item.getGoodsOrderId(), 4);
                                            orders.get(position).setGoodsOrderState(new GoodsOrderState(4, "待评价"));
                                            orderAdapter.notifyDataSetChanged();
                                            break;
                                        case "待收货":
                                            //操作：确认收货
                                            updateOrder(item.getGoodsOrderId(), 4);

                                            //当前的订单集合中的数据源发生改变；直接更新界面
                                            //调用adapter方法更新集合
                                            orders.get(position).setGoodsOrderState(new GoodsOrderState(4, "待评价"));
                                            orderAdapter.notifyDataSetChanged();
                                            break;
                                        case "待评价":
                                            //操作：跳转到评价页面
                                            updateOrder(item.getGoodsOrderId(),5);
                                            orders.get(position).setGoodsOrderState(new GoodsOrderState(5, "交易成功"));
                                            orderAdapter.notifyDataSetChanged();
                                            break;
                                    }
                                }
                            });
                        }
                    };
                    //listview中显示的是所有的数据信息
                    fragAllordersListview.setAdapter(orderAdapter);

                } else {
                    orderAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if(ex instanceof HttpException){  //网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    //...
                }else{
                    //...其他错误
                }
                Log.i("OrderAllFragment", "onError: "+ex.getMessage());
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 更改OrderId的订单状态，返回新的所有的订单集合，赋值给list，重新更新适配器
     * @param orderId
     * @param newState
     */
    public void updateOrder(int orderId, int newState) {
        String url = PathUrl.appUrl + "/OrderUpdateServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("orderId", orderId + "");
        requestParams.addQueryStringParameter("newState", newState + "");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
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
        //Log.i("OrderAllFragment", "onDestroyView: ");
        //当滑动ViewPager使当前Fragment隐藏在后台，则会调用onStop、onDestroyView（Fragment生命周期）ViewPager会预加载相邻的页面
        //orderAdapter = null;//把orderAdapter回收,也可以不回收，用下面的方法（回收View）
        if(view != null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.i("OrderAllFragment", "onStop: ");
    }

    //Fragment对于用户是否可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){  //当该页面对用户可见时，再获取服务器数据
            getOrderData();
        }
    }

    @Override
    public void onDestroy() { //退出ViewPager会执行该方法，即该Fragment被销毁
        super.onDestroy();
        //Log.i("OrderAllFragment", "onDestroy: ");
    }



}
