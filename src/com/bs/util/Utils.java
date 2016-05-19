package com.bs.util;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.bs.listener.DialogListener;
import com.bs.rockingmusic1.R;
import com.bs.view.MusicWaitBox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Utils {
	/**
	 * 加载进度
	 * 
	 * @param context
	 */
	private static MusicWaitBox musicWaitBox = null;

	/**
	 * 创建加载进度
	 * 
	 * @param context
	 */
	public static void showMusicWaitBox(Activity context) {
		ViewGroup rootContainer = (ViewGroup) context.getWindow().getDecorView().findViewById(android.R.id.content);
		rootContainer.setBackgroundColor(0x7f000000);
		musicWaitBox = new MusicWaitBox(context);
		musicWaitBox.setMessage("加载中。。。。");
		rootContainer.addView(musicWaitBox);
		ViewGroup.LayoutParams params = musicWaitBox.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		musicWaitBox.setLayoutParams(params);
		musicWaitBox.startAnimation();
	}

	/**
	 * 停止进度
	 */
	public static void stopMusicWaitBox() {
		if (musicWaitBox != null) {
			musicWaitBox.stopAnimation();
			musicWaitBox = null;
		}
	}

	/**
	 * 歌曲时长换算为分钟
	 * 
	 * @param duration
	 * @return
	 */
	public static String getDuration(int oldDuration) {
		int duration = oldDuration / 1000;
		if (duration == 0) {
			return oldDuration + "MS";
		} else if (duration < 60) {
			return duration + "S";
		} else if (duration > 60 && duration / 60 < 60) {
			return duration / 60 + "M";
		} else if (duration / 60 > 60) {
			return duration / 60 / 60 + "H";
		}
		return "";
	}

	/**
	 * 
	 * @param oldSize
	 * @return
	 */
	public static String getFileSize(long oldSize) {
		long size = oldSize / 1024l;
		if (size == 0l) {
			return size + "BYTE";
		} else if (size < 1024l) {
			return size + "KB";
		} else if (size > 1024l && size / 1024l < 1024l) {
			return size / 1024l + "MB";
		} else if (size / 1024l > 1024l) {
			return size / 1024l / 1024l + "GB";
		}
		return "";
	}

	/**
	 * 显示对话框
	 * 
	 * @param context
	 * @param layoutId
	 * @param msg
	 */
	public static void showDialog(Context context, int layoutId, String msg, final DialogListener listener) {
		View view = LayoutInflater.from(context).inflate(layoutId, null);
		final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(false).create();
		dialog.show();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		params.width = ScreenUtil.get(context).widthPixel * 4 / 5;
		dialog.addContentView(view, params);

		if (!TextUtils.isEmpty(msg)) {
			TextView xiaoxiTextView = (TextView) view.findViewById(R.id.messageTextView);
			xiaoxiTextView.setText(msg);
		}

		(view.findViewById(R.id.sureTextView)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				listener.sure();
			}
		});
		(view.findViewById(R.id.cancelTextView)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				listener.cancel();
			}
		});
	}

	/**
	 * 显示toast消息
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToast(Context context, String msg) {
		Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 获取专辑图片
	 * 
	 * @param str
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String str) {
		Uri uri = Uri.parse(str);
		ParcelFileDescriptor pfd = null;
		try {
			pfd = context.getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
		} catch (FileNotFoundException e) {
		}
		Bitmap bm;
		if (pfd != null) {
			FileDescriptor fd = pfd.getFileDescriptor();
			bm = BitmapFactory.decodeFileDescriptor(fd);

			BitmapFactory.Options options = new Options();
			//
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 我们的目标是在800pixel的画面上显示
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 100;
			// 我们得到了缩放的比例，现在开始正式读入Bitmap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 根据options参数，减少所需要的内存
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);

			return bm;
		}
		return null;
	}

	/*********
	 * ----------http://blog.csdn.net/wwj_748/article/details/9237561---------
	 */

	// 获取专辑封面的Uri
	private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

	/**
	 * 获取默认专辑图片
	 * 
	 * @param context
	 * @return
	 */
	public static Bitmap getDefaultArtwork(Context context, boolean small) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		if (small) { // 返回小图片
			return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.ic_launcher), null,
					opts);
		}
		return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.ic_launcher), null, opts);
	}

	/**
	 * 从文件当中获取专辑封面位图
	 * 
	 * @param context
	 * @param songid
	 * @param albumid
	 * @return
	 */
	private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
		Bitmap bm = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException("Must specify an album or a song id");
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			FileDescriptor fd = null;
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			} else {
				Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			}
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 我们的目标是在800pixel的画面上显示
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 100;
			// 我们得到了缩放的比例，现在开始正式读入Bitmap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 根据options参数，减少所需要的内存
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			Log.i("TAG", e.getMessage() + "");
		}
		return bm;
	}

	/**
	 * 获取专辑封面位图对象
	 * 
	 * @param context
	 * @param song_id
	 * @param album_id
	 * @param allowdefalut
	 * @return
	 */
	public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small) {
		if (album_id < 0) {
			if (song_id < 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefalut) {
				return getDefaultArtwork(context, small);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				// 先制定原始大小
				options.inSampleSize = 1;
				// 只进行大小判断
				options.inJustDecodeBounds = true;
				// 调用此方法得到options得到图片的大小
				BitmapFactory.decodeStream(in, null, options);
				/** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
				/** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
				if (small) {
					options.inSampleSize = computeSampleSize(options, 40);
				} else {
					options.inSampleSize = computeSampleSize(options, 600);
				}
				// 我们得到了缩放比例，现在开始正式读入Bitmap数据
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, options);
			} catch (FileNotFoundException e) {
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefalut) {
							return getDefaultArtwork(context, small);
						}
					}
				} else if (allowdefalut) {
					bm = getDefaultArtwork(context, small);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 对图片进行合适的缩放
	 * 
	 * @param options
	 * @param target
	 * @return
	 */
	public static int computeSampleSize(Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0) {
			return 1;
		}
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target) {
				candidate -= 1;
			}
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target) {
				candidate -= 1;
			}
		}
		return candidate;
	}

	/********* --------------------- */
}
