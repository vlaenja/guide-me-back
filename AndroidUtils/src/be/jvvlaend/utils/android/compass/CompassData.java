package be.jvvlaend.utils.android.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class CompassData {

	private SensorEvent sensorEvent;

	public CompassData(SensorEvent sensorEvent) {
		if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			this.sensorEvent = sensorEvent;
		} else {
			throw new IllegalArgumentException("Expecting Magnectic field data");
		}
	}

	public float getX() {
		return sensorEvent.values[0];
	}

	public float getY() {
		return sensorEvent.values[1];
	}

	public float getZ() {
		return sensorEvent.values[2];
	}

	public long getTimestamp() {
		return sensorEvent.timestamp;
	}

	public int getAccuracy() {
		return sensorEvent.accuracy;
	}

}
