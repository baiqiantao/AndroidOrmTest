package com.bqt.test.ormlite;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ORMLiteActivity extends ListActivity {
	private DbHelper helper;
	private Dao<User, Integer> userDao;
	private RuntimeExceptionDao<User, Integer> userRuntimeDao;
	
	private User user;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"0、添加：create",
				"1、添加：createIfNotExists",
				"2、添加：createOrUpdate",
				"3、事务操作：使用DatabaseConnection",
				"4、事务操作：使用AndroidDatabaseConnection",
				"5、更新：update、updateId",
				"6、删除：delete、deleteIds、deleteById",
				"7、QueryBuilder、UpdateBuilder、DeleteBuilder",
				"8、其他API",
				"9、查询并清除所有数据：queryForAll",};
		
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));
		
		user = User.newBuilder().date(new Date()).date(new Date()).string("====包青天====").build();
		try {
			helper = new DbHelper(this);
			userDao = helper.getUserDao();
			userRuntimeDao = helper.getUserRuntimeDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		helper.close();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		user.string = "【包青天" + id + "】" + System.currentTimeMillis();
		
		try {
			switch (position) {
				case 0:
					int updatNum1 = userDao.create(user);
					boolean isEqual1 = userDao.objectsEqual(user, userDao.queryForId(1));
					int updatNum2 = userDao.create(user);//[注意]对于同一个对象，不管是否存在，每次就会添加一条新的数据
					boolean isEqual2 = userDao.objectsEqual(user, userDao.queryForId(1));
					boolean isEqual3 = userDao.objectsEqual(user, userDao.queryForId(2));
					Log.i("bqt", "是否为同一对象：" + Arrays.asList(isEqual1, isEqual2, isEqual3));//[true, false, true]
					
					int updatNum3 = userDao.create(User.newBuilder().id(100).string("包青天0").build());//[注意]设置id无效
					Log.i("bqt", "更新数量：" + Arrays.asList(updatNum1, updatNum2, updatNum3));//[1, 1, 1]
					break;
				case 1://如果指定的对象不存在则插入，如果存在则不插入
					User newUser1 = userDao.createIfNotExists(user);//[垃圾]上面添加了多个，但这里只返回了最后一个
					//[注意]因为user已经存在，所以不会添加，也不更新数据库中对象的数据；并且返回的是数据库中的对象(旧的数据)
					boolean isEqual11 = userDao.objectsEqual(newUser1, user);
					boolean isEqual12 = userDao.objectsEqual(newUser1, userDao.queryForId(2));
					Log.i("bqt", "是否为同一对象：" + Arrays.asList(isEqual11, isEqual12));//[false, true]
					
					User newUser2 = userDao.createIfNotExists(User.newBuilder().string("包青天1").build());
					Log.i("bqt", new Gson().toJson(Arrays.asList(newUser1, newUser2)));
					break;
				case 2:
					Dao.CreateOrUpdateStatus status1 = userDao.createOrUpdate(user);//[注意]上面添加了多个，但这里只更改了最后一个
					Log.i("bqt", "是否为同一对象：" + userDao.objectsEqual(user, userDao.queryForId(2)));//true
					
					Dao.CreateOrUpdateStatus status2 = userDao.createOrUpdate(User.newBuilder().string("包青天2").build());
					//[{"created":false,"numLinesChanged":1,"updated":true},{"created":true,"numLinesChanged":1,"updated":false}]
					Log.i("bqt", new Gson().toJson(Arrays.asList(status1, status2)));
					break;
				case 3://使用DatabaseConnection
					DatabaseConnection dc = userDao.startThreadConnection();//也可以 new AndroidDatabaseConnection
					dc.setAutoCommit(false); //设置不自动提交
					
					Savepoint savePoint = dc.setSavePoint("savePointName");//设置回滚点，参数是一个名字，没什么影响
					for (User user : new User[]{user, user}) {
						userDao.createOrUpdate(user);
					}
					dc.commit(savePoint);//提交事务。保存大量数据时以事务的方式提交，可以大幅提高速度
					userDao.endThreadConnection(dc);
					break;
				case 4://使用AndroidDatabaseConnection
					AndroidDatabaseConnection adc = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
					userRuntimeDao.setAutoCommit(adc, false);// 设置不自动提交
					Savepoint savePoint4 = adc.setSavePoint("savePointName");//设置回滚点，参数是一个名字，没什么影响
					for (User user : new User[]{user, User.newBuilder().string("包青天4").build()}) {
						userDao.createOrUpdate(user);
					}
					adc.commit(savePoint4);
					adc.rollback(savePoint4); // 回滚事物。一般用于在发生异常时进行回滚
					break;
				case 5:
					int uNum1 = userDao.update(user);//[注意]同样更新的是最后添加的数据
					int uNum2 = userDao.update(User.newBuilder().build());
					int uNum3 = userDao.updateId(user, 100);//[注意]新添加的数据的id将从最大值开始
					int uNum4 = userDao.updateId(userDao.queryForId(3), -100);//[注意]值可以为负值
					Log.i("bqt", new Gson().toJson(Arrays.asList(uNum1, uNum2, uNum3, uNum4)));//[1,0,1,1]
					break;
				case 6:
					int dNum1 = userDao.delete(user);//[注意]这个删掉的也是最后一个添加的数据，前面添加的数据已经和user脱离关联了
					int dNum2 = userDao.delete(user);
					int dNum3 = userDao.delete(Arrays.asList(user, User.newBuilder().build()));
					int dNum4 = userDao.deleteById(3);
					int dNum5 = userDao.deleteIds(Arrays.asList(5, 6, 7, 8));
					Log.i("bqt", new Gson().toJson(Arrays.asList(dNum1, dNum2, dNum3, dNum4, dNum5)));//[1,0,0,1,1]
					break;
				case 7:
					QueryBuilder<User, Integer> builder = userDao.queryBuilder()
							.distinct()// 排重
							.groupBy("id")  //分组
							.limit(10L)//限制数量
							.offset(3L) //偏移
							.orderBy("date", true); //排序
					List<User> list = builder.where().like("string", "%包青天%").query();//条件查询
					Log.i("bqt", "查询到的数据：" + new Gson().toJson(list));
					break;
				case 8:
					User data = userDao.queryForId(1);//如果不存在则返回 null
					long count = userDao.countOf();//总数量
					Log.i("bqt", count + " " + new Gson().toJson(data));
					Log.i("bqt", userDao.isTableExists() + " " + userDao.isUpdatable() + " " + userDao.idExists(1));
				case 9:
					userDao.delete(userDao.queryForAll());
					break;
			}
			Log.i("bqt", "全部数据：" + new Gson().toJson(userDao.queryForAll()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}