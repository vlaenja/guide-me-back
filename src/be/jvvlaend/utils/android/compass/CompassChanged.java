package be.jvvlaend.utils.android.compass;

import android.hardware.SensorEvent;

public interface CompassChanged {
	public void onCompassSensorChanged(SensorEvent event);

}
