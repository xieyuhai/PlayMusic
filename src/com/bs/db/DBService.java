package com.bs.db;

import java.util.ArrayList;
import java.util.List;

import com.bs.entity.MusicBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DBService {
	private MySqliteOpenHelper helper;

	public DBService(Context context) {
		helper = new MySqliteOpenHelper(context);
	}

	private static DBService dbService;

	public static synchronized DBService getDBInstance(Context c) {

		if (dbService == null) {
			dbService = new DBService(c);
		}
		return dbService;
	}

	/**
	 * 查询全部或单条
	 */
	public List<MusicBean> selectAllOrSingle(String musicID) {
		List<MusicBean> list = new ArrayList<MusicBean>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String buffer = "";
		if (TextUtils.isEmpty(musicID)) {
			buffer = null;
		} else {
			buffer = "musicID=" + musicID;
		}
		Cursor cursor = db.query(MySqliteOpenHelper.TABLE_NAME, null, buffer, null, null, null, null);
		while (cursor.moveToNext()) {
			// 歌曲ID：MediaStore.Audio.Media._ID
			String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
			// 歌曲的名称 ：MediaStore.Audio.Media.TITLE
			String tilte = cursor.getString(cursor.getColumnIndexOrThrow("title"));
			// 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
			String album = cursor.getString(cursor.getColumnIndexOrThrow("album"));
			// 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
			String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
			// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
			String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
			// 歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
			int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
			// 歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
			long size = cursor.getLong(cursor.getColumnIndexOrThrow("size"));

			String musicID2 = cursor.getString(cursor.getColumnIndexOrThrow("musicID"));

			String albumUrl = cursor.getString(cursor.getColumnIndexOrThrow("albumUrl"));

			// 获取专辑图片
			int album_id = cursor.getInt(cursor.getColumnIndexOrThrow("album_id"));

			MusicBean bean = new MusicBean();
			bean.id = id;
			bean.title = tilte;
			bean.album = album;
			bean.artist = artist;
			bean.url = url;
			bean.duration = duration;
			bean.size = size;
			bean.musicID = musicID2;
			bean.albumUrl = albumUrl;
			//
			bean.album_id = album_id + "";
			list.add(bean);
		}
		return list;
	}

	/**
	 * 当bean中id为null执行添加否则执行修改
	 * 
	 * @param bean
	 */
	public void insertOrUpdate(MusicBean bean) {
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("title", bean.title);
		values.put("album", bean.album);
		values.put("artist", bean.artist);
		values.put("url", bean.url);
		values.put("duration", bean.duration);
		values.put("size", bean.size);
		values.put("musicID", bean.musicID);
		//
		values.put("album_id", bean.album_id);
		if (TextUtils.isEmpty(bean.id)) {
			db.insert(MySqliteOpenHelper.TABLE_NAME, null, values);
		} else {
			db.update(MySqliteOpenHelper.TABLE_NAME, values, "musicID=" + bean.musicID, null);
		}
		db.close();
	}

	/**
	 * 删除
	 * 
	 * @param deleteID
	 */
	public void delete(String deleteID) {
		SQLiteDatabase db = helper.getWritableDatabase();
		StringBuffer music = new StringBuffer();
		music.append("DELETE FROM " + MySqliteOpenHelper.TABLE_NAME + " WHERE musicID= " + deleteID);
		db.execSQL(music.toString());

		db.close();
	}

}
