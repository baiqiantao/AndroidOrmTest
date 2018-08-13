package com.bqt.test.greendao;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

public class App extends Application {
	public static final boolean ENCRYPTED = true; //在 SQLite 和  encrypted SQLCipher 之间转换的标志
	
	private DaoSession daoSession;
	
	@Override
	public void onCreate() {
		super.onCreate();
		String name = ENCRYPTED ? "notes-db-encrypted" : "notes-db";
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, name);
		Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
		daoSession = new DaoMaster(db).newSession();
	}
	
	public DaoSession getDaoSession() {
		return daoSession;
	}
}
