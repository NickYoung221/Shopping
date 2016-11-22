package com.young.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/** 自定义的ViewHolder(用于在通用的适配器上)
 * Created by yang on 2016/10/2 0002.
 */
public class ViewHolder {
    View convertView;
    SparseArray<View> sparseArray; //  key:int ,value:View

    //构造方法
    private ViewHolder(Context context, int layoutId, ViewGroup parent){
        //解析布局文件
        this.convertView = LayoutInflater.from(context).inflate(layoutId,null);

        convertView.setTag(this);

        sparseArray = new SparseArray<View>();

    }

    //获取ViewHolder对象
    public static ViewHolder getViewHolder(Context context, int layoutId, View convertView, ViewGroup parent){
        ViewHolder viewHolder;

        if(convertView==null){
            viewHolder = new ViewHolder(context,layoutId,parent);//若convertView为null，创建ViewHolder对象
        }else{
            viewHolder = (ViewHolder) convertView.getTag();//获取ViewHolder
        }
        return viewHolder;
    }

    //返回ViewHolder关联的convertView
    public View getConvertView(){
        return convertView;
    }

    //根据Id查找View
    public <T extends View>T getViewById(int resourceId){
        View v = sparseArray.get(resourceId);

        //若没有找到View
        if(v==null){
            v = convertView.findViewById(resourceId);
            sparseArray.put(resourceId,v);
        }
        return (T) v;
    }


}
