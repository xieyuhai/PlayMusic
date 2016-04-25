package com.bs.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.bs.adapter.MusicAdapter;
import com.bs.db.DBService;
import com.bs.entity.MusicBean;
import com.bs.listener.DialogListener;
import com.bs.listener.SongChangeListener;
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
import android.view.KeyEvent;
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
	private volatile static List<MusicBean> list = new ArrayList<MusicBean>();
	//
	public static ImageView pre;// 上一首
	public static ImageView play;// 播放
	public static ImageView stop;// 停止
	public static ImageView next;// 下一首
	public static SeekBar seekbar;// 进度条
	private int position = 0;// 当前音乐

	private com.bs.listener.SongChangeListener mSongChangeListener;// 传感器

	static class MyHandle extends Handler {

		private Context context;
		private MusicAdapter adapter;

		public MyHandle(Context context, List<MusicBean> musicBeans, MusicAdapter adapter) {
			this.context = context;
			this.adapter = adapter;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				list = (List<MusicBean>) msg.obj;
				adapter = new MusicAdapter(context, list);
				((ListActivity) context).getListView().setAdapter(adapter);
			}
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (service != null) {
				seekbar.setProgress(service.getCurrentPosition());
				handler.postDelayed(runnable, 100);
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
		if (service != null) {
			service.stopMusic();
			service.releaseMusic();
			service.unbindService(conn);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//
		Intent intent = new Intent();
		intent.setClass(this, BackgroundService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		//
		handler = new MyHandle(this, list, adapter);
		handler.post(runnable);
		init();
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

		// mVibrator = (Vibrator)
		// getApplication().getSystemService(VIBRATOR_SERVICE);
		title = (TextView) findViewById(R.id.title);
		pre = (ImageView) findViewById(R.id.pre);
		next = (ImageView) findViewById(R.id.next);
		stop = (ImageView) findViewById(R.id.stop);
		play = (ImageView) findViewById(R.id.play);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		seekbar.setMax(100);
		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		stop.setOnClickListener(this);
		play.setOnClickListener(this);
		//
		getListView().setAdapter(adapter);
		adapter = new MusicAdapter(this, list);
		getListView().setAdapter(adapter);
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, AddMusicActivity.class));
			}
		});
		mSongChangeListener = new SongChangeListener(this);
		/**
		 * 传感器监听
		 */
		mSongChangeListener.setOnShakeListener(new SongChangeListener.OnSongChangeListener() {
			@Override
			public void onSongChange() {
				mSongChangeListener.stop();
				if (list.size() != 0) {
					position = list.size() == 1 ? 0 : new Random().nextInt(list.size() - 1);
					// startMusic(position); // 开始播放
					if (service != null) {
						service.startMusic(list.get(position).url);
					}
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
		new Thread(new Runnable() {
			public void run() {
				Message msg = handler.obtainMessage(1);
				msg.obj = DBService.getDBInstance(getApplicationContext()).selectAllOrSingle(null);
				handler.sendMessage(msg);
			}
		}).start();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Utils.showDialog(MainActivity.this, R.layout.dialog_layout, getString(R.string.exit), new DialogListener() {
				@Override
				public void sure() {
					// stopMusic();
					// releaseMusic();
					if (service != null) {
						service.stopMusic();
						service.releaseMusic();
					}
					handler.removeCallbacks(runnable);
					MainActivity.this.finish();
				}

				@Override
				public void cancel() {
					Utils.showToast(MainActivity.this, "cancel");
				}
			});

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pre:
			--position;
			if (list != null && list.size() > 0 && position < list.size() && position != -1) {
				// startMusic(position);
				if (service != null) {
					service.startMusic(list.get(position).url);
				}
				return;
			}
			if (position == -1 && list.size() == 0) {
				Utils.showToast(this, getString(R.string.add_music));
			} else {
				Utils.showToast(this, getString(R.string.first_musix));
			}
			break;
		case R.id.next:
			++position;
			if (list != null && list.size() > 0 && position < list.size()) {
				// startMusic(position);
				if (service != null) {
					service.startMusic(list.get(position).url);
				}
				return;
			}
			if (list.size() == 0) {
				Utils.showToast(this, getString(R.string.add_music));
			} else {
				Utils.showToast(this, getString(R.string.end_music));
			}
			break;
		case R.id.stop:
			// stopMusic();
			if (service != null) {
				service.pauseMusic();
			}
			// if (player != null && player.isPlaying()) {
			// player.pause();
			// }
			break;
		case R.id.play:
			if (list != null && list.size() > 0 && position < list.size()) {
				// startMusic(position);
				if (service != null) {
					service.startMusic1();
				}
				return;
			}
			if (list.size() == 0) {
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
		// startMusic(position);
		if (service != null) {
			service.startMusic(list.get(position).url);
		}
	}

	/**
	 * 跳转activity
	 */
	@Override
	protected void onStop() {
		mSongChangeListener.stop();
		// if (service != null) {
		// service.stopMusic();
		// }
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
		super.onPause();
	}
}
