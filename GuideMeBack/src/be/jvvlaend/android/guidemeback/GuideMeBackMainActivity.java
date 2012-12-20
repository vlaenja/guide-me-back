package be.jvvlaend.android.guidemeback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import be.jvvlaend.utils.android.compass.CompassData;
import be.jvvlaend.utils.android.compass.CompassSensor;
import be.jvvlaend.utils.android.gps.GPSTracker;
import be.jvvlaend.utils.android.gps.LocationChanged;
import be.jvvlaend.utils.android.utils.MyActivity;
import be.jvvlaend.utils.android.utils.Utils;

public class GuideMeBackMainActivity extends MyActivity implements LocationChanged, CompassChanged {
	private static final int DEVICE_ALLOWED_LEVEL = 25;
	private static final int GPS_EXPECTED_UPDATE_TIME = 5000;
	private static final String SAVED_LOCATION = "savedLocation";
	private static final String SAVED_LOCATION_PROVIDER = "savedLocationProvider";
	private static final String SAVED_LOCATION_LATITUDE = "savedLocationLatitude";
	private static final String SAVED_LOCATION_LONGITUDE = "savedLocationLongitude";
	private GPSTracker gpsTracker;
	private Location destinationLocation;
	private Location previousReceivedGPSLocation = null;
	private long lastReceivedGPSLocationTime = 0;
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
		arrow = BitmapFactory.decodeResource(getResources(), R.drawable.directionarrow);
		initScreenData();
		setScreenOn(true);
	}

	private void initScreenData() {
		getTextView(R.id.speedData).setText("--");
		getTextView(R.id.distanceData).setText("--");
		getTextView(R.id.distanceUnits).setText("--");

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
		setScreenOn(false);
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
		setScreenOn(false);
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
		setScreenOn(true);
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
		setScreenOn(false);
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
		setScreenOn(true);
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
		setScreenOn(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_guide_me_back_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_manage_locations).setEnabled(false);
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
			if (previousReceivedGPSLocation != null) {
				destinationLocation = previousReceivedGPSLocation;
				quickSaveLocation(destinationLocation);
			}
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

	private void quickSaveLocation(Location location) {
		SavedLocation savedLocation = new SavedLocation(location);
		savedLocation.setOmschrijving("Quick save");
		GuideMeBackDbHelper dbHelper = new GuideMeBackDbHelper(this);
		dbHelper.insertLocation(savedLocation);
		dbHelper = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		showActualSpeed(location.getSpeed());
		if (destinationLocation != null) {
			showDistanceToDestination(location.distanceTo(destinationLocation));
			rotateImage(calculateDirection(location, previousReceivedGPSLocation, destinationLocation));
			getTextView(R.id.debugline1).setText("GPS: " + String.valueOf(calculateDirection(location, previousReceivedGPSLocation, destinationLocation)));
		}
		keepLastGPSLocation(location);
	}

	private float calculateDirection(Location actualLocation, Location previousLocation, Location destinationLocation) {
		if (actualLocation == null || previousLocation == null || destinationLocation == null) {
			return 0f;
		}
		return previousLocation.bearingTo(actualLocation) - actualLocation.bearingTo(destinationLocation);
	}

	private float calculateCompassDirection(Location previousLocation, AverageCompassData averageCompassData, Location destinationLocation) {
		if (previousReceivedGPSLocation != null && destinationLocation != null) {
			return averageCompassData.getAverage().getX() - previousReceivedGPSLocation.bearingTo(destinationLocation);
		} else {
			return 0f;
		}
	}

	private void keepLastGPSLocation(Location location) {
		lastReceivedGPSLocationTime = System.currentTimeMillis();
		previousReceivedGPSLocation = new Location(location);
	}

	private void showActualSpeed(float speed) {
		getTextView(R.id.speedData).setText(String.valueOf(Utils.afrondenTotCijfersNaKomma(speed * 3.6f, 2)));

	}

	private void showDistanceToDestination(float distanceTo) {
		int distance = Float.valueOf(distanceTo).intValue();
		if (distance < 10000) {
			getTextView(R.id.distanceUnits).setText("m");
			getTextView(R.id.distanceData).setText(String.valueOf(distance));
		} else {
			getTextView(R.id.distanceUnits).setText("Km");
			getTextView(R.id.distanceData).setText(String.valueOf(Utils.afrondenTotCijfersNaKomma(distance / 1000f, 2)));
		}

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
		CompassData compassData = new CompassData(event);
		averageCompassData.add(compassData);
		getTextView(R.id.debugline2).setText("Compass: " + calculateCompassDirection(previousReceivedGPSLocation, averageCompassData, destinationLocation));
		if (!gpsIsUpdating()) {

			if ((Math.abs(compassData.getY()) > DEVICE_ALLOWED_LEVEL) || (Math.abs(compassData.getZ()) > DEVICE_ALLOWED_LEVEL)) {
				getTextView(R.id.info1).setText(R.string.keepLevel);
			} else {
				getTextView(R.id.info1).setText(R.string.emptyString);
			}
			rotateImage(calculateCompassDirection(previousReceivedGPSLocation, averageCompassData, destinationLocation));
		} else {
			getTextView(R.id.info1).setText(R.string.emptyString);
		}
	}

	private boolean gpsIsUpdating() {
		return (System.currentTimeMillis() - lastReceivedGPSLocationTime) < GPS_EXPECTED_UPDATE_TIME;
	}

	private void rotateImage(float angle) {
		float delta = imageRotationAngle - angle;
		if (Math.abs(delta) > 2) {
			imageRotationAngle = angle;
			ImageView imageView = getImageView(R.id.directionImage);
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			Bitmap rotatedBitmap = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
			imageView.setImageBitmap(rotatedBitmap);
			rotatedBitmap = null;
			matrix = null;
		}
	}

}
