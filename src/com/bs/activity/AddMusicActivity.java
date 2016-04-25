package com.bs.activity;

import java.util.ArrayList;
import java.util.List;

import com.bs.adapter.MusicAdapter;
import com.bs.db.DBService;
import com.bs.entity.MusicBean;
import com.bs.listener.DialogListener;
import com.bs.rockingmusic1.R;
import com.bs.util.Utils;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;

/**
 * 展示本地音乐添加音乐
 * 
 * @author xieyuhai
 *
 */
public class AddMusicActivity extends ListActivity {
	private List<MusicBean> list = new ArrayList<MusicBean>();
	private MusicAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_music);
		init();
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				"_data like '%.mp3' or _data like '%.mp4' or _data like '%.3gp'", null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		while (cursor.moveToNext()) {
			// 歌曲ID：MediaStore.Audio.Media._ID
			String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			// 歌曲的名称 ：MediaStore.Audio.Media.TITLE
			String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			// 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
			String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			// 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
			String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
			// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
			String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			// 歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
			int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			// 歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
			long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			// 获取专辑图片
			int album_id = cursor.getInt(cursor.getColumnIndexOrThrow("album_id"));
			//
			String str = "content://media/external/audio/media/" + id + "/albumart";// 根据这个去查询专辑图片

			MusicBean bean = new MusicBean();
			// bean.id = id;
			bean.title = tilte;
			bean.album = album;
			bean.artist = artist;
			bean.url = url;
			bean.duration = duration;
			bean.size = size;
			bean.musicID = id;
			bean.albumUrl = str;
			bean.album_id = album_id + "";
			list.add(bean);
		}
		adapter = new MusicAdapter(this, list);
		getListView().setAdapter(adapter);

		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final MusicBean bean = list.get(position);
				Utils.showDialog(AddMusicActivity.this, R.layout.dialog_layout, null, new DialogListener() {
					@Override
					public void sure() {
						List<MusicBean> selectAllOrSingle = DBService.getDBInstance(getApplicationContext())
								.selectAllOrSingle(bean.musicID);
						if (selectAllOrSingle == null || selectAllOrSingle.size() == 0) {
							DBService.getDBInstance(getApplicationContext()).insertOrUpdate(bean);
							Utils.showToast(AddMusicActivity.this, "添加成功");
						} else {
							Utils.showToast(AddMusicActivity.this, "已添加过");
						}
					}

					@Override
					public void cancel() {
						Utils.showToast(AddMusicActivity.this, "cancel");
					}
				});
			}
		});
	}
}
