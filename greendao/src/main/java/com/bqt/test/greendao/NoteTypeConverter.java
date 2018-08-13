package com.bqt.test.greendao;

import org.greenrobot.greendao.converter.PropertyConverter;

public class NoteTypeConverter implements PropertyConverter<NoteType, String> {
	public static final String TYPE_TEXT = "文本";
	public static final String TYPE_PICTURE = "图片";
	public static final String TYPE_UNKNOWN = "未知格式";
	
	@Override
	public NoteType convertToEntityProperty(String databaseValue) {
		switch (databaseValue) {
			case TYPE_TEXT:
				return NoteType.TEXT;
			case TYPE_PICTURE:
				return NoteType.PICTURE;
			default:
				return NoteType.UNKNOWN;
		}
	}
	
	@Override
	public String convertToDatabaseValue(NoteType entityProperty) {
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
