package com.bs.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtil {

	public double density;

	public int widthDp;

	public int heightDp;

	public int widthPixel;

	public int heightPixel;

	private static Context context;

	private ScreenUtil(Context c) {
		obtainInfo(c);
	}

	private void obtainInfo(Context context) {

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		density = displayMetrics.density;
		widthPixel = displayMetrics.widthPixels;
		heightPixel = displayMetrics.heightPixels;
		widthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);
		heightDp = (int) (displayMetrics.heightPixels / displayMetrics.density);
	}

	private static ScreenUtil instance;

	public static synchronized ScreenUtil get(Context c) {
		if (instance == null) {
			context = c;
			instance = new ScreenUtil(context);
		}
		return instance;
	}
}
