package com.bs.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.provider.MediaStore;

public class MusicCursorLoader extends CursorLoader {

	private Context context;

	public MusicCursorLoader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Cursor loadInBackground() {
		return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				"_data like '%.mp3' or _data like '%.mp4' or _data like '%.3gp'", null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	}

}
