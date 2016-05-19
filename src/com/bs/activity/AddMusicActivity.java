package com.bs.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bs.adapter.MusicAdapter;
import com.bs.db.DBService;
import com.bs.entity.MusicBean;
import com.bs.listener.DialogListener;
import com.bs.loader.MusicCursorLoader;
import com.bs.rockingmusic1.R;
import com.bs.util.Utils;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * 展示本地音乐添加音乐
 * 
 * @author xieyuhai
 *
 */
public class AddMusicActivity extends ListActivity implements LoaderCallbacks<Cursor> {

	private volatile static List<MusicBean> list = new ArrayList<MusicBean>();
	private MusicAdapter adapter;

	// private Handler handler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// if (msg.what == 1) {
	// // executor.shutdown();
	// Utils.stopMusicWaitBox();
	// if (list == null || list.isEmpty()) {
	// Utils.showToast(AddMusicActivity.this, "暂无歌曲");
	// return;
	// }
	// executor.submit(new Runnable() {
	// @Override
	// public void run() {
	// adapter = new MusicAdapter(AddMusicActivity.this, list);
	// handlerUI.sendEmptyMessage(0x222);
	// }
	// });
	//
	// // handler.post(new Runnable() {
	// // @Override
	// // public void run() {
	// // adapter = new MusicAdapter(AddMusicActivity.this, list);
	// // handlerUI.sendEmptyMessage(0x222);
	// // }
	// // });
	//
	// // ((ListActivity) context).setListAdapter(adapter);
	// }
	// };
	// };
	//
	// private Handler handlerUI = new Handler() {
	// public void handleMessage(Message msg) {
	// if (msg.what == 0x222) {
	// adapter.notifyDataSetChanged();
	// System.out.println("current4:" + System.currentTimeMillis());
	// }
	// };
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_add_music);

		getListView().setEmptyView(findViewById(R.id.emptyTextView));
		// handler = new MyHandle(this, adapter);
		Utils.showMusicWaitBox(this);
		getLoaderManager().initLoader(0, null, this);

		// /**
		// * 获取音乐列表
		// */
		//
		// System.out.println("current1:" + System.currentTimeMillis());
		// executor.submit(new Runnable() {
		// @Override
		// public void run() {
		// Utils.showMusicWaitBox(AddMusicActivity.this);
		// initData();
		// initListView();
		// Message msg = handler.obtainMessage(1);
		// handler.sendMessage(msg);
		//
		// System.out.println("current3:" + System.currentTimeMillis());
		// }
		// });
		// System.out.println("current2:" + System.currentTimeMillis());
		Log.i("TAG", "onCreate");
		// initData();
		// initListView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("TAG", "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// getLoaderManager().restartLoader(0, null, this);
		Log.i("TAG", "onResume");

	}

	/**
	 * 初始化ListView数据
	 */
	private void initListView() {
		adapter = new MusicAdapter(this, list);
		getListView().setAdapter(adapter);
		// setListAdapter(adapter);
	}

	/**
	 * 初始化数据
	 */
	private void initData(Cursor cursor) {
		if (cursor == null) {
			cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
					"_data like '%.mp3' or _data like '%.mp4' or _data like '%.3gp'", null,
					MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		}

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
		if (cursor != null) {
			cursor.close();
		}

		// initListView();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//
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

	@Override
	public Loader<Cursor> onCreateLoader(int longId, Bundle args) {
		Log.i("TAG", "onCreateLoader");
		// return new CursorLoader(this,
		// MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
		// "_data like '%.mp3' or _data like '%.mp4' or _data like '%.3gp'",
		// null,
		// MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		
		return new MusicCursorLoader(this);
	}

	private boolean isFinish = false;

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (!isFinish) {
			Utils.stopMusicWaitBox();
			// rootContainer.removeView(progressBar);
			isFinish = true;
			Log.i("TAG", "onLoadFinished");
			initData(cursor);
			initListView();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.i("TAG", "onLoaderReset");
		list.clear();
		initListView();

		getLoaderManager().destroyLoader(0);
	}

	@Override
	protected void onDestroy() {
		// adapter = new MusicAdapter(this, new ArrayList<MusicBean>());
		// getListView().setAdapter(adapter);
		Log.i("TAG", "onDestroy Cancel Success!");
		super.onDestroy();
	}
}
