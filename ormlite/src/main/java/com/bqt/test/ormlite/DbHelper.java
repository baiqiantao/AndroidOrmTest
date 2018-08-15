package com.bqt.test.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DbHelper extends OrmLiteSqliteOpenHelper {
	private static final String TABLE_NAME = "sqlite-test.db";
	private static final int DATABASE_VERSION = 1;
	private Dao<User, Integer> userDao = null;
	private RuntimeExceptionDao<User, Integer> userRuntimeDao;
	
	public DbHelper(Context context) {
		super(context, TABLE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			Log.i("bqt", "onCreate，版本：" + DATABASE_VERSION);
			
			TableUtils.createTable(connectionSource, User.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i("bqt", "onUpgrade，旧版本：" + oldVersion + "，新版本：" + newVersion);
			
			TableUtils.dropTable(connectionSource, User.class, true);
			
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		super.close();
		userDao = null;
	}
	
	public Dao<User, Integer> getUserDao() throws SQLException {
		return userDao == null ? getDao(User.class) : userDao;
	}
	
	public RuntimeExceptionDao<User, Integer> getUserRuntimeDao() {
		return userRuntimeDao == null ? getRuntimeExceptionDao(User.class) : userRuntimeDao;
	}
}