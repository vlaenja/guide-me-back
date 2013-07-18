package be.jvvlaend.utils.android.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassSensor implements SensorEventListener {
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private Context context;

	public CompassSensor(Context mContext) {
		context = mContext;
	}

	public void startCompass() {
		if (sensorManager == null) {
			sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
		}
	}

	public void stopCompass() {
		if (sensorManager != null) {
			sensorManager.unregisterListener(this, sensor);
			sensorManager = null;
		}
		if (sensor != null) {
			sensor = null;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			if (context instanceof CompassChanged) {
				((CompassChanged) context).onCompassSensorChanged(event);
			}
		}

	}

}
