package com.bs.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;

public class BackgroundService extends Service implements MediaPlayer.OnCompletionListener {
	private Vibrator mVibrator;
	private MediaPlayer player;
	private int position = 0;// 当前的歌曲

	private final IBinder binder = new AudioBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	/**
	 * 当Audio播放完的时候触发该动作
	 */
	@Override
	public void onCompletion(MediaPlayer player) {
		// TODO Auto-generated method stub
		stopSelf();// 结束了，则结束Service
	}

	// 在这里我们需要实例化MediaPlayer对象
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
		mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
		// player.setOnCompletionListener(this);
	}

	/**
	 * 该方法在SDK2.0才开始有的，替代原来的onStart方法
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!player.isPlaying()) {
			player.start();
		}
		return START_STICKY;
	}

	public void onDestroy() {
		// super.onDestroy();
		if (player != null && player.isPlaying()) {
			player.stop();
			player.release();
			player = null;
		}

	}

	// 为了和Activity交互，我们需要定义一个Binder对象
	public class AudioBinder extends Binder {
		// 返回Service对象
		public BackgroundService getService() {
			return BackgroundService.this;
		}
	}

	/**
	 * 播放音乐 int position,
	 * 
	 * @param position
	 */
	public void startMusic(String url) {
		try {
			player.reset();
			player.setDataSource(url);
			player.prepare();
			player.setLooping(true);
			player.start();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 定义震动
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
	}

	/**
	 * 获取进度
	 * 
	 * @return
	 */
	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	/**
	 * 指定播放进度
	 * 
	 * @param progress
	 */
	public void seekTo(int progress) {
		player.seekTo(progress);
	}

	/**
	 * 开始播放
	 */
	public void startMusic1() {
		if (player != null && !player.isPlaying()) {
			player.start();
		}
	}

	/**
	 * 停止播放
	 */
	public void stopMusic() {
		if (player != null && player.isPlaying()) {
			player.stop();
		}
	}

	/**
	 * 暂停播放
	 */
	public void pauseMusic() {
		if (player != null && player.isPlaying()) {
			player.pause();
		}
	}

	/**
	 * 释放
	 */
	public void releaseMusic() {
		if (player != null) {
			player.release();
			player = null;
		}
	}

	/**
	 * 取消震动
	 */
	public void cancel() {
		mVibrator.cancel();
	}

	/**
	 * 后退播放进度
	 */
	public void haveFun() {
		if (player.isPlaying() && player.getCurrentPosition() > 2500) {
			player.seekTo(player.getCurrentPosition() - 2500);
		}
	}
}
