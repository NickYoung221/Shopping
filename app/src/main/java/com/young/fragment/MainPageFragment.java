package com.young.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.young.entity.Product;
import com.young.shopping.ProductInfoActivity;
import com.young.shopping.R;
import com.young.util.CommonAdapter;
import com.young.util.PathUrl;
import com.young.util.ViewHolder;
import com.young.widget.RefreshListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by yang on 2016/9/30 0030.
 */
public class MainPageFragment extends BaseFragment implements RefreshListView.OnRefreshLoadChangeListener{

    public static final int requestCode = 1;
    Handler handler = new Handler();

    @InjectView(R.id.query)
    EditText query;
    @InjectView(R.id.search_clear)
    ImageButton searchClear;
    @InjectView(R.id.tv_search)
    TextView tvSearch;
    @InjectView(R.id.id_prod_list_sort_left)
    TextView idProdListSortLeft;
    @InjectView(R.id.prod_list_rl_pop)
    RelativeLayout prodListRlPop;
    @InjectView(R.id.id_prod_list_sort_right)
    TextView idProdListSortRight;
    @InjectView(R.id.id_prod_list_sort_right_trangle)
    ImageView idProdListSortRightTrangle;
    @InjectView(R.id.prod_list_pop_two)
    RelativeLayout prodListPopTwo;
    @InjectView(R.id.id_prod_list_sort)
    LinearLayout idProdListSort;
    @InjectView(R.id.id_prod_list_sort_line1)
    View idProdListSortLine1;
    @InjectView(R.id.lv_goods)
    RefreshListView lvGoods;

    String productName;//商品名称（默认没有）
    int orderFlag = 0; //排序标记（默认为0，即按照时间降序排列）
    int pageNo = 1;    //第几页？（默认为第一页）
    int pageSize = 8;  //每页大小？（默认每页有4条数据）

    CommonAdapter<Product> goodsAdapter;
    List<Product> products = new ArrayList<>();//存放商品信息
    List<String> popupWindowContent = new ArrayList<>();//存放popupWindow显示的信息


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_page, null);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void initView() {
        searchClear.setVisibility(View.GONE);
    }

    @Override
    public void initData() {

        getData();//获取网络数据，显示在listView上

        popupWindowContent.add("价格从高到低");
        popupWindowContent.add("价格从低到高");
    }

    @Override
    public void initEvent() {  //事件
        //设置ListView的Item点击事件
        lvGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //bug1解决：首先要判断不是下拉，而是点击!lvGoods.isFlag()
                //bug2解决：因为头部和底部也算位置的，这里要判断用户点击的不是头部也不是尾部
                if(!lvGoods.isFlag()&&position!=0&&position<=products.size()){
                    //跳转到商品详情界面
                    Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                    //传值：点解的Item的商品信息
                    intent.putExtra("productInfo",products.get(position-1));//上面还有一个头部View
                    //startActivity(intent);
                    getActivity().startActivityForResult(intent,requestCode);
                }
            }
        });

        //添加EditText的文本监听事件
        query.addTextChangedListener(new MyTextChange());

        //实现自定义ListView里的OnRefreshUploadChangeListener接口
        lvGoods.setOnRefreshUploadChangeListener(this);
    }

    //获取网络数据
    public void getData(){
        //界面初始化数据，listView显示数据
        //xUtils框架获取网络数据
        String url = PathUrl.appUrl+"/QueryProductServlet";
        RequestParams requestParams = new RequestParams(url);
        //使用get提交方式，addQueryStringParameter
        requestParams.addQueryStringParameter("productName",productName);
        requestParams.addQueryStringParameter("orderFlag",orderFlag+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //服务器返回的是Json，将Json转化为List<Product>
                Gson gson = new Gson();
                Type type = new TypeToken<List<Product>>(){}.getType();
                List<Product> newProducts = gson.fromJson(result,type);
                products.clear();  //将List数据清空
                products.addAll(newProducts);  //将另一个List的全部数据添加到该List
                //设置listView的adapter
                if(goodsAdapter==null){
                    goodsAdapter = new CommonAdapter<Product>(getActivity(),products,R.layout.prod_list_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, Product product, int position) {
                            //取出控件，赋值
                            TextView tvName = viewHolder.getViewById(R.id.prod_list_item_tv);
                            tvName.setText(product.getName());  //设置商品名称

                            TextView tvPrice = viewHolder.getViewById(R.id.prod_list_item_tv2);
                            tvPrice.setText("￥ "+product.getPrice());//设置商品价格

                            TextView tvSaleNumber = viewHolder.getViewById(R.id.prod_list_item_tv4);
                            tvSaleNumber.setText("已售 "+product.getProductSaleNumber()+" 件");

                        }
                    };
                    lvGoods.setAdapter(goodsAdapter);
                }else{
                    //只有当数据改变而不是引用改变才会执行该方法，因此不能改变引用，只改变数据内容，将list提为全局变量
                    goodsAdapter.notifyDataSetChanged();
                    //goodsAdapter.notifyDataSetInvalidated();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "无法获取网络数据，请检查网络连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //加载更多数据
    public void loadMoreData(){
        //界面初始化数据，listView显示数据
        //xUtils框架获取网络数据
        String url = PathUrl.appUrl+"/QueryProductServlet";
        RequestParams requestParams = new RequestParams(url);
        //使用get提交方式，addQueryStringParameter
        requestParams.addQueryStringParameter("productName",productName);
        requestParams.addQueryStringParameter("orderFlag",orderFlag+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //服务器返回的是Json，将Json转化为List<Product>
                Gson gson = new Gson();
                Type type = new TypeToken<List<Product>>(){}.getType();
                List<Product> newProducts = gson.fromJson(result,type);
                if(newProducts.size()==0){//服务器没有返回新的数据
                    pageNo--; //下一次继续加载这一页
                    Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
                    lvGoods.completeLoad();//没获取到数据也要改变界面
                    return;
                }
                //products.clear();  //将List数据清空,加载数据，不能清空
                products.addAll(newProducts);  //将另一个List的全部数据添加到该List
                //设置listView的adapter
                if(goodsAdapter==null){
                    goodsAdapter = new CommonAdapter<Product>(getActivity(),products,R.layout.prod_list_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, Product product, int position) {
                            //取出控件，赋值
                            TextView tvName = viewHolder.getViewById(R.id.prod_list_item_tv);
                            tvName.setText(product.getName());  //设置商品名称

                            TextView tvPrice = viewHolder.getViewById(R.id.prod_list_item_tv2);
                            tvPrice.setText("￥ "+product.getPrice());//设置商品价格

                            TextView tvSaleNumber = viewHolder.getViewById(R.id.prod_list_item_tv4);
                            tvSaleNumber.setText("已售 "+product.getProductSaleNumber()+" 件");

                        }
                    };
                    lvGoods.setAdapter(goodsAdapter);
                }else{
                    //只有当数据改变而不是引用改变才会执行该方法，因此不能改变引用，只改变数据内容，将list提为全局变量
                    goodsAdapter.notifyDataSetChanged();
                    //goodsAdapter.notifyDataSetInvalidated();
                }
                lvGoods.completeLoad();//获取完数据后在改变界面
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "无法获取网络数据，请检查网络连接", Toast.LENGTH_SHORT).show();
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
    }

    //创建popupWindow：v（点击的按钮）
    public void initPopupWindow(View view){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.lv_zonghe_paixu,null);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//获取当前屏幕宽度
        //final PopupWindow popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT,300);
        final PopupWindow popupWindow = new PopupWindow(v, width/2,300);
        //ListView设置数据源
        ListView listView = (ListView) v.findViewById(R.id.lv_zonghe_paixu);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),R.layout.lv_item_zonghe_paixu,popupWindowContent);
        listView.setAdapter(arrayAdapter);

        //设置popupWindow外部点击后消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        //显示在view下面
        popupWindow.showAsDropDown(view);

        //设置popupWindow里面ListView的Item点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //关闭popupWindow
                popupWindow.dismiss();
                //排序
                if(position==0){
                    orderFlag = 2;
                }else if(position==1){
                    orderFlag = 3;
                }
                //重新获取数据源
                getData();
            }
        });
    }

    //按钮点击事件
    @OnClick({R.id.search_clear, R.id.tv_search, R.id.id_prod_list_sort_left, R.id.id_prod_list_sort_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search://搜索：按名称查询
                productName = query.getText().toString().trim(); //去掉前后空格
                getData();
                break;
            case R.id.id_prod_list_sort_left:
                //按照销量优先排序
                orderFlag = 1;
                getData();
                break;
            case R.id.id_prod_list_sort_right://综合排序
                //显示popupWindow
                initPopupWindow(view);
                break;
            case R.id.search_clear://清空EditText内容
                query.setText("");
                break;
        }
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        pageNo = 1; //每次刷新，让pageNo变成初始值1
        //1秒后发一个消息
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();  //再次获取数据
                lvGoods.completeRefresh();  //刷新数据后，改变页面为初始页面：隐藏头部
            }
        },1000);

    }

    //上拉加载
    @Override
    public void onLoad() {
        pageNo++;
        //原来数据基础上增加
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreData();
                //lvGoods.completeLoad();
            }
        },1000);
    }

    //TextWatcher:监听EditText文本改变
    class MyTextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (searchClear != null) {//搜索框文本不为空，就将清空按钮显示
                    searchClear.setVisibility(View.VISIBLE);
                }
            } else {
                if (searchClear != null) {//搜索框文本为空，隐藏清空按钮
                    searchClear.setVisibility(View.GONE);
                }
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
