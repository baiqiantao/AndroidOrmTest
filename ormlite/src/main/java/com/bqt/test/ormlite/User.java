package com.bqt.test.ormlite;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

public class User {
	@DatabaseField(generatedId = true) public int id;
	@DatabaseField(index = true) public String string;
	@DatabaseField public Date date;
	
	public User() { //必须具有至少包机可见[package visibility]的无参数构造函数，以便在执行查询时可以创建此类的对象
	}
	
	private User(Builder builder) {
		id = builder.id;
		string = builder.string;
		date = builder.date;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static final class Builder {
		private int id;
		private String string;
		private Date date;
		
		private Builder() {
		}
		
		public Builder id(int val) {
			id = val;
			return this;
		}
		
		public Builder string(String val) {
			string = val;
			return this;
		}
		
		public Builder date(Date val) {
			date = val;
			return this;
		}
		
		public User build() {
			return new User(this);
		}
	}
}