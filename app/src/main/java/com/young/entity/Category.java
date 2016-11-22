package com.young.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 标签分类
 * @author yang
 *
 */
public class Category implements Parcelable{

	private int categoryId;
	private String categoryName;
	
	public Category(int categoryId, String categoryName) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}
	
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.categoryId);
		dest.writeString(this.categoryName);
	}

	protected Category(Parcel in) {
		this.categoryId = in.readInt();
		this.categoryName = in.readString();
	}

	public static final Creator<Category> CREATOR = new Creator<Category>() {
		@Override
		public Category createFromParcel(Parcel source) {
			return new Category(source);
		}

		@Override
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
}
