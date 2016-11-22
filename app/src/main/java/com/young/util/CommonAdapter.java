package com.young.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/** 通用的适配器(抽象类，使用泛型)
 * Created by yang on 2016/10/2 0002.
 */
public abstract class CommonAdapter<T> extends BaseAdapter{

    Context context;
    List<T> lists;
    int layoutId;

    //构造方法
    public CommonAdapter(Context context, List<T> lists, int layoutId){
        this.context = context;
        this.lists = lists;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return lists!=null?lists.size():0;//若lists不为null，返回其size，若为null，返回0
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);  //每个Item的数据源
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //找到控件，赋值
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = ViewHolder.getViewHolder(context,layoutId,convertView,parent);
        convert(viewHolder,lists.get(position),position);

        return viewHolder.getConvertView();
    }

    //取出控件，赋值
    public abstract void convert(ViewHolder viewHolder,T t,int position);


}
