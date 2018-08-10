package com.bqt.test.litepal;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class Album extends DataSupport {
	@Column(unique = true, defaultValue = "包青天")
	private String name;
	
	private int age;//新增一个字段时需要更改数据库版本，否则操作数据时会失败
	private float price;
	
	@Column(ignore = true)//忽略掉的字段，此字段的值不会保存到数据库中
	private int size;
	
	//必须有一个默认的、public的无参构造器，否则执行 update 等语句时会崩溃
	public Album() {
	}
	
	private Album(Builder builder) {
		setName(builder.name);
		setAge(builder.age);
		setPrice(builder.price);
		setSize(builder.size);
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public static final class Builder {
		private String name;
		private int age;
		private float price;
		private int size;
		
		private Builder() {
		}
		
		public Builder name(String val) {
			name = val;
			return this;
		}
		
		public Builder age(int val) {
			age = val;
			return this;
		}
		
		public Builder price(float val) {
			price = val;
			return this;
		}
		
		public Builder size(int val) {
			size = val;
			return this;
		}
		
		public Album build() {
			return new Album(this);
		}
	}
}