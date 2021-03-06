package com.bqt.test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bqt.test.greendao.GreenDaoActivity;
import com.bqt.test.litepal.LitePalActivity;
import com.bqt.test.ormlite.ORMLiteActivity;

import java.util.Arrays;

public class MainActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"LitePal",
				"GreenDao",
				"ORMLite",};
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
				startActivity(new Intent(this, LitePalActivity.class));
				break;
			case 1:
				startActivity(new Intent(this, GreenDaoActivity.class));
				break;
			case 2:
				startActivity(new Intent(this, ORMLiteActivity.class));
				break;
			default:
				break;
		}
	}
}