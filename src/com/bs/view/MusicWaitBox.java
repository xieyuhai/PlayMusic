package com.bs.view;

import com.bs.rockingmusic1.R;
import com.bs.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicWaitBox extends FrameLayout {
	public MusicWaitBox(Context context) {
		this(context, null);
	}

	public MusicWaitBox(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public MusicWaitBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private TextView messageTextView;
	private ImageView imageView;
	private Animation animation;

	private void initialize() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.music_wait_box, this);

		messageTextView = (TextView) findViewById(R.id.messageTextView);
		imageView = (ImageView) findViewById(R.id.imageview);

		animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		animation.setInterpolator(linearInterpolator);
	}

	public void setMessage(String message) {
		messageTextView.setText(message);
	}

	public void setTextColor(int color) {
		messageTextView.setTextColor(color);
	}

	public void startAnimation() {
		imageView.startAnimation(animation);
	}

	public void stopAnimation() {
		// if (((Activity) getContext()).isTaskRoot()) {
		removeWait();
		// }
		imageView.clearAnimation();
	}

	/**
	 * 移除
	 */
	public void removeWait() {
		ViewGroup group = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView()
				.findViewById(android.R.id.content);
		if (group.getChildAt(group.getChildCount() - 1) instanceof MusicWaitBox) {
			group.removeViewAt(group.getChildCount() - 1);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// if (((Activity) getContext()).isTaskRoot()) {
		// removeWait();
		// return false;
		// }
		return true;
	}

}
