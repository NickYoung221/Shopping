package com.young.shopping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.young.widget.TitleBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import c.b.BP;
import c.b.PListener;
import c.b.QListener;

/**
 * 支付的页面
 */
public class GoPayActivity extends AppCompatActivity {

    //Appid
    String APPID = "11e93b5e5fad3225bebec3fde45839e7";
    // 此为支付插件的官方最新版本号,请在更新时留意更新说明
    int PLUGINVERSION = 7;

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.gopay_prodmoney)
    TextView gopayProdmoney;
    @InjectView(R.id.gopay_servicemoney)
    TextView gopayServicemoney;
    @InjectView(R.id.gopay_yunfumoney)
    TextView gopayYunfumoney;
    @InjectView(R.id.gopay_youhuimoney)
    TextView gopayYouhuimoney;
    @InjectView(R.id.gopay_shifumoney)
    TextView gopayShifumoney;
    @InjectView(R.id.gopay_order_info)
    LinearLayout gopayOrderInfo;
    @InjectView(R.id.gopay_alipay)
    ImageView gopayAlipay;
    @InjectView(R.id.gopay_select_alipay)
    RadioButton gopaySelectAlipay;
    @InjectView(R.id.gopay_rl_alipay)
    RelativeLayout gopayRlAlipay;
    @InjectView(R.id.gopay_weixinpay)
    ImageView gopayWeixinpay;
    @InjectView(R.id.gopay_select_weixin)
    RadioButton gopaySelectWeixin;
    @InjectView(R.id.gopay_rl_weixinpay)
    RelativeLayout gopayRlWeixinpay;
    @InjectView(R.id.gopay_pay)
    Button gopayPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pay);
        ButterKnife.inject(this);
        BP.init(this,APPID);
        gopaySelectWeixin.setChecked(true); //默认设为微信支付
    }

    @OnClick({R.id.gopay_select_alipay,R.id.gopay_select_weixin, R.id.gopay_pay })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gopay_select_alipay: //用户选择了支付宝支付
                if(gopaySelectWeixin.isChecked()) {
                    gopaySelectWeixin.setChecked(false);
                }
                break;
            case R.id.gopay_select_weixin: //用户选择了微信支付
                if(gopaySelectAlipay.isChecked()){
                    gopaySelectAlipay.setChecked(false);
                }
                break;
            case R.id.gopay_pay://立即支付(根据用户选择)
                if(gopaySelectAlipay.isChecked()){
                    Toast.makeText(GoPayActivity.this, "支付宝支付", Toast.LENGTH_SHORT).show();
                    pay(true);
                }else if(gopaySelectWeixin.isChecked()){
                    Toast.makeText(GoPayActivity.this, "微信支付", Toast.LENGTH_SHORT).show();
                    pay(false);
                }
                break;
        }
    }

    /**
     * 调用支付
     *
     * @param alipayOrWechatPay
     *            支付类型，true为支付宝支付,false为微信支付
     */
    void pay(final boolean alipayOrWechatPay) {
        //showDialog("正在获取订单...");
        final String name = getName();//商品名
        
        BP.pay(name, getBody(), getPrice(), alipayOrWechatPay, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(GoPayActivity.this, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                //tv.append(name + "'s pay status is unknow\n\n");
                //hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(GoPayActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
                //tv.append(name + "'s pay status is success\n\n");
                //hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
//                order.setText(orderId);
//                tv.append(name + "'s orderid is " + orderId + "\n\n");
//                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(GoPayActivity.this,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付", Toast.LENGTH_LONG).show();
                    //installBmobPayPlugin("bp.db");
                } else {
                    Toast.makeText(GoPayActivity.this, "支付中断!", Toast.LENGTH_SHORT)
                            .show();
                }
//                tv.append(name + "'s pay status is fail, error code is \n"
//                        + code + " ,reason is " + reason + "\n\n");
//                hideDialog();
            }
        });
    }

    // 执行订单查询
//    void query() {
//        showDialog("正在查询订单...");
//        final String orderId = getOrder();
//
//        BP.query(orderId, new QListener() {
//
//            @Override
//            public void succeed(String status) {
//                Toast.makeText(GoPayActivity.this, "查询成功!该订单状态为 : " + status,
//                        Toast.LENGTH_SHORT).show();
//                tv.append("pay status of" + orderId + " is " + status + "\n\n");
//                hideDialog();
//            }
//
//            @Override
//            public void fail(int code, String reason) {
//                Toast.makeText(GoPayActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
//                tv.append("query order fail, error code is " + code
//                        + " ,reason is \n" + reason + "\n\n");
//                hideDialog();
//            }
//        });
//    }

    // 默认为0.02
    double getPrice() {
        double price = 0.02;
        try {
                //price = Double.parseDouble(this.price.getText().toString());
                price = 0.02;
            } catch (NumberFormatException e) {
        }
        return price;
    }

    // 商品详情(可不填)
    String getName() {
        //return this.name.getText().toString();
        return "毛衣";
    }
//
    // 商品详情(可不填)
    String getBody() {
        //return this.body.getText().toString();
        return "商品详情";
    }
//
//    // 支付订单号(查询时必填)
//    String getOrder() {
//        return this.order.getText().toString();
//    }

//    void showDialog(String message) {
//        try {
//            if (dialog == null) {
//                dialog = new ProgressDialog(this);
//                dialog.setCancelable(true);
//            }
//            dialog.setMessage(message);
//            dialog.show();
//        } catch (Exception e) {
//            // 在其他线程调用dialog会报错
//        }
//    }

//    void hideDialog() {
//        if (dialog != null && dialog.isShowing())
//            try {
//                dialog.dismiss();
//            } catch (Exception e) {
//            }
//    }

//    void installBmobPayPlugin(String fileName) {
//        try {
//            InputStream is = getAssets().open(fileName);
//            File file = new File(Environment.getExternalStorageDirectory()
//                    + File.separator + fileName + ".apk");
//            if (file.exists())
//                file.delete();
//            file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            byte[] temp = new byte[1024];
//            int i = 0;
//            while ((i = is.read(temp)) > 0) {
//                fos.write(temp, 0, i);
//            }
//            fos.close();
//            is.close();
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.parse("file://" + file),
//                    "application/vnd.android.package-archive");
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
