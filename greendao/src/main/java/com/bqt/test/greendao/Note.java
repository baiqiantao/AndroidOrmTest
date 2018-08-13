package com.bqt.test.greendao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Date;

@Entity(
		nameInDb = "BQT_USERS",// 指定该表在数据库中的名称，默认是基于实体类名
		indexes = {@Index(value = "text, date DESC", unique = true)},// 定义跨多个列的索引
		createInDb = true,// 高级标志，是否创建该表。默认为true。如果有多个实体映射一个表，或者该表已在外部创建，则可置为false
		//schema = "bqt_schema",// 告知GreenDao当前实体属于哪个schema。属于不同模式的实体应不具有关系。
		active = true,// 标记一个实体处于活动状态，活动实体(设置为true)有更新、删除和刷新方法。默认为false
		generateConstructors = true,//是否生成所有属性的构造器。注意：无参构造器总是会生成。默认为true
		generateGettersSetters = true//是否应该生成属性的getter和setter。默认为true
)
public class Note {
	@Id(autoincrement = false) //必须 Long 或 long 类型的，SQLite 上的自增长会引入额外的资源使用，通常可以避免使用
	private Long id;
	
	@NotNull//在保存到数据库中之前需要保证此值不为null，否则会报 IllegalArgumentException ，并直接崩溃
	private String text;
	
	@Transient //表明此字段不存储到数据库中，用于不需要持久化的字段，比如临时状态
	private String comment;
	
	@Property(nameInDb = "TIME") //为该属性在数据库中映射的字段名设置一个非默认的名称
	private Date date;
	
	@Convert(converter = NoteTypeConverter.class, columnType = String.class) //自定义参数类型
	// converter 为类型转换类，columnType 为自定义的类型在数据库中存储的类型
	private NoteType type; //在保存到数据库时会将自定义的类型 NoteType 通过 NoteTypeConverter 转换为 String 类型。反之亦然
	
	/**
	 * Used to resolve relations
	 */
	@Generated(hash = 2040040024)
	private transient DaoSession daoSession;
	
	/**
	 * Used for active entity operations.
	 */
	@Generated(hash = 363862535)
	private transient NoteDao myDao;
	
	private Note(Builder builder) {
		setId(builder.id);
		setText(builder.text);
		setComment(builder.comment);
		setDate(builder.date);
		setType(builder.type);
	}
	
	@Generated(hash = 2139673067)
	public Note(Long id, @NotNull String text, Date date, NoteType type) {
		this.id = id;
		this.text = text;
		this.date = date;
		this.type = type;
	}
	
	@Generated(hash = 1272611929)
	public Note() {
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public NoteType getType() {
		return this.type;
	}
	
	public void setType(NoteType type) {
		this.type = type;
	}
	
	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
	 * Entity must attached to an entity context.
	 */
	@Generated(hash = 128553479)
	public void delete() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.delete(this);
	}
	
	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
	 * Entity must attached to an entity context.
	 */
	@Generated(hash = 1942392019)
	public void refresh() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.refresh(this);
	}
	
	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
	 * Entity must attached to an entity context.
	 */
	@Generated(hash = 713229351)
	public void update() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.update(this);
	}
	
	/** called by internal mechanisms, do not call yourself. */
	@Generated(hash = 799086675)
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getNoteDao() : null;
	}

	public static final class Builder {
		private Long id;
		private String text;
		private String comment;
		private Date date;
		private NoteType type;
		
		private Builder() {
		}
		
		public Builder id(Long val) {
			id = val;
			return this;
		}
		
		public Builder text(String val) {
			text = val;
			return this;
		}
		
		public Builder comment(String val) {
			comment = val;
			return this;
		}
		
		public Builder date(Date val) {
			date = val;
			return this;
		}
		
		public Builder type(NoteType val) {
			type = val;
			return this;
		}
		
		public Note build() {
			return new Note(this);
		}
	}
}