package be.jvvlaend.android.guidemeback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import be.jvvlaend.utils.android.compass.AverageCompassData;
import be.jvvlaend.utils.android.compass.CompassChanged;
import be.jvvlaend.utils.android.compass.CompassSensor;
import be.jvvlaend.utils.android.gps.GPSTracker;
import be.jvvlaend.utils.android.gps.LocationChanged;
import be.jvvlaend.utils.android.utils.MyActivity;

public class GuideMeBackMainActivity extends MyActivity implements LocationChanged, CompassChanged {
	private static final String SAVED_LOCATION = "savedLocation";
	private static final String SAVED_LOCATION_PROVIDER = "savedLocationProvider";
	private static final String SAVED_LOCATION_LATITUDE = "savedLocationLatitude";
	private static final String SAVED_LOCATION_LONGITUDE = "savedLocationLongitude";
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
			destinationLocation = (Location) savedInstanceState.getParcelable(SAVED_LOCATION);
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
		setContentView(R.layout.activity_guide_me_back_main);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (compassSensor != null) {
			compassSensor.stopCompass();
			compassSensor = null;
		}
		if (gpsTracker != null) {
			gpsTracker.stopLocationService();
			gpsTracker = null;
			this.finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putParcelable(SAVED_LOCATION, destinationLocation);
		super.onSaveInstanceState(bundle);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			destinationLocation = (Location) savedInstanceState.getParcelable(SAVED_LOCATION);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (compassSensor != null) {
			compassSensor.stopCompass();
			compassSensor = null;
		}
		if (gpsTracker != null) {
			gpsTracker.stopLocationService();
			gpsTracker = null;
			this.finish();
		}
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
		if (compassSensor != null) {
			compassSensor.stopCompass();
			compassSensor = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_guide_me_back_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_manage_locations).setEnabled(false);
		menu.findItem(R.id.menu_quick_save).setEnabled(false);
		menu.findItem(R.id.menu_settings).setEnabled(false);
		menu.findItem(R.id.menu_stored_locations).setEnabled(false);
		return super.onPrepareOptionsMenu(menu);
	}

	public void onClick(View view) {
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_debug:
			Intent intent = new Intent(this, DebugActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_quick_save:
			break;
		case R.id.menu_manage_locations:
			break;
		case R.id.menu_settings:
			break;
		case R.id.menu_stored_locations:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
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
	}

	private void backupSavedLocation() {
		if (destinationLocation != null) {
			SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putString(SAVED_LOCATION_PROVIDER, String.valueOf(destinationLocation.getProvider()));
			editor.putString(SAVED_LOCATION_LATITUDE, String.valueOf(destinationLocation.getLatitude()));
			editor.putString(SAVED_LOCATION_LONGITUDE, String.valueOf(destinationLocation.getLongitude()));
			editor.commit();
		}
	}

	private Location restoreSavedlocation() {
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		String provider = sharedPreferences.getString(SAVED_LOCATION_PROVIDER, null);
		if (provider != null) {
			Location location = new Location(provider);
			location.setLatitude(Double.valueOf(sharedPreferences.getString(SAVED_LOCATION_LATITUDE, "0")));
			location.setLongitude(Double.valueOf(sharedPreferences.getString(SAVED_LOCATION_LONGITUDE, "0")));
			return location;
		}
		return null;
	}

	@Override
	public void onCompassSensorChanged(SensorEvent event) {
	}

	private void rotateGPSImage(float angle) {
		float delta = imageRotationAngle - angle;
		getTextView(R.id.compassDelta).setText(String.valueOf(delta));
		if (Math.abs(delta) > 2) {
			imageRotationAngle = angle;
			ImageView imageView = getImageView(R.id.gpsDirectionImage);
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			Bitmap rotatedBitmap = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
			imageView.setImageBitmap(rotatedBitmap);
			getTextView(R.id.gpsRotationAngle).setText(String.valueOf(angle));
		}
	}

	private void rotateCompassImage(float angle) {
		float delta = imageRotationAngle - angle;
		getTextView(R.id.compassDelta).setText(String.valueOf(delta));
		if (Math.abs(delta) > 2) {
			imageRotationAngle = angle;
			ImageView imageView = getImageView(R.id.compassDirectionImage);
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			Bitmap rotatedBitmap = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
			imageView.setImageBitmap(rotatedBitmap);
			getTextView(R.id.compassRotationAngle).setText(String.valueOf(angle));
		}
	}
}
