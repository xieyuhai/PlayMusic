package com.bs.activity;

import com.bs.rockingmusic1.R;
import com.bs.rockingmusic1.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 音乐列表
 * 
 * @author
 *
 */
public class InitActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		Handler handler = new Handler();
		Runnable runable = new Runnable() {
			@Override
			public void run() {
				/**
				 * 跳转到MainActivity，音乐列表
				 */
				Intent intent = new Intent();
				intent.setClass(InitActivity.this, com.bs.activity.MainActivity.class);
				startActivity(intent);
				finish();
			}
		};
		/**
		 * 延时3秒执行
		 */
		handler.postDelayed(runable, 500);
	}
}
