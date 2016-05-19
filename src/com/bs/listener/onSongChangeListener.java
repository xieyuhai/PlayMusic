package com.bs.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

public class onSongChangeListener implements SensorEventListener {
	private static final int SPEED_SHRESHOLD = 3000;
	private static final int UPTATE_INTERVAL_TIME = 100;// 70;
	private SensorManager sensorManager;
	private Sensor sensor;
	private OnSongChangeListener onShakeListener;
	private Context mContext;
	private float lastX;
	private float lastY;
	private float lastZ;
	private long lastUpdateTime;

	public onSongChangeListener(Context c) {
		mContext = c;
		start();
	}

	public void start() {
		sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		if (sensor != null) {
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void stop() {
		sensorManager.unregisterListener(this);
	}

	public void setOnShakeListener(OnSongChangeListener listener) {
		onShakeListener = listener;
	}

	public void onSensorChanged(SensorEvent event) {
		// int sensorType = event.sensor.getType();
		// float[] values = event.values;
		// if (sensorType == Sensor.TYPE_ACCELEROMETER) {
		// if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 ||
		// Math.abs(values[2]) > 14)) {
		// onShakeListener.onSongChange();
		// }
		// }

		long currentUpdateTime = System.currentTimeMillis();
		long timeInterval = currentUpdateTime - lastUpdateTime;
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;
		lastUpdateTime = currentUpdateTime;

		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		lastX = x;
		lastY = y;
		lastZ = z;
		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;

		// Log.e("TAG", "x=" + x + "y=" + y + "z=" + z + "lastX=" + lastX +
		// "lastY=" + lastY + "lastZ=" + lastZ + "speed="
		// + speed);
		if (speed >= SPEED_SHRESHOLD) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					onShakeListener.onSongChange();
				}
			}, 1000);
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Log.e("tag", "accuracy=" + accuracy + "Vendor=" + sensor.getVendor()
		// + "MinDelay=" + sensor.getMinDelay());
	}

	public interface OnSongChangeListener {
		public void onSongChange();
	}

}
