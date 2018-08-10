package com.bqt.test.litepal;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LitePalActivity extends ListActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"findAll()",
				"save() ",
				"find() 和 save()",
				"update()",
				"updateAll()",
				"delete() 和 deleteAll()",
				"fluent query",
				"Async operations: saveAsync() + listen()",
				"Async operations: findAllAsync() + listen()",
		};
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int random = new Random().nextInt(5);
		switch (position) {
			case 0:  //findAll()
				showTips(new Gson().toJson(DataSupport.findAll(Album.class)));
				//[{"age":1,"name":"包青天1","price":1.0,"size":0,"baseObjId":1},{"age":0,"name":"包青天0","price":0.0,"size":0,"baseObjId":2}]
				break;
			case 1: { //save()
				Album album = Album.newBuilder().name("包青天" + random).age(random).price(random).size(random).build();
				boolean success = album.save();
				showTips(success + "  " + new Gson().toJson(album));
				break;
			}
			case 2: { //find() 和 save() 和 findAll
				Album album = DataSupport.find(Album.class, random);
				if (album != null) {
					album.setPrice(9.99f); //仅更改指定的值
					showTips(random + "  " + album.save());
				} else {
					showTips("没有找到id=" + random + "的数据");
				}
				break;
			}
			case 3: { //update()
				Album album = Album.newBuilder().name("包青天0").age(random + 100).build();
				//注意：如果指定为 unique 字段的值已存在，则会报 DataSupportException: UNIQUE constraint failed: album.name
				try {
					int rowsAffected = album.update(random); //注意，未指定的字段的值不会更改，比如这里只更改 name 和 age 而不更改 price
					showTips(random + "  更新" + (rowsAffected == 0 ? "失败(未找到指定 id 的数据)" : rowsAffected + "条"));
				} catch (Exception e) {
					e.printStackTrace();
					showTips(random + "  更新失败(主键冲突 UNIQUE constraint failed )");
				}
				break;
			}
			case 4: {//updateAll()
				Album album = Album.newBuilder().age(random + 200).price(random + 200).size(random + 200).build();
				int rowsAffected = album.updateAll("name like ? and age > ?", "包青天%", Integer.toString(3));
				showTips(random + "  更新" + (rowsAffected == 0 ? "失败(未找到指定 id 的数据)" : rowsAffected + "条"));
				break;
			}
			case 5://delete() 和 deleteAll()
				int rowsAffected1 = DataSupport.delete(Album.class, random);
				int rowsAffected2 = DataSupport.deleteAll(Album.class, "age < ?", Integer.toString(200));
				showTips(random + "  " + rowsAffected1 + "  " + rowsAffected2);
				break;
			case 6:
				List<Album> list = DataSupport.where("name like ? and price <= ?", "包青天%", Float.toString(3.0f))
						.order("name")
						.find(Album.class);
				showTips(new Gson().toJson(list));
				break;
			case 7: {
				Album album = Album.newBuilder().name("包青天" + random).age(random).price(random).size(random).build();
				album.saveAsync().listen(success -> {
					boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();//是主线程
					showTips(random + "  " + success + " " + isMainThread);
				});
				break;
			}
			case 8:
				DataSupport.findAllAsync(Album.class).listen(new FindMultiCallback() {
					@Override
					public <T> void onFinish(List<T> t) {
						showTips(new Gson().toJson(t));
					}
				});
				break;
		}
	}
	
	private void showTips(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		Log.i("bqt", s);
	}
}