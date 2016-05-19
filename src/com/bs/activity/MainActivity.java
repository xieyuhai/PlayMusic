package com.bs.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bs.adapter.MusicAdapter;
import com.bs.db.DBService;
import com.bs.entity.MusicBean;
import com.bs.listener.DialogListener;
import com.bs.listener.onSongChangeListener;
import com.bs.rockingmusic1.R;
import com.bs.service.BackgroundService;
import com.bs.util.Utils;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends ListActivity implements OnClickListener {

	private BackgroundService service;
	private boolean bind;

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((BackgroundService.AudioBinder) binder).getService();
		}
	};

	// ／／

	private TextView title;
	private MusicAdapter adapter;
	private MyHandle handler;
	private volatile List<MusicBean> list = new ArrayList<MusicBean>();
	//
	public static ImageView pre;// 上一首
	public static ImageView play;// 播放
	public static ImageView stop;// 停止
	public static ImageView next;// 下一首
	public SeekBar seekbar;// 进度条
	private int position = 0;// 当前音乐

	private com.bs.listener.onSongChangeListener mSongChangeListener;// 传感器
	//
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	class MyHandle extends Handler {

		private Context context;
		private MusicAdapter adapter;

		public MyHandle(Context context, MusicAdapter adapter) {
			this.context = context;
			this.adapter = adapter;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Utils.stopMusicWaitBox();
				// list.clear();
				if (list == null || list.isEmpty()) {
					Utils.showToast(context, "暂无歌曲");
					return;
				}
				// list = (List<MusicBean>) msg.obj;
				adapter = new MusicAdapter(context, list);
				// adapter.notifyDataSetChanged();
				setListAdapter(adapter);
			}
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Log.i("TAG", "runnable " + service);
			if (service != null && service.isPlaying()) {
				seekbar.setProgress(service.getCurrentPosition());
				handler.postDelayed(runnable, 1000);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getListView().setEmptyView(findViewById(R.id.emptyTextView));
		init();
		//
		Intent intent = new Intent();
		intent.setClass(this, BackgroundService.class);
		bind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
		//
		handler = new MyHandle(this, adapter);

		SeekBarChange();
	}

	private void SeekBarChange() {
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					service.seekTo(progress);
				}
			}
		});
	}

	private void init() {
		title = (TextView) findViewById(R.id.title);
		pre = (ImageView) findViewById(R.id.pre);
		next = (ImageView) findViewById(R.id.next);
		stop = (ImageView) findViewById(R.id.stop);
		play = (ImageView) findViewById(R.id.play);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		stop.setOnClickListener(this);
		play.setOnClickListener(this);
		//
		// adapter = new MusicAdapter(this, list);
		// setListAdapter(adapter);
		title.setOnClickListener(this);
		mSongChangeListener = new onSongChangeListener(this);
		/**
		 * 传感器监听
		 */
		mSongChangeListener.setOnShakeListener(new onSongChangeListener.OnSongChangeListener() {
			@Override
			public void onSongChange() {
				mSongChangeListener.stop();
				if (list.isEmpty()) {
					position = list.size() == 1 ? 0 : new Random().nextInt(list.size() - 1);
					// startMusic(position); // 开始播放
					startMusic();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (service != null) {
								service.cancel();
							}
							mSongChangeListener.start();
						}
					}, 3000);
				}
			}
		});
		/**
		 * 长按删除事件
		 */
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final MusicBean bean = list.get(position);
				Utils.showDialog(MainActivity.this, R.layout.dialog_layout, getString(R.string.delete_music),
						new DialogListener() {
					@Override
					public void sure() {
						DBService.getDBInstance(getApplicationContext()).delete(bean.musicID);
						getMusic();
					}

					@Override
					public void cancel() {
						Utils.showToast(MainActivity.this, "cancel");
					}
				});
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSongChangeListener.start();
		getMusic();
	}

	/**
	 * 获取音乐列表
	 */
	public void getMusic() {
		Utils.showMusicWaitBox(MainActivity.this);
		executor.submit(new Runnable() {
			@Override
			public void run() {
				list = DBService.getDBInstance(getApplicationContext()).selectAllOrSingle(null);
				Message msg = handler.obtainMessage(1);
				handler.sendMessage(msg);
			}
		});

		// new Thread(new Runnable() {
		// public void run() {
		//
		// list =
		// DBService.getDBInstance(getApplicationContext()).selectAllOrSingle(null);
		// Message msg = handler.obtainMessage(1);
		// handler.sendMessage(msg);
		// }
		// }).start();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Utils.showDialog(this, R.layout.dialog_layout, getString(R.string.exit), new DialogListener() {
			@Override
			public void sure() {
				handler.removeCallbacks(runnable);
				MainActivity.this.finish();
			}

			@Override
			public void cancel() {
				Utils.showToast(MainActivity.this, "cancel");
			}
		});
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// Utils.showDialog(MainActivity.this, R.layout.dialog_layout,
	// getString(R.string.exit), new DialogListener() {
	// @Override
	// public void sure() {
	// handler.removeCallbacks(runnable);
	// MainActivity.this.finish();
	// }
	//
	// @Override
	// public void cancel() {
	// Utils.showToast(MainActivity.this, "cancel");
	// }
	// });
	//
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title:
			startActivity(new Intent(MainActivity.this, AddMusicActivity.class));
			break;
		case R.id.pre:
			--position;
			if (list != null && !list.isEmpty() && position < list.size() && position != -1) {
				startMusic();
				return;
			}
			if (position == -1 && list.isEmpty()) {
				Utils.showToast(this, getString(R.string.add_music));
			} else {
				Utils.showToast(this, getString(R.string.first_musix));
			}
			break;
		case R.id.next:
			++position;
			if (list != null && !list.isEmpty() && position < list.size()) {
				startMusic();
				return;
			}
			if (list.isEmpty()) {
				Utils.showToast(this, getString(R.string.add_music));
			} else {
				Utils.showToast(this, getString(R.string.end_music));
			}
			break;
		case R.id.stop:
			if (service != null) {
				service.pauseMusic();
				handler.removeCallbacks(runnable);
			}
			break;
		case R.id.play:
			if (list != null && !list.isEmpty() && position < list.size()) {
				if (service != null) {
					service.startMusic1();
					handler.post(runnable);
				}
				return;
			}
			if (list.isEmpty()) {
				Utils.showToast(this, getString(R.string.add_music));
			} else {
				Utils.showToast(this, getString(R.string.end_music));
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		this.position = position;
		startMusic();
	}

	/**
	 * 跳转activity
	 */
	@Override
	protected void onStop() {
		// if (service != null) {
		// service.stopMusic();
		// }
		Log.i("TAG", "onStop1 !");
		super.onStop();
	}

	/**
	 * 覆盖
	 */
	@Override
	protected void onPause() {
		// if (service != null) {
		// service.pauseMusic();
		// }
		Log.i("TAG", "onPause1 !");
		super.onPause();
	}

	/**
	 * 播放音乐
	 */
	public void startMusic() {
		if (service != null) {
			seekbar.setMax(list.get(position).duration);
			service.startMusic(list.get(position).url);
			handler.post(runnable);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		handler.removeCallbacksAndMessages(null);
		if (service != null) {
			service.stopMusic();
			service.releaseMusic();
		}
		if (bind) {
			unbindService(conn);
			bind = false;
		}

		Log.i("TAG", "onDestroy  bind=" + bind);
		mSongChangeListener.stop();
	}
}
