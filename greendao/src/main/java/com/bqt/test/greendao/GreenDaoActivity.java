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

import org.greenrobot.greendao.Property;
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
				"2、插入数据：insertInTx、insertOrReplace、save、saveInTx、insertWithoutSettingPk",
				"3、删除数据：delete、deleteAll、deleteByKey、deleteByKeyInTx、deleteInTx",
				"4、更新数据：update、updateInTx、updateKeyAfterInsert",
				"5、查询数据：where、whereOr、or、and、limit、offset、*order*、distinct、queryRaw*",
				"6、数据加载和缓存：load、loadByRowId、loadAll、detach、detachAll、unique*",
				"7、其他API：getKey、getPkProperty、getProperties、getPkColumns、getNonPkColumns",
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
				loadAndDetach();
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
		Log.i("bqt", "插入数据的ID= " + insertId + getAllDataString());
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
		Log.i("bqt", "插入数据的ID= " + id + getAllDataString());
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
		Log.i("bqt", getAllDataString());
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
		Log.i("bqt", getAllDataString());
	}
	
	private void query() {
		WhereCondition cond1 = NoteDao.Properties.Id.eq(1);//==
		WhereCondition cond2 = NoteDao.Properties.Type.notEq(NoteTypeConverter.TYPE_UNKNOWN);//!=
		//在构建查询时，必须使用数据库值类型。 因为我们使用转换器将枚举类型映射到 String 值，则应在查询中使用 String 值
		WhereCondition cond3 = NoteDao.Properties.Id.gt(10);//大于
		WhereCondition cond4 = NoteDao.Properties.Id.le(5);// less and eq 小于等于
		WhereCondition cond5 = NoteDao.Properties.Id.in(1, 4, 10);//可以是集合。在某些值内，notIn	不在某些值内
		WhereCondition cond6 = NoteDao.Properties.Text.like("%【%");//包含
		// 最常用Like通配符：下划线_代替一个任意字符(相当于正则表达式中的 ?)   百分号%代替任意数目的任意字符(相当于正则表达式中的 *)
		WhereCondition cond7 = NoteDao.Properties.Date.between(System.currentTimeMillis() - 1000 * 60 * 20, new Date());//20分钟
		WhereCondition cond8 = NoteDao.Properties.Date.isNotNull();//isNull
		WhereCondition cond9 = new WhereCondition.StringCondition(NoteDao.Properties.Id.columnName + ">=? ", 2L);
		
		//其他排序API：orderRaw(使用原生的SQL语句)、orderCustom、stringOrderCollation、preferLocalizedStringOrder
		Property[] orders = new Property[]{NoteDao.Properties.Type, NoteDao.Properties.Date, NoteDao.Properties.Text};//同样支持可变参数
		List<Note> list = null;
		
		if (tag % 9 == 8) {
			String where = "where " + NoteDao.Properties.Type.columnName + " like ? AND " + NoteDao.Properties.Id.columnName + ">=?";
			list = dao.queryRaw(where, "%" + NoteTypeConverter.TYPE_TEXT + "%", Long.toString(2L));
			Log.i("bqt", "queryRaw查询到的数据" + new Gson().toJson(list));
			list = dao.queryRawCreate(where, "%" + NoteTypeConverter.TYPE_TEXT + "%", Long.toString(2L)).list();
		} else if (tag % 9 == 7) list = dao.queryBuilder().where(cond9).build().list(); //执行原生的SQL查询语句
		else if (tag % 9 == 6) {
			QueryBuilder<Note> qb = dao.queryBuilder();
			WhereCondition condOr = qb.or(cond2, cond3, cond4);
			WhereCondition condAnd = qb.and(cond6, cond7, cond8);
			list = qb.where(cond1, condOr, condAnd).build().list();
		} else if (tag % 9 == 5) list = dao.queryBuilder().whereOr(cond5, cond6, cond7).build().list();//多个语句间是 OR 的关系
		else if (tag % 9 == 4) list = dao.queryBuilder().where(cond5, cond6, cond7).build().list();//多个语句间是 AND 的关系
		else if (tag % 9 == 3) list = dao.queryBuilder().where(cond4).offset(1).limit(2).build().list();//(必须)与limit一起设置查询结果的偏移量
		else if (tag % 9 == 2) list = dao.queryBuilder().where(cond5).limit(2).distinct().build().list();//限制查询结果个数，避免重复的实体(不明白)
		else if (tag % 9 == 1) list = dao.queryBuilder().where(cond2).orderAsc(orders).build().list(); //常用升序orderAsc、降序orderDesc
		else if (tag % 9 == 0) list = dao.queryBuilder().build().list();
		Log.i("bqt", getAllDataString() + "\n查询到的数据" + new Gson().toJson(list));
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
		Log.i("bqt", getAllDataString());
	}
	
	private void loadAndDetach() {
		Note note1 = dao.load(1L);// 加载给定主键的实体
		Note note2 = dao.load(10086L);//如果给定主键不存在则返回null
		Note note3 = dao.loadByRowId(1L);// 加载某一行并返回该行的实体
		Note note4 = dao.queryBuilder().limit(1).unique();//返回一个元素(可能为null)。如果查询结果数量不是0或1，则抛出DaoException
		Note note5 = dao.queryBuilder().limit(1).build().uniqueOrThrow(); //保证返回一个非空的实体(否则会抛出一个 DaoException)
		
		if (tag % 9 >= 2) {
			note1.setText("-------Text" + System.currentTimeMillis());
			Log.i("bqt", dao.load(1L).getText());//因为这里获取到的实体对象仍然是 note1 ，所以这里的值立即就改变了！
			dao.detachAll();//清除指定实体缓存，将会重新从数据库中查询，然后封装成新的对象
			Log.i("bqt", dao.load(1L).getText());//因为只更改了实体但没有调用更新数据库的方法，所以数据库中的数据并没有改变
		} else if (tag % 9 == 1) {
			dao.detach(note4);//清除指定实体缓存；dao.detachAll 清除当前表的所有实体缓存；daoSession.clear() 清除所有的实体缓存
			Note note7 = dao.load(1L);// 清除指定Dao类的缓存后，再次得到的实体就是构造的新的对象
			Log.i("bqt", (note1 == note3 && note1 == note4 && note1 == note5) + "  " + (note1 == note7));//true  false
		} else if (tag % 9 == 0) {
			Log.i("bqt", new Gson().toJson(Arrays.asList(note1, note2, note3, note4, note5)));
		}
	}
	
	private void testOtherApi() {
		dao.save(mNote);//确保要更新的实体已存在
		//查找指定实体的Key，当指定实体在数据库表中不存在时返回 null(而不是返回-1或其他值)
		Log.i("bqt", "实体的 Key = " + dao.getKey(mNote) + "  " + dao.getKey(Note.newBuilder().text("text").build()));
		
		Property pk = dao.getPkProperty();//id	_id	0	true	class java.lang.Long
		for (Property p : dao.getProperties()) {
			Log.i("bqt", p.name + "\t" + p.columnName + "\t" + p.ordinal + "\t" + p.primaryKey + "\t" + p.type + "\t" + (p == pk));
		}
		
		Log.i("bqt", "所有的PK列：" + Arrays.toString(dao.getPkColumns()));//[_id]
		Log.i("bqt", "所有的非PK列：" + Arrays.toString(dao.getNonPkColumns()));//[TEXT, TIME, TYPE]
	}
	
	private String getAllDataString() {
		List<Note> list1 = dao.queryBuilder().build().list();//缓存查询结果
		List<Note> list2 = dao.queryBuilder().list();
		List<Note> list3 = dao.loadAll();
		return " 个数=" + dao.count() + "，等价=" + (list1.equals(list2) && list1.equals(list3)) + "，数据=" + new Gson().toJson(list1);//true
	}
}