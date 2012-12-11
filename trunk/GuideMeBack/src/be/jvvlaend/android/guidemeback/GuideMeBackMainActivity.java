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

public class GuideMeBackMainActivity extends MyActivity implements LocationChanged, CompassChanged {
	private static final String SAVED_LOCATION = "savedLocation";
	private static final String SAVED_LOCATION_PROVIDER = "savedLocationProvider";
	private static final String SAVED_LOCATION_LATITUDE = "savedLocationLatitude";
	private static final String SAVED_LOCATION_LONGITUDE = "savedLocationLongitude";
	private static final int DOUBLE_PRECISION = 100000;
	private GPSTracker gpsTracker;
	private Location destinationLocation;
	private Bitmap arrow = null;
	private int angle = 0;
	private CompassSensor compassSensor = null;
	private AverageCompassData averageCompassData = new AverageCompassData();

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
		arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
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
		outState.putParcelable(SAVED_LOCATION, destinationLocation);
		super.onSaveInstanceState(outState);
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
		// TODO Auto-generated method stub
		super.onBackPressed();
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
		getMenuInflater().inflate(R.menu.activity_guide_me_back_main, menu);
		return true;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.savePositionButton:
			destinationLocation = gpsTracker.getLocation();
			displayDestinationLocation();
			break;
		case R.id.loadSavedPositionsButton:
			rotateImage();
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
			getTextView(R.id.destinationPositionLatitudeData).setText(formatLocation(destinationLocation.getLatitude()));
			getTextView(R.id.destinationPositionLongitudeData).setText(formatLocation(destinationLocation.getLongitude()));
			getTextView(R.id.distanceToDestinationData).setText("");
		} else {
			getTextView(R.id.destinationPositionLatitudeData).setText("---");
			getTextView(R.id.destinationPositionLongitudeData).setText("---");
		}
	}

	private CharSequence formatLocation(double value) {
		int tmp = (int) (value * DOUBLE_PRECISION);
		return String.valueOf((double) (tmp) / DOUBLE_PRECISION);
	}

	private CharSequence formatSensorLocation(float value) {
		int tmp = (int) (value * DOUBLE_PRECISION);
		return String.valueOf((double) (tmp) / DOUBLE_PRECISION);
	}

	@Override
	public void onLocationChanged(Location location) {
		getTextView(R.id.currentPositionLatitudeData).setText(formatLocation(location.getLatitude()));
		getTextView(R.id.currentPositionLongitudeData).setText(formatLocation(location.getLongitude()));
		if (destinationLocation != null) {
			getTextView(R.id.distanceToDestinationData).setText(String.valueOf(location.distanceTo(destinationLocation)));
		}
		getTextView(R.id.speedData).setText(String.valueOf(location.getSpeed()));
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
		Log.d("GuideMeBack", "CompassSensorChanged:" + event.toString());
		CompassData compassData = new CompassData(event);
		averageCompassData.add(compassData);
		CompassData avg = averageCompassData.getAverage();
		getTextView(R.id.compassXData).setText(formatSensorLocation(avg.getX()));
		getTextView(R.id.compassYData).setText(formatSensorLocation(avg.getY()));
		getTextView(R.id.compassZData).setText(formatSensorLocation(avg.getZ()));
	}

	private void rotateImage() {
		Log.v("GuideMeBack", "RotateImage");
		ImageView imageView = getImageView(R.id.directionImage);
		// Matrix matrix = imageView.getImageMatrix();
		Matrix matrix = new Matrix();
		angle += 10;
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
		imageView.setImageBitmap(rotatedBitmap);
	}
}
