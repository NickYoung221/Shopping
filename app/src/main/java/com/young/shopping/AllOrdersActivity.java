package com.young.shopping;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.young.fragment.BaseFragment;
import com.young.fragment.OrderAllFragment;
import com.young.fragment.UnAssessedFragment;
import com.young.fragment.UnPaidFragment;
import com.young.fragment.UnReceivedFragment;
import com.young.fragment.UnSendFragment;
import com.young.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 所有订单的页面
 */
public class AllOrdersActivity extends AppCompatActivity {

    @InjectView(R.id.all_order_goback)
    ImageView allOrderGoback;
    @InjectView(R.id.order_fragment_tab1)
    TextView orderFragmentTab1;
    @InjectView(R.id.order_fragment_tab2)
    TextView orderFragmentTab2;
    @InjectView(R.id.order_fragment_tab3)
    TextView orderFragmentTab3;
    @InjectView(R.id.order_fragment_tab4)
    TextView orderFragmentTab4;
//    @InjectView(R.id.order_fragment_tab5)
//    TextView orderFragmentTab5;
    @InjectView(R.id.order_fragment_tab6)
    TextView orderFragmentTab6;
    @InjectView(R.id.id_linearly)
    LinearLayout idLinearly;
    @InjectView(R.id.order_line_tab)
    ImageView orderLineTab;
    @InjectView(R.id.order_fragment_viewpager)
    MyViewPager orderFragmentViewpager;

    List<BaseFragment> fragmentList=new ArrayList<BaseFragment>();
    //上面“全部，待付款，待发货”5个按钮的数组
    TextView[] tvs;
    int oldIndex = 0; //记录上一个ViewPager的页面的位置,用来把上一个位置的按钮颜色变为灰色

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);
        ButterKnife.inject(this);

        initView();
    }

    //初始化界面,设置初始显示的fragment
    void initView(){
        tvs = new TextView[]{orderFragmentTab1, orderFragmentTab2, orderFragmentTab3, orderFragmentTab4, orderFragmentTab6};
        tvs[0].setTextColor(Color.RED); //初始的第一个页面的按钮设为红色
        fragmentList.add(new OrderAllFragment());
        fragmentList.add(new UnPaidFragment());
        fragmentList.add(new UnSendFragment());
        fragmentList.add(new UnReceivedFragment());
        fragmentList.add(new UnAssessedFragment());

        //设置viewpager显示内容
        //orderFragmentViewpager.setOffscreenPageLimit(0); //设置预加载个数，设为0无效
        orderFragmentViewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        //设置页面改变监听事件
        orderFragmentViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                oldIndex=position; //滑动完后使oldIndex变为当前位置
                if(position!=0){  //设置相邻的左边按钮颜色为灰色
                    tvs[position-1].setTextColor(Color.GRAY);
                }
                if(position!=4){ //设置相邻的右边按钮颜色为灰色
                    tvs[position+1].setTextColor(Color.GRAY);
                }
                tvs[position].setTextColor(Color.RED);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.order_fragment_tab1, R.id.order_fragment_tab2, R.id.order_fragment_tab3, R.id.order_fragment_tab4, R.id.order_fragment_tab6, R.id.all_order_goback})
    public void onClick(View view) {
        if(view.getId()==R.id.all_order_goback){
            finish();  //返回主页面
        }

        int currentIndex = 0;  //viewPager显示项的位置
        switch (view.getId()) {
            case R.id.order_fragment_tab1:
                currentIndex = 0;
                break;
            case R.id.order_fragment_tab2:
                currentIndex = 1;
                break;
            case R.id.order_fragment_tab3:
                currentIndex = 2;
                break;
            case R.id.order_fragment_tab4:
                currentIndex = 3;
                break;
//            case R.id.order_fragment_tab5:
//                currentIndex = 4;
//                break;
            case R.id.order_fragment_tab6:
                currentIndex = 4;
                break;

        }
        tvs[oldIndex].setTextColor(Color.GRAY);//将上一个索引位置的按钮颜色变为灰色
        oldIndex = currentIndex;     //将当前索引数设为oldIndex
        orderFragmentViewpager.setCurrentItem(currentIndex);
    }


}
