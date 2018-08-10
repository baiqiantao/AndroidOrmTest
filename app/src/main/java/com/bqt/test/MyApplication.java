package com.bqt.test;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Desc：
 *
 * @author 白乾涛 <p>
 * @tag <p>
 * @date 2018/8/10 14:50 <p>
 */
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		LitePal.initialize(this);
	}
}
