package com.young.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.young.entity.Product;

import java.util.HashMap;
import java.util.Map;

/** 自定义一个类，实现Parcelable接口，用来在intent间传Map<Product,Integer>
 * Created by yang on 2016/10/9 0009.
 */
public class MyOrderMap implements Parcelable {

    public Map<Product,Integer> orderMap;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.orderMap.size());
        for (Map.Entry<Product, Integer> entry : this.orderMap.entrySet()) {
            dest.writeParcelable(entry.getKey(), flags);
            dest.writeValue(entry.getValue());
        }
    }

    public MyOrderMap() {
    }

    protected MyOrderMap(Parcel in) {
        int orderMapSize = in.readInt();
        this.orderMap = new HashMap<Product, Integer>(orderMapSize);
        for (int i = 0; i < orderMapSize; i++) {
            Product key = in.readParcelable(Product.class.getClassLoader());
            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
            this.orderMap.put(key, value);
        }
    }

    public static final Parcelable.Creator<MyOrderMap> CREATOR = new Parcelable.Creator<MyOrderMap>() {
        @Override
        public MyOrderMap createFromParcel(Parcel source) {
            return new MyOrderMap(source);
        }

        @Override
        public MyOrderMap[] newArray(int size) {
            return new MyOrderMap[size];
        }
    };

    public Map<Product, Integer> getOrderMap() {
        return orderMap;
    }

    public void setOrderMap(Map<Product, Integer> orderMap) {
        this.orderMap = orderMap;
    }
}
