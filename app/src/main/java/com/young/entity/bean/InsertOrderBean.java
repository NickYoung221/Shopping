package com.young.entity.bean;

import java.util.Map;

import com.young.entity.Product;

/**
 * 向数据库插入订单(包括订单表和订单详情表)信息用到的bean
 */
public class InsertOrderBean {
	
	Integer userId;//用户id
	Integer addressId;//地址id
	Double totalPrice;//总价格
	Map<Product, Integer> details;//商品详情

	public InsertOrderBean(Integer userId, Integer addressId, Double totalPrice, Map<Product, Integer> details) {
		super();
		this.userId = userId;
		this.addressId = addressId;
		this.totalPrice = totalPrice;
		this.details = details;
	}

    public Integer getUserId() {
        return userId;
    }
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Map<Product, Integer> getDetails() {
		return details;
	}
	public void setDetails(Map<Product, Integer> details) {
		this.details = details;
	}
	
}
