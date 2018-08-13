package com.bqt.test.greendao;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GreenDaoActivity extends ListActivity {
	private Note mNote = Note.newBuilder().text("【text】").date(new Date()).comment("包青天").type(NoteType.PICTURE).build();
	private NoteDao dao;
	private EditText editText;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"0、插入一条数据",
				"1、演示 insert 数据时一些特殊情况",
				"2、插入数据：insertInTx、insertWithoutSettingPk、insertOrReplace、save、saveInTx",
				"3、删除数据：delete、deleteAll、deleteByKey、deleteByKeyInTx、deleteInTx",
				"4、更新数据：update、updateInTx、updateKeyAfterInsert",
				"5、查询数据：where、whereOr、limit、offset、*order*、distinct、or、and",
				"6、加载数据：load、loadByRowId、loadAll",
				"7、其他API：",
				"8、删除所有数据：deleteAll",
				"tag 值 +1",};
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));
		editText = new EditText(this);
		editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		getListView().addFooterView(editText);
		initDB();
	}
	
	private void initDB() {
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
		Database db = helper.getWritableDb();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		dao = daoSession.getNoteDao();
	}
	
	int tag = -1;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (TextUtils.isEmpty(editText.getText())) {
			tag++;
			Toast.makeText(this, "最新值为" + tag, Toast.LENGTH_SHORT).show();
		} else {
			tag = Integer.parseInt(editText.getText().toString());
			editText.setText("");
		}
		switch (position) {
			case 0://插入一条数据
				simpleInsert();
				break;
			case 1://演示 insert 数据时一些特殊情况
				insert();
				break;
			case 2://插入数据
				inserts();
				break;
			case 3://删除数据
				deletes();
				break;
			case 4://更新数据
				updates();
				break;
			case 5://查询数据
				query();
				break;
			case 6:
				load();
				break;
			case 7:
				testOtherApi();
				break;
			case 8:
				dao.deleteAll();
				break;
		}
	}
	
	private void simpleInsert() {
		Note insertNote = Note.newBuilder().type(tag % 3 == 2 ? NoteType.UNKNOWN :
				(tag % 3 == 1 ? NoteType.PICTURE : NoteType.TEXT))
				.comment("comment" + tag).date(new Date()).text("text" + tag).build();
		long insertId = dao.insert(insertNote);// 将给定的实体插入数据库，默认情况下，插入的数据的Id将从1开始递增
		Log.i("bqt", "插入数据的ID= " + insertId + " 全部数据" + getAllDataString());
	}
	
	private void insert() {
		long id = -10086;//以下注释不管是否给 id 设置【@Id(autoincrement = true)】注解时的得出的结论都是一样的
		if (tag % 9 == 6) id = dao.insert(new Note());//同样会崩溃，插入数据的 text 不能为空(IllegalArgumentException)
		else if (tag % 9 == 5) id = dao.insert(Note.newBuilder().id(6L).text("text5").build());//会崩溃，因为此 id 已经存在
		else if (tag % 9 == 4) id = dao.insert(Note.newBuilder().text("text4").build());//id =12
		else if (tag % 9 == 3) id = dao.insert(Note.newBuilder().id(6L).text("text3").build());//自定义id，id =6
		else if (tag % 9 == 2) id = dao.insert(Note.newBuilder().text("text2").type(NoteType.UNKNOWN).build());//id = 11
		else if (tag % 9 == 1) id = dao.insert(Note.newBuilder().id(10L).text("text1").build());//插入的数据的Id将从最大值开始
		else if (tag % 9 == 0) id = dao.insert(Note.newBuilder().id(-2L).text("text0").build());//id =-2，并不限制id的值
		Log.i("bqt", "插入数据的ID= " + id + " 全部数据" + getAllDataString());
	}
	
	private void inserts() {
		Note note1 = Note.newBuilder().text("text1-" + tag).date(new Date()).comment("包青天").type(NoteType.TEXT).build();
		Note note2 = Note.newBuilder().text("text2-" + tag).date(new Date()).comment("包青天").type(NoteType.TEXT).build();
		if (tag % 9 >= 6) {
			dao.save(note1);// 将给定的实体插入数据库，若此实体类存在，则【更新】
			Note quryNote = dao.queryBuilder().build().list().get(0);
			quryNote.setText("【新的Text2】");
			mNote.setText("【新的Text2】");
			dao.saveInTx(quryNote, mNote);//同样参数可以时集合，基本上所有的 **InTx 方法都是这样的
		} else if (tag % 9 == 5) {
			Note quryNote = dao.queryBuilder().build().list().get(0);
			quryNote.setText("【新的Text】");
			mNote.setText("【新的Text】");
			dao.insertOrReplaceInTx(quryNote, mNote);//同样参数可以时集合，并可选择是否设定主键
		} else if (tag % 9 == 4) {
			long rowId = dao.insertOrReplace(mNote);// 将给定的实体插入数据库，若此实体类存在则【覆盖】，返回新插入实体的行ID
			Log.i("bqt", "对象的ID=" + mNote.getId() + "  返回的ID=" + rowId);
		} else if (tag % 9 == 3) dao.insertWithoutSettingPk(note1);//插入数据库但不设定主键(不明白含义)，返回新插入实体的行ID
		else if (tag % 9 == 2) dao.insertInTx(Arrays.asList(note1, note2), false);//并设置是否设定主键(不明白含义)
		else if (tag % 9 == 1) dao.insertInTx(Arrays.asList(note1, note2));// 使用事务操作，将给定的实体集合插入数据库
		else if (tag % 9 == 0) dao.insertInTx(note1, note2);// 使用事务操作，将给定的实体集合插入数据库
		Log.i("bqt", "全部数据" + getAllDataString());
	}
	
	private void deletes() {
		if (tag % 9 >= 7) dao.deleteAll(); //删除数据库中全部数据。再插入的数据的 Id 同样将从最大值开始
		else if (tag % 9 == 6) dao.deleteByKeyInTx(Arrays.asList(1L, 3L));
		else if (tag % 9 == 5) dao.deleteByKeyInTx(1L, 2L);// 使用事务操作删除数据库中给定的所有key所对应的实体
		else if (tag % 9 == 4) dao.deleteByKey(1L);//从数据库中删除给定Key所对应的实体
		else if (tag % 9 == 3) dao.delete(new Note());//从数据库中删除给定的实体
		else if (tag % 9 == 2) dao.deleteInTx(new Note(), new Note());// 使用事务操作删除数据库中给定实体集合中的实体
		else if (tag % 9 == 1) dao.deleteInTx(Arrays.asList(new Note(), new Note()));
		else if (tag % 9 == 0) dao.deleteInTx(dao.queryBuilder().limit(1).list());
		Log.i("bqt", "全部数据" + getAllDataString());
	}
	
	private void query() {
		WhereCondition cond1 = NoteDao.Properties.Id.eq(1);//==
		WhereCondition cond2 = NoteDao.Properties.Type.notEq(NoteTypeConverter.TYPE_UNKNOWN);//!=
		WhereCondition cond3 = NoteDao.Properties.Id.gt(10);//大于
		WhereCondition cond4 = NoteDao.Properties.Id.le(5);// less and eq 小于等于
		WhereCondition cond5 = NoteDao.Properties.Id.in(1, 4, 10, 12);//在某些值内，notIn	不在某些值内
		WhereCondition cond6 = NoteDao.Properties.Text.like("%【%");//包含
		// 最常用Like通配符：下划线_代替一个任意字符(相当于正则表达式中的 ?)   百分号%代替任意数目的任意字符(相当于正则表达式中的 *)
		WhereCondition cond7 = NoteDao.Properties.Date.between(System.currentTimeMillis() - 1000 * 60 * 20, new Date());//20分钟
		
		List<Note> list = null;
		if (tag % 9 >= 6) {
			QueryBuilder<Note> qb = dao.queryBuilder();
			WhereCondition condOr = qb.or(cond2, cond3, cond4);
			WhereCondition condAnd = qb.and(cond5, cond6, cond7);
			list = qb.where(cond1, condOr, condAnd).build().list();
		} else if (tag % 9 == 5) list = dao.queryBuilder().whereOr(cond5, cond6, cond7).build().list();//多个语句间是 OR 的关系
		else if (tag % 9 == 4) list = dao.queryBuilder().where(cond5, cond6, cond7).build().list();//多个语句间是 AND 的关系
		else if (tag % 9 == 3) list = dao.queryBuilder().where(cond4).offset(1).limit(2).build().list();//(必须)与limit一起设置查询结果的偏移量
		else if (tag % 9 == 2) list = dao.queryBuilder().where(cond5).limit(2).distinct().build().list();//限制查询结果个数，避免重复的实体(不明白)
		else if (tag % 9 == 1) list = dao.queryBuilder().where(cond2)
				.orderAsc(NoteDao.Properties.Type, NoteDao.Properties.Date, NoteDao.Properties.Text)//常用升序orderAsc、降序orderDesc
				.build().list();//其他排序API：orderRaw(使用原生的SQL语句)、orderCustom、stringOrderCollation、preferLocalizedStringOrder
		else if (tag % 9 == 0) list = dao.queryBuilder().build().list();
		Log.i("bqt", "全部数据" + getAllDataString() + "\n查询到的数据" + new Gson().toJson(list));
	}
	
	private void updates() {
		dao.save(mNote);//确保要更新的实体已存在
		
		if (tag % 9 >= 4) dao.update(Note.newBuilder().text("text-update0").build()); //如果要更新的实体不存在，则会报DaoException异常
		else if (tag % 9 == 3) {
			mNote.setText("【Text-update3】");
			long rowId = dao.updateKeyAfterInsert(mNote, 666L);
			Log.i("bqt", "rowId=" + rowId);//更新实体内容，并更新key
		} else if (tag % 9 == 2) {
			mNote.setText("【Text-update2】");
			dao.updateInTx(Arrays.asList(dao.queryBuilder().build().list().get(0), mNote));// 使用事务操作，更新给定的实体
		} else if (tag % 9 == 1) {
			Note updateNote = dao.queryBuilder().build().list().get(0);
			updateNote.setText("【Text-update1】");
			dao.updateInTx(updateNote, mNote);// 使用事务操作，更新给定的实体
		} else if (tag % 9 == 0) dao.update(mNote);//更新给定的实体，不管有没有变化，只需用新的实体内容替换旧的内容即可(必须是同一实体)
		Log.i("bqt", "全部数据" + getAllDataString());
	}
	
	private void load() {
		Note note1 = dao.load(1L);
		Note note2 = dao.load(10086L);
		Note note3 = dao.loadByRowId(1L);
		Log.i("bqt", new Gson().toJson(Arrays.asList(note1, note2, note3)));
	}
	
	private void testOtherApi() {
		//查找指定实体的Key，当指定实体在数据库表中不存在时返回 null(而不是返回-1或其他值)
		Log.i("bqt", dao.getKey(mNote) + "  " + dao.getKey(Note.newBuilder().text("text").build()));
		//执行原生的查询
		List<Note> list = dao.queryRaw("select * from BQT_USERS where id= '2'");
		Log.i("bqt", new Gson().toJson(list));
	}
	
	private String getAllDataString() {
		return new Gson().toJson(dao.loadAll()); //dao.loadAll() 等价于 dao.queryBuilder().build().list()
	}
}