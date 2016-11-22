package com.young.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.young.application.MyApplication;
import com.young.entity.Cart;
import com.young.entity.Product;
import com.young.entity.User;
import com.young.shopping.ProductOrderActivity;
import com.young.shopping.R;
import com.young.util.CommonAdapter;
import com.young.util.MyOrderMap;
import com.young.util.PathUrl;
import com.young.util.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 购物车的Fragment
 * checkbox的check状态在复用的时候很复杂，可以用点击事件setOnClickListener来做
 * （老师的方法全是用setOnCheckedChangeListener来做的，用点击事件来做更简单）
 * Created by yang on 2016/9/30 0030.
 */
public class ShopingCartFragment extends BaseFragment {
    public static final int requestCode = 2;

    MyApplication myApplication;
    User user;
    Handler handler = new Handler();

    @InjectView(R.id.cart_jiesuan)
    Button cartJiesuan;
    @InjectView(R.id.cart_buy_money)
    TextView cartBuyMoney;
    @InjectView(R.id.checkall)
    public CheckBox checkall;
    @InjectView(R.id.cart_listview)
    ListView cartListview;

    CartAdapter cartAdapter;
    List<Cart> carts = new ArrayList<>();
    double totalPrice; //购物车选择的商品的总价

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shoping_cart, null);
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

        //获取购物车信息，展示在ListView上面
        getCartData();

    }

    @Override
    public void initEvent() {
        //全选的checkbox
        checkall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cartAdapter.setFlag(2);
                    Log.i("CartAdapter", "Flag: "+cartAdapter.getFlag());
                    for(int i=0;i<cartAdapter.getCount();i++) {
                        cartAdapter.checkedStatus.put(i, true);
                    }
                    //改变adapter的selectnumbers:为总记录的条数
                    cartAdapter.setSelectedNumber(cartAdapter.getCount());
                    Log.i("CartAdapter", "E1----" + cartAdapter.getSelectedNumber());
                    cartAdapter.notifyDataSetChanged();//更新ListView
                    //totalPrice = cartAdapter.allPrice;
                    //也可以不用第一种方法来获得总价，在这个地方计算出总价来
                    totalPrice = 0;
                    for(int i=0;i<cartAdapter.getCount();i++){
                        totalPrice+=cartAdapter.getGoodsNumberMap().get(i)*carts.get(i).getProduct().getPrice();
                    }
                    cartBuyMoney.setText("￥" + totalPrice);
                }else if(!isChecked&&cartAdapter.getFlag()!=1){//如果用老师的方法(即使用check监听),则这里不能设置该方法，会出错，即没有取消全选的功能，用click监听可以实现↓↓↓
                    cartAdapter.setFlag(0);
                    Log.i("CartAdapter", "Flag: "+cartAdapter.getFlag());
                    for(int i=0;i<cartAdapter.getCount();i++) {
                        cartAdapter.checkedStatus.put(i, false);
                        cartAdapter.setSelectedNumber(0);
                        Log.i("CartAdapter", "E2----" + cartAdapter.getSelectedNumber());
                        cartAdapter.notifyDataSetChanged();
                        totalPrice = 0.00;
                        cartBuyMoney.setText("￥" + totalPrice);
                    }
                }
            }
        });
//        checkall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(((CheckBox)v).isChecked()){
//                    for(int i=0;i<cartAdapter.getCount();i++) {
//                        cartAdapter.checkedStatus.put(i, true);
//                    }
//                    //改变adapter的selectNumber:为总记录的条数
//                    cartAdapter.setSelectedNumber(cartAdapter.getCount());
//                    Log.i("CartAdapter", "E1----" + cartAdapter.getSelectedNumber());
//                    cartAdapter.notifyDataSetChanged();//更新ListView
//                    totalPrice = cartAdapter.allPrice;
//                    cartBuyMoney.setText("￥" + totalPrice);
//                }else{
//                    for(int i=0;i<cartAdapter.getCount();i++) {
//                        cartAdapter.checkedStatus.put(i, false);
//                        cartAdapter.setSelectedNumber(0);
//                        Log.i("CartAdapter", "E2----" + cartAdapter.getSelectedNumber());
//                        cartAdapter.notifyDataSetChanged();
//                        totalPrice = 0.00;
//                        cartBuyMoney.setText("￥" + totalPrice);
//                    }
//                }
//            }
//        });



    }

    //Fragment显示或隐藏会回调该方法
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            checkall.setChecked(false);
            totalPrice = 0;
            cartBuyMoney.setText("￥" + totalPrice);
            cartAdapter = null;
        } else{ //Fragment显示的时候会获取一次数据
            initData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    //获得购物车信息，显示在ListView上面
    void getCartData(){
        //获取网络数据，显示用户加入购物车的所有信息
        RequestParams requestParams = new RequestParams(PathUrl.appUrl+"/QueryCartServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Cart>>(){}.getType();
                List<Cart> newCarts = gson.fromJson(result,type);
                carts.clear();   //清空集合信息
                carts.addAll(newCarts);  //将另一个集合所有数据添加进来

                if(cartAdapter==null){
                    cartAdapter = new CartAdapter(getActivity(),carts,R.layout.cart_item);
                    cartListview.setAdapter(cartAdapter);
                }else{
                    cartAdapter.notifyDataSetChanged();
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

    //自定义Adapter
    class CartAdapter extends CommonAdapter<Cart>{
        //存放每个CheckBox选中状态的Map
        Map<Integer,Boolean> checkedStatus = new HashMap<>();
        //存放商品数量的Map
        Map<Integer,Integer> goodsNumberMap = new HashMap<>();
        double allPrice = 0;//所有商品总价,最开始先计算出来，用户加减数量后会再进行计算,(click监听用的是这种方法)
                            // 也可以不用这种方法，在全选按钮的监听事件最后再计算价格总数(check监听用的是这种方法)
        int flag = 0; //当flag为0时，代表不能执行每个checkbox的check监听里的else，为1表示可以执行
        //初始值为0，点击单个checkbox后，flag=1，取消后也是1,flag为1的时候不能执行initEvent里的else，即不能全部取消选中
        //点击全选后flag变为2，取消某个单选flag变回1，全部选中后flag变为2，全部取消后flag变为0，flag为0的时候不能执行单个checkbox取消选中
        int selectedNumber;//记录当前选中的checkbox的个数
        public void setSelectedNumber(int selectedNumber){
            this.selectedNumber = selectedNumber;
        }
        public int getSelectedNumber(){
            return selectedNumber;
        }
        public Map<Integer,Integer> getGoodsNumberMap(){
            return goodsNumberMap;
        }
        public Map<Integer,Boolean> getCheckedStatus(){
            return checkedStatus;
        }
        public void setFlag(int flag){
            this.flag = flag;
        }

        public int getFlag(){
            return flag;
        }

        public CartAdapter(Context context, List<Cart> lists, int layoutId) {
            super(context, lists, layoutId);
            //初始化checkedStatus：默认所有checkbox均未选中
            for(int i=0;i<lists.size();i++){
                checkedStatus.put(i,false);
            }
            //初始化goodsNumberMap
            for(int i=0;i<lists.size();i++){
                goodsNumberMap.put(i,lists.get(i).getCollectNumber());
            }
            //计算所有商品总价，最初始的总价，即用户没有增加数量或减少数量
            for(int i=0;i<lists.size();i++){
                allPrice+=lists.get(i).getProduct().getPrice()*lists.get(i).getCollectNumber();
            }
            Log.i("CartAdapter", "onCheckedChanged: allPrice:"+allPrice);
        }

        @Override
        public void convert(ViewHolder viewHolder, final Cart cart, final int position) {
            //找控件，赋值
            final TextView tv_price = viewHolder.getViewById(R.id.cart_item_prod_price);//显示价格
            tv_price.setText(cart.getProduct().getPrice()+"");
            final TextView tv_number = viewHolder.getViewById(R.id.cart_item_number);//显示数量
            //tv_number.setText(cart.getCollectNumber()+"");
            tv_number.setText(goodsNumberMap.get(position)+"");//每次都设置最新的数据
            TextView tv_productName = viewHolder.getViewById(R.id.cart_item_prod_des);//设置商品名
            tv_productName.setText(cart.getProduct().getName());

            final CheckBox cb = viewHolder.getViewById(R.id.check_item);//单个checkbox
            cb.setTag(position);//保证每个checkbox的tag不一样

            //显示一个view的时候，重新设置view上一次的checkbox状态
            cb.setChecked(checkedStatus.get(position));

            //设置checkbox点击事件（用点击事件来做更简单）
//            cb.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {//checkedStatus.get(v.getTag())
//                    if(((CheckBox)v).isChecked()) {//checkbox选中状态
//                        selectedNumber++;  //选中的个数+1
//                        Log.i("CartAdapter", "O1----"+selectedNumber);
//                        checkedStatus.put((int) v.getTag(),true);
//                        //选中项的价格
//                        double eachPrice = Double.parseDouble(tv_price.getText().toString()) * Integer.parseInt(tv_number.getText().toString());
//                        //修改总价格
//                        totalPrice += eachPrice;
//                        //改变合计的TextView的值
//                        cartBuyMoney.setText("￥" + totalPrice);
//
//                        //当某个checkbox选中时，判断当前选中项个数是否等于总数，若等于，则让“全选”的checkbox选中
//                        if(selectedNumber==cartAdapter.getCount()){
//                            checkall.setChecked(true);
//                        }
//                    }else{ //checkbox未选中状态
//                        selectedNumber--;  //选中的个数减一
//                        Log.i("CartAdapter", "O2----"+selectedNumber);
//                        checkedStatus.put((int) v.getTag(),false);
//                        //选中项的价格
//                        double eachPrice = Double.parseDouble(tv_price.getText().toString()) * Integer.parseInt(tv_number.getText().toString());
//                        //修改总价格
//                        totalPrice -= eachPrice;
//                        //改变合计的TextView的值
//                        cartBuyMoney.setText("￥" + totalPrice);
//                        //当某一个checkbox取消选中；如果当前“全选”是选中状态，取消全选
//                        if(checkall.isChecked()){
//                            checkall.setChecked(false);
//                        }
//                    }
//                }
//            });

            //设置check状态改变的监听事件
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Log.i("CartAdapter", "onCheckedChanged: "+position+"--"+cb.getTag());
                    if(isChecked&&position==(int)cb.getTag()&&!checkall.isChecked()){//选中checkbox
                        flag=1;
                        Log.i("CartAdapter", "Flag: "+flag);
                        selectedNumber++;  //选中的个数+1
                        Log.i("CartAdapter", "O1----"+selectedNumber);
                        checkedStatus.put((int) buttonView.getTag(),true);
                        //选中项的价格
                        double eachPrice = Double.parseDouble(tv_price.getText().toString())*Integer.parseInt(tv_number.getText().toString());
                        //修改总价格
                        totalPrice+=eachPrice;
                        //改变合计的TextView的值
                        cartBuyMoney.setText("￥"+totalPrice);

                        //当某个checkbox选中时，判断当前选中项个数是否等于总数，若等于，则让“全选”的checkbox选中
                        if(selectedNumber==cartAdapter.getCount()){
                            checkall.setChecked(true);
                        }
                    }else if(!isChecked&&position==(int)cb.getTag()&&flag!=0){
                        flag=1;
                        Log.i("CartAdapter", "Flag: "+flag);
                        selectedNumber--;  //选中的个数减一
                        Log.i("CartAdapter", "O2----"+selectedNumber);
                        checkedStatus.put((int) buttonView.getTag(),false);
                        //选中项的价格
                        double eachPrice = Double.parseDouble(tv_price.getText().toString())*Integer.parseInt(tv_number.getText().toString());
                        //修改总价格
                        totalPrice-=eachPrice;
                        //改变合计的TextView的值
                        cartBuyMoney.setText("￥"+totalPrice);

                        //当某一个checkbox取消选中；如果当前“全选”是选中状态，取消全选
                        if(checkall.isChecked()){
                            checkall.setChecked(false);
                        }

                    }
                }
            });

            Button btn_minus = viewHolder.getViewById(R.id.cart_item_jian);// “减”的Button
            btn_minus.setTag(position); //保证每个button的tag不一样
            Button btn_plus = viewHolder.getViewById(R.id.cart_item_jia); //   “加”的Button
            btn_plus.setTag(position);
            // “减”的Button点击事件
            btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("CartAdapter", "onClick:minus "+cb.getTag()+"--"+position+checkedStatus.get(position));
                        //减少数量，条件：至少有2个
                        if (Integer.parseInt(tv_number.getText().toString()) > 1) {
                            int newNumber = Integer.parseInt(tv_number.getText().toString()) - 1;//用户点击减号后新的数量
                            tv_number.setText(newNumber+"");  //改变用户界面显示的商品数量
                            goodsNumberMap.put((int)v.getTag(),newNumber);//改变存放数量的Map里的数据
                            allPrice -= Double.parseDouble(tv_price.getText().toString());
                            if(checkedStatus.get(v.getTag())) {//“减”的Button所在的checkbox为选中状态
                                //修改总价格
                                totalPrice -= Double.parseDouble(tv_price.getText().toString());
                                //改变合计的TextView的值
                                cartBuyMoney.setText("￥" + totalPrice);
                        }
                    }
                }
            });
            // “加”的Button点击事件
            btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newNumber = Integer.parseInt(tv_number.getText().toString()) + 1;
                    tv_number.setText(newNumber+"");  //改变用户界面显示的商品数量
                    goodsNumberMap.put((int)v.getTag(),newNumber);//改变存放数量的Map里的数据
                    allPrice += Double.parseDouble(tv_price.getText().toString());
                    if(checkedStatus.get(position)) {//“加”的Button所在的checkbox为选中状态
                        //修改总价格
                        totalPrice += Double.parseDouble(tv_price.getText().toString());
                        //改变合计的TextView的值
                        cartBuyMoney.setText("￥" + totalPrice);
                    }
                }
            });

//            //全选的checkbox,放在initEvent里面执行
//            checkall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked){
//                        for(int i=0;i<checkedStatus.size();i++) {
//                            checkedStatus.put(i, true);
//                        }
//                        notifyDataSetChanged();
//                        totalPrice = allPrice;
//                        cartBuyMoney.setText("￥" + totalPrice);
//                    }else{
//                        for(int i=0;i<checkedStatus.size();i++) {
//                            checkedStatus.put(i, false);
//                            notifyDataSetChanged();
//                            cartBuyMoney.setText("￥" + 0.00);
//                        }
//                    }
//                }
//            });

        }
    }

    @OnClick(R.id.cart_jiesuan)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cart_jiesuan:
                //跳转到确认订单界面：传值：用户选中的商品对象，商品数量
                //传给订单结算页面的map：商品对象-购买数量
                //Map<Product,Integer> map = new HashMap<>();
                MyOrderMap myOrderMap = new MyOrderMap();
                myOrderMap.orderMap = new HashMap<>();
                //遍历cartAdapter里面的Map:checkedStatus,将商品对象和数量添加到map
                for(Map.Entry<Integer,Boolean> entry:cartAdapter.getCheckedStatus().entrySet()){
                    //如果该项被选中
                    if(entry.getValue()){
                        int position = entry.getKey();
                        //获得选中项的商品对象
                        Product product = carts.get(position).getProduct();
                        //获得选中项的商品数量
                        int number = cartAdapter.getGoodsNumberMap().get(position);

                        myOrderMap.orderMap.put(product,number);
                    }
                }
//                for(Map.Entry<Product,Integer> m:map.entrySet()){
//                    Log.i("ShopingCartFragment", "onClick: "+m.getKey()+"--"+m.getValue());
//                }

                Intent intent = new Intent(getActivity(), ProductOrderActivity.class);
                //将map传过去
                intent.putExtra("myOrderMap",myOrderMap);
                //startActivity(intent);
                getActivity().startActivityForResult(intent,requestCode);
                //延迟0.3秒后执行，（用户就看不到了），内部并没有开子线程，具体看Handler源码
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearCheckStatus(); //清空checkbox状态
                    }
                },300);
                break;
        }
    }

    //如果该Fragment所在的activity重写了该方法，则不会执行这里的，只执行activity里的
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("MainActivity", "onActivityResult2: "+requestCode+"-----"+this.requestCode);
        if(requestCode==this.requestCode){

//            cartAdapter=null;
//            onResume(); //刷新页面，无效？
            checkall.setChecked(false);
        }
    }

    public void clearCheckStatus(){ //清空选中状态
        checkall.setChecked(false);
        cartAdapter.setFlag(0);
        for(int i=0;i<cartAdapter.getCount();i++) {
            cartAdapter.checkedStatus.put(i, false);
        }
        cartAdapter.setSelectedNumber(0);
        cartAdapter.notifyDataSetChanged();
        totalPrice = 0.00;
        cartBuyMoney.setText("￥" + totalPrice);
    }

}
