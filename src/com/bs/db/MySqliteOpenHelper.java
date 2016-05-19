package com.bs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqliteOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "music.db";
	public static final int VERSION_CODE = 1;
	public static final String TABLE_NAME = "mymusic";

	public MySqliteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION_CODE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,musicID TEXT,title TEXT,album TEXT, artist TEXT,url TEXT,duration INTEGER,size INTEGER,albumUrl TEXT,album_id TEXT) ";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
		if (oldVersion > newVersion) {
			// 版本回退
		} else if (oldVersion < newVersion) {
			// 版本更新
		}
	}
}
