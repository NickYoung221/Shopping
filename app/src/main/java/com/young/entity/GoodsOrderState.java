package com.young.entity;

public class GoodsOrderState {
	
	private int goodsOrderStateId;
	private String goodsOrderStates;//订单状态̬
	
	
	public GoodsOrderState(int goodsOrderStateId, String goodsOrderStates) {
		super();
		this.goodsOrderStateId = goodsOrderStateId;
		this.goodsOrderStates = goodsOrderStates;
	}
	public int getGoodsOrderStateId() {
		return goodsOrderStateId;
	}
	public void setGoodsOrderStateId(int goodsOrderStateId) {
		this.goodsOrderStateId = goodsOrderStateId;
	}
	public String getGoodsOrderStates() {
		return goodsOrderStates;
	}
	public void setGoodsOrderStates(String goodsOrderStates) {
		this.goodsOrderStates = goodsOrderStates;
	}
	

}
