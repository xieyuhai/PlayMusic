package com.bs.adapter;

import java.util.List;

import com.bs.entity.MusicBean;
import com.bs.rockingmusic1.R;
import com.bs.util.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<MusicBean> beans;
	private Context context;

	public MusicAdapter(Context context, List<MusicBean> beans) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.beans = beans;
	}

	@Override
	public int getCount() {
		return beans.size();
	}

	@Override
	public Object getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.music_item, parent, false);
			holder.typeFace = Typeface.createFromAsset(context.getAssets(), "MNJLX.TTF");
			holder.zhuanjiTextView = (TextView) convertView.findViewById(R.id.zhuanjiTextView);
			holder.gequDaxiaoTextView = (TextView) convertView.findViewById(R.id.gequDaxiaoTextView);
			holder.geshouMingTextView = (TextView) convertView.findViewById(R.id.geshouMingTextView);
			holder.shichangTextView = (TextView) convertView.findViewById(R.id.shichangTextView);
			holder.tupianImageView = (ImageView) convertView.findViewById(R.id.tupianImageView);

			holder.zhuanjiTextView.setTypeface(holder.typeFace);
			holder.gequDaxiaoTextView.setTypeface(holder.typeFace);
			holder.geshouMingTextView.setTypeface(holder.typeFace);
			holder.shichangTextView.setTypeface(holder.typeFace);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MusicBean bean = beans.get(position);

		holder.zhuanjiTextView.setText(bean.album + "");
		holder.gequDaxiaoTextView.setText(bean.size + "");
		holder.geshouMingTextView.setText(bean.artist + "");
		holder.shichangTextView.setText(bean.duration + "");
		// if (bean.albumUrl != null) {
		// holder.tupianImageView.setImageBitmap(Utils.getBitmap(context,
		// bean.albumUrl));
		// }

		if (bean.musicID != null && bean.album_id != null) {
			Bitmap bitmap = Utils.getArtwork(context, Long.parseLong(bean.musicID), Long.parseLong(bean.album_id), true,
					true);
			holder.tupianImageView.setImageBitmap(bitmap);
		} else {
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
			holder.tupianImageView.setImageBitmap(bitmap);
		}

		return convertView;
	}

	public class ViewHolder {
		public Typeface typeFace;
		public TextView zhuanjiTextView;
		public TextView gequDaxiaoTextView;
		public TextView geshouMingTextView;
		public TextView shichangTextView;
		public ImageView tupianImageView;
	}

}
