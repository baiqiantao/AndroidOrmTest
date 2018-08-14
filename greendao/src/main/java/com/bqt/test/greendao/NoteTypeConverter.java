package com.bqt.test.greendao;

import org.greenrobot.greendao.converter.PropertyConverter;

public class NoteTypeConverter implements PropertyConverter<NoteType, String> {
	public static final String TYPE_TEXT = "文本";
	public static final String TYPE_PICTURE = "图片";
	public static final String TYPE_UNKNOWN = "未知格式";
	
	@Override
	public NoteType convertToEntityProperty(String databaseValue) { //将 数据库中存储的String类型 转换为 自定义的NoteType类型
		switch (databaseValue) {
			case TYPE_TEXT:
				return NoteType.TEXT;
			case TYPE_PICTURE:
				return NoteType.PICTURE;
			default: //不要忘记正确处理空值
				return NoteType.UNKNOWN;
		}
	}
	
	@Override
	public String convertToDatabaseValue(NoteType entityProperty) { //将 自定义的NoteType类型 转换为在数据库中存储的String类型
		switch (entityProperty) {
			case TEXT:
				return TYPE_TEXT;
			case PICTURE:
				return TYPE_PICTURE;
			default:
				return TYPE_UNKNOWN;
		}
	}
}
