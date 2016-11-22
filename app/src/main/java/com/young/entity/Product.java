package com.young.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Timestamp;


public class Product implements Parcelable{

	private Integer id;
	private String name;
	private Integer stock;//库存
	private double price;

	private String imgurl;
	private String description;
	private Integer productSaleNumber;//商品销量
	private Category category;//类别
	
	private Timestamp createTime;//商品发布时间
	private User user;//卖家信息：User对象
	
	
	
	public Product(Integer id, String name, Integer stock, double price, String imgurl, String description,
			Integer productSaleNumber, Category category, Timestamp createTime, User user) {
		super();
		this.id = id;
		this.name = name;
		this.stock = stock;
		this.price = price;
		this.imgurl = imgurl;
		this.description = description;
		this.productSaleNumber = productSaleNumber;
		this.category = category;
		this.createTime = createTime;
		this.user = user;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getProductSaleNumber() {
		return productSaleNumber;
	}

	public void setProductSaleNumber(Integer productSaleNumber) {
		this.productSaleNumber = productSaleNumber;
	}

	//只有商品信息和商品价格的构造方法
	public  Product(String name,double price){
		this.name=name;
		this.price=price;
		
	}

	public Product(Integer id, String name, Integer stock, double price, String imgurl, Integer productSaleNumber,String description) {
		this.id = id;
		this.name = name;
		this.stock = stock;
		this.price = price;
		
		
		this.imgurl = imgurl;
		this.productSaleNumber=productSaleNumber;
		this.description = description;
	}

	

	public Product(Integer id,String name,double price,int count,
			String imagurl,String description){
		this.id=id;
		this.name=name;
		this.price=price;
		
		

		this.imgurl=imagurl;
		this.description=description;
		
	}
	//只有Id和价格的构造方法
	public Product(Integer id,double price){
		this.id=id;
		this.price=price;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	public String getImgurl() {
		return imgurl;
	}

	/*public String getImgurls(){
		return imgurl.substring(0,imgurl.lastIndexOf("."))+"_s"+imgurl.substring(imgurl.lastIndexOf("."));
	}*/
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price
				+  ", imgurl="
				+ imgurl + ", description=" + description + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.id);
		dest.writeString(this.name);
		dest.writeValue(this.stock);
		dest.writeDouble(this.price);
		dest.writeString(this.imgurl);
		dest.writeString(this.description);
		dest.writeValue(this.productSaleNumber);
		dest.writeParcelable(this.category, flags);
		dest.writeSerializable(this.createTime);
		dest.writeParcelable(this.user, flags);
	}

	protected Product(Parcel in) {
		this.id = (Integer) in.readValue(Integer.class.getClassLoader());
		this.name = in.readString();
		this.stock = (Integer) in.readValue(Integer.class.getClassLoader());
		this.price = in.readDouble();
		this.imgurl = in.readString();
		this.description = in.readString();
		this.productSaleNumber = (Integer) in.readValue(Integer.class.getClassLoader());
		this.category = in.readParcelable(Category.class.getClassLoader());
		this.createTime = (Timestamp) in.readSerializable();
		this.user = in.readParcelable(User.class.getClassLoader());
	}

	public static final Creator<Product> CREATOR = new Creator<Product>() {
		@Override
		public Product createFromParcel(Parcel source) {
			return new Product(source);
		}

		@Override
		public Product[] newArray(int size) {
			return new Product[size];
		}
	};


}
