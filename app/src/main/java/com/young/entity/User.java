package com.young.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	 
	private Integer userId;
	private String userName;
	private String userPwd;
	private String phone;
	private String nick;
	private String imageUrl;
	private String userSex;
	private String storeName;//商店名

	public User(){

	}

	public User(Integer userId, String userName){
		this.userId = userId;
		this.userName = userName;
	}

	public User(Integer userId, String userName, String userPwd, String phone, String nick, String imageUrl,
			String userSex, String storeName) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.userPwd = userPwd;
		this.phone = phone;
		this.nick = nick;
		this.imageUrl = imageUrl;
		this.userSex = userSex;
		this.storeName = storeName;
	}

	public User(String userName, String userPwd, String phone, String nick, String imageUrl, String userSex, String storeName) {
		this.userName = userName;
		this.userPwd = userPwd;
		this.phone = phone;
		this.nick = nick;
		this.imageUrl = imageUrl;
		this.userSex = userSex;
		this.storeName = storeName;
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getUserSex() {
		return userSex;
	}
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.userId);
		dest.writeString(this.userName);
		dest.writeString(this.userPwd);
		dest.writeString(this.phone);
		dest.writeString(this.nick);
		dest.writeString(this.imageUrl);
		dest.writeString(this.userSex);
		dest.writeString(this.storeName);
	}

	protected User(Parcel in) {
		this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
		this.userName = in.readString();
		this.userPwd = in.readString();
		this.phone = in.readString();
		this.nick = in.readString();
		this.imageUrl = in.readString();
		this.userSex = in.readString();
		this.storeName = in.readString();
	}

	public static final Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

}
