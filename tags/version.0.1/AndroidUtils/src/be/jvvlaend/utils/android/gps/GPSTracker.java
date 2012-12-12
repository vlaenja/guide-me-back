package be.jvvlaend.utils.android.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSTracker extends Service implements LocationListener {
	private static final int MINIMUM_DISTANCE_BETWEEN_UPDATES = 5;
	private static final int MINIMUM_TIME_BETWEEN_UPDATES = 500;
	private Context myContext;
	private LocationManager locationManager;
	private String bestProvider;

	public GPSTracker(Context context) {
		myContext = context;
		initLocation();
	}

	public void initLocation() {
		locationManager = (LocationManager) myContext.getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		bestProvider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(bestProvider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_BETWEEN_UPDATES, this);
	}

	public Location getLocation() {
		if (locationManager != null) {
			return locationManager.getLastKnownLocation(bestProvider);
		} else {
			return null;
		}
	}

	public void startLocationService() {
		initLocation();

	}

	public void stopLocationService() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager = null;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// logger.info("onProviderDisabled:" + provider);

	}

	@Override
	public void onProviderEnabled(String provider) {
		// logger.info("onProviderEnabled:" + provider);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// logger.info("onStatusChanged:");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// logger.info("onBind:");
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (myContext instanceof LocationChanged) {
			((LocationChanged) myContext).onLocationChanged(location);
		}

	}
}
