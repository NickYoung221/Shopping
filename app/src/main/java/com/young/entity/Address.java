package com.young.entity;

public class Address {
	private int addressId;
	private int userId;
	private String cantactName;
	
	private String cantactPhone;
	
	private String contactAddress;
	private boolean isDefault;
	
	public String getCantactName() {
		return cantactName;
	}

	public Address(int addressId, int userId, String cantactName, String cantactPhone, String contactAddress,
			boolean isDefault) {
		super();
		this.addressId = addressId;
		this.userId = userId;
		this.cantactName = cantactName;
		this.cantactPhone = cantactPhone;
		this.contactAddress = contactAddress;
		this.isDefault = isDefault;
	}

	public void setCantactName(String cantactName) {
		this.cantactName = cantactName;
	}

	public String getCantactPhone() {
		return cantactPhone;
	}

	public void setCantactPhone(String cantactPhone) {
		this.cantactPhone = cantactPhone;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	

}
