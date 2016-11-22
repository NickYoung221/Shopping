package com.young.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.young.shopping.R;

/**
 * 标题栏
 *  有自定义的属性，然后解析对应的属性值，赋值给tv
 * Created by yang on 2016/10/9 0009.
 */
public class TitleBar extends RelativeLayout{

    protected RelativeLayout leftLayout;
    protected ImageView leftImage;
    protected RelativeLayout rightLayout;
    protected ImageView rightImage;
    protected TextView titleView;
    protected RelativeLayout titleLayout;

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TitleBar(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.widget_title_bar, this);
        leftLayout = (RelativeLayout) findViewById(R.id.left_layout);
        leftImage = (ImageView) findViewById(R.id.left_image);
        rightLayout = (RelativeLayout) findViewById(R.id.right_layout);
        rightImage = (ImageView) findViewById(R.id.right_image);
        titleView = (TextView) findViewById(R.id.title);
        titleLayout = (RelativeLayout) findViewById(R.id.root);
//
        parseStyle(context, attrs);
    }

    private void parseStyle(Context context, AttributeSet attrs){
        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
            //获取文本
            String title = ta.getString(R.styleable.TitleBar_titleBarTitle);
            titleView.setText(title);

            //默认白色
            int color=ta.getColor(R.styleable.TitleBar_titleBarColor,0XFFFFFFFF);

            titleView.setTextColor(color);//设置文本的颜色



            Drawable leftDrawable = ta.getDrawable(R.styleable.TitleBar_titleBarLeftImage);
            //如果设置了左边的图片
            if (null != leftDrawable) {
                leftImage.setImageDrawable(leftDrawable);
            }
            Drawable rightDrawable = ta.getDrawable(R.styleable.TitleBar_titleBarRightImage);
            //如果设置了右边的图片
            if (null != rightDrawable) {
                rightImage.setImageDrawable(rightDrawable);
            }

            Drawable background = ta.getDrawable(R.styleable.TitleBar_titleBarBackground);
            //如果设置了背景
            if(null != background) {
                titleLayout.setBackgroundDrawable(background);
            }
            ta.recycle();
        }
    }

    public void setLeftImageResource(int resId) {
        leftImage.setImageResource(resId);
    }

    public void setRightImageResource(int resId) {
        rightImage.setImageResource(resId);
    }

    public void setLeftLayoutClickListener(OnClickListener listener){
        leftLayout.setOnClickListener(listener);
    }

    public void setRightLayoutClickListener(OnClickListener listener){
        rightLayout.setOnClickListener(listener);
    }

    public void setLeftLayoutVisibility(int visibility){
        leftLayout.setVisibility(visibility);
    }

    public void setRightLayoutVisibility(int visibility){
        rightLayout.setVisibility(visibility);
    }

    public void setTitle(String title){
        titleView.setText(title);
    }

    public void setBackgroundColor(int color){
        titleLayout.setBackgroundColor(color);
    }

    public RelativeLayout getLeftLayout(){
        return leftLayout;
    }

    public RelativeLayout getRightLayout(){
        return rightLayout;
    }

    public ImageView getLeftImage(){
        return leftImage;
    }

}
