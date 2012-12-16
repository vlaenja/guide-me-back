package be.jvvlaend.android.guidemeback;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import be.jvvlaend.utils.android.compass.AverageCompassData;
import be.jvvlaend.utils.android.compass.CompassChanged;
import be.jvvlaend.utils.android.compass.CompassData;
import be.jvvlaend.utils.android.compass.CompassSensor;
import be.jvvlaend.utils.android.gps.GPSTracker;
import be.jvvlaend.utils.android.gps.LocationChanged;
import be.jvvlaend.utils.android.utils.MyActivity;

public class DebugActivity extends MyActivity implements LocationChanged, CompassChanged {
	private static final String SAVED_LOCATION_DEBUG = "savedLocationDebug";
	private static final String SAVED_LOCATION_PROVIDER_DEBUG = "savedLocationProviderDebug";
	private static final String SAVED_LOCATION_LATITUDE_DEBUG = "savedLocationLatitudeDebug";
	private static final String SAVED_LOCATION_LONGITUDE_DEBUG = "savedLocationLongitudeDebug";
	private static final int DOUBLE_PRECISION = 100000;
	private static final int DOUBLE_PRECISION_COMPASS = 1000;
	private GPSTracker gpsTracker;
	private Location destinationLocation;
	private Bitmap arrow = null;
	private CompassSensor compassSensor = null;
	private AverageCompassData averageCompassData = new AverageCompassData();
	private float imageRotationAngle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			destinationLocation = (Location) savedInstanceState.getParcelable(SAVED_LOCATION_DEBUG);
		} else {
			// Maybe previously backed-up
			destinationLocation = restoreSavedlocation();
		}
		if (gpsTracker == null) {
			gpsTracker = new GPSTracker(this);
		}
		if (compassSensor == null) {
			compassSensor = new CompassSensor(this);
		}
		setContentView(R.layout.activity_debug);
		arrow = BitmapFactory.decodeResource(getResources(), R.drawable.directionarrow);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (gpsTracker != null) {
			gpsTracker.stopLocationService();
			gpsTracker = null;
			this.finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (outState == null) {
			outState = new Bundle();
		}
		outState.putParcelable(SAVED_LOCATION_DEBUG, destinationLocation);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			destinationLocation = (Location) savedInstanceState.getParcelable(SAVED_LOCATION_DEBUG);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (gpsTracker != null) {
			gpsTracker.stopLocationService();
			gpsTracker = null;
		}
		super.onBackPressed();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (gpsTracker == null) {
			gpsTracker = new GPSTracker(this);
		}
		if (compassSensor == null) {
			compassSensor = new CompassSensor(this);
		}
		compassSensor.startCompass();
		displayDestinationLocation();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (gpsTracker != null) {
			gpsTracker.stopLocationService();
			gpsTracker = null;
		}
		if (compassSensor != null) {
			compassSensor.stopCompass();
			compassSensor = null;
		}
		backupSavedLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gpsTracker == null) {
			gpsTracker = new GPSTracker(this);
		}
		if (compassSensor == null) {
			compassSensor = new CompassSensor(this);
		}
		compassSensor.startCompass();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (gpsTracker != null) {
			gpsTracker.stopLocationService();
			gpsTracker = null;
		}
		compassSensor.stopCompass();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_debug, menu);
		return true;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.debug_savePositionButton:
			destinationLocation = gpsTracker.getLocation();
			displayDestinationLocation();
			break;
		case R.id.debug_loadSavedPositionsButton:
			break;
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d("GuideMeBack", "onMenuItemSelected");
		boolean menuItemSelected = super.onMenuItemSelected(featureId, item);
		return menuItemSelected;
	}

	private void displayDestinationLocation() {
		if (destinationLocation != null) {
			getTextView(R.id.debug_destinationPositionLatitudeData).setText(formatLocation(destinationLocation.getLatitude()));
			getTextView(R.id.debug_destinationPositionLongitudeData).setText(formatLocation(destinationLocation.getLongitude()));
			getTextView(R.id.debug_distanceToDestinationData).setText("");
		} else {
			getTextView(R.id.debug_destinationPositionLatitudeData).setText("---");
			getTextView(R.id.debug_destinationPositionLongitudeData).setText("---");
		}
	}

	private CharSequence formatLocation(double value) {
		int tmp = (int) (value * DOUBLE_PRECISION);
		return String.valueOf((double) (tmp) / DOUBLE_PRECISION);
	}

	private CharSequence formatCompassLocation(float value) {
		int tmp = (int) (value * DOUBLE_PRECISION_COMPASS);
		return String.valueOf((double) (tmp) / DOUBLE_PRECISION_COMPASS);
	}

	@Override
	public void onLocationChanged(Location location) {
		getTextView(R.id.debug_currentPositionLatitudeData).setText(formatLocation(location.getLatitude()));
		getTextView(R.id.debug_currentPositionLongitudeData).setText(formatLocation(location.getLongitude()));
		if (destinationLocation != null) {
			getTextView(R.id.debug_distanceToDestinationData).setText(String.valueOf(location.distanceTo(destinationLocation)));
			getTextView(R.id.debug_gpsRotationAngle).setText(formatCompassLocation(location.bearingTo(destinationLocation)));
			rotateGPSImage(location.bearingTo(destinationLocation));
		}
		getTextView(R.id.speedData).setText(String.valueOf(location.getSpeed()));
	}

	private void backupSavedLocation() {
		if (destinationLocation != null) {
			SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putString(SAVED_LOCATION_PROVIDER_DEBUG, String.valueOf(destinationLocation.getProvider()));
			editor.putString(SAVED_LOCATION_LATITUDE_DEBUG, String.valueOf(destinationLocation.getLatitude()));
			editor.putString(SAVED_LOCATION_LONGITUDE_DEBUG, String.valueOf(destinationLocation.getLongitude()));
			editor.commit();
		}
	}

	private Location restoreSavedlocation() {
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		String provider = sharedPreferences.getString(SAVED_LOCATION_PROVIDER_DEBUG, null);
		if (provider != null) {
			Location location = new Location(provider);
			location.setLatitude(Double.valueOf(sharedPreferences.getString(SAVED_LOCATION_LATITUDE_DEBUG, "0")));
			location.setLongitude(Double.valueOf(sharedPreferences.getString(SAVED_LOCATION_LONGITUDE_DEBUG, "0")));
			return location;
		}
		return null;
	}

	@Override
	public void onCompassSensorChanged(SensorEvent event) {
		CompassData compassData = new CompassData(event);
		averageCompassData.add(compassData);
		CompassData avg = averageCompassData.getAverage();
		getTextView(R.id.debug_compassXData).setText(formatCompassLocation(avg.getX()));
		getTextView(R.id.debug_compassYData).setText(formatCompassLocation(avg.getY()));
		getTextView(R.id.debug_compassZData).setText(formatCompassLocation(avg.getZ()));
		rotateCompassImage(Math.abs(avg.getX() - 360));
	}

	private void rotateGPSImage(float angle) {
		ImageView imageView = getImageView(R.id.debug_gpsDirectionImage);
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
		imageView.setImageBitmap(rotatedBitmap);
		getTextView(R.id.debug_gpsRotationAngle).setText(String.valueOf(angle));
	}

	private void rotateCompassImage(float angle) {
		float delta = imageRotationAngle - angle;
		getTextView(R.id.debug_compassDelta).setText(String.valueOf(delta));
		if (Math.abs(delta) > 2) {
			imageRotationAngle = angle;
			ImageView imageView = getImageView(R.id.debug_compassDirectionImage);
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			Bitmap rotatedBitmap = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
			imageView.setImageBitmap(rotatedBitmap);
			getTextView(R.id.debug_compassRotationAngle).setText(String.valueOf(angle));
		}
	}
}
