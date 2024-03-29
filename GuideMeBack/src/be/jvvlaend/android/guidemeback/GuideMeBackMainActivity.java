package be.jvvlaend.android.guidemeback;

import android.content.ClipboardManager;
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
import android.widget.ImageView;
import android.widget.Toast;
import be.jvvlaend.utils.android.compass.AverageCompassData;
import be.jvvlaend.utils.android.compass.CompassChanged;
import be.jvvlaend.utils.android.compass.CompassData;
import be.jvvlaend.utils.android.compass.CompassSensor;
import be.jvvlaend.utils.android.gps.AveragerGPSData;
import be.jvvlaend.utils.android.gps.GPSTracker;
import be.jvvlaend.utils.android.gps.LocationChanged;
import be.jvvlaend.utils.android.utils.MyActivity;
import be.jvvlaend.utils.android.utils.Utils;

public class GuideMeBackMainActivity extends MyActivity implements LocationChanged, CompassChanged {
	private GPSTracker gpsTracker;
	private Location destinationLocation;
	private Location previousReceivedGPSLocation = null;
	private long lastReceivedGPSLocationTime = 0;
	private Bitmap arrow = null;
	private CompassSensor compassSensor = null;
	private AverageCompassData averageCompassData = new AverageCompassData();
	private AveragerGPSData averagerGPSData = new AveragerGPSData();
	private float imageRotationAngle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			destinationLocation = (Location) savedInstanceState.getParcelable(Constant.SAVED_LOCATION);
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
		setScreenOn(ParameterDbHelper.getParameterScreenOn(this));
		invalidateOptionsMenu();
	}

	private void initScreenData() {
		getTextView(R.id.speedData).setText(R.string.no_data);
		getTextView(R.id.distanceData).setText(R.string.no_data);
		getTextView(R.id.distanceUnits).setText(R.string.no_data);

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
		bundle.putParcelable(Constant.SAVED_LOCATION, destinationLocation);
		super.onSaveInstanceState(bundle);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			destinationLocation = (Location) savedInstanceState.getParcelable(Constant.SAVED_LOCATION);
		}
		setScreenOn(ParameterDbHelper.getParameterScreenOn(this));
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
		setScreenOn(ParameterDbHelper.getParameterScreenOn(this));
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
		menu.findItem(R.id.menu_settings).setEnabled(true);
		menu.findItem(R.id.menu_save).setEnabled(true);
		menu.findItem(R.id.menu_clipboard).setEnabled(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_debug:
			Intent debugIntent = new Intent(this, DebugActivity.class);
			startActivity(debugIntent);
			return true;
		case R.id.menu_quick_save_location:
			if (previousReceivedGPSLocation != null) {
				destinationLocation = previousReceivedGPSLocation;
				Intent editIntent = new Intent(this, EditSavedLocationActivity.class);
				Bundle extras = new Bundle(3);
				extras.putString(Constant.EDIT_OMSCHRIJVING, "");
				extras.putDouble(Constant.EDIT_LONGITUDE, destinationLocation.getLongitude());
				extras.putDouble(Constant.EDIT_LATITUDE, destinationLocation.getLatitude());
				editIntent.putExtras(extras);
				startActivityForResult(editIntent, Constant.RESULT_EDIT_LOCATION);
			} else {
				Toast.makeText(this, Constant.UNKNOWN_GPS_LOCATION, Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_quick_save_car:
			if (previousReceivedGPSLocation != null) {
				destinationLocation = previousReceivedGPSLocation;
				quickSaveLocation(getResources().getString(R.string.quick_save_car), destinationLocation);
			} else {
				Toast.makeText(this, Constant.UNKNOWN_GPS_LOCATION, Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_quick_save_home:
			if (previousReceivedGPSLocation != null) {
				destinationLocation = previousReceivedGPSLocation;
				quickSaveLocation(getResources().getString(R.string.quick_save_home), destinationLocation);
			} else {
				Toast.makeText(this, Constant.UNKNOWN_GPS_LOCATION, Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_quick_save_hotel:
			if (previousReceivedGPSLocation != null) {
				destinationLocation = previousReceivedGPSLocation;
				quickSaveLocation(getResources().getString(R.string.quick_save_hotel), destinationLocation);
			} else {
				Toast.makeText(this, Constant.UNKNOWN_GPS_LOCATION, Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_clipboard:
			if (previousReceivedGPSLocation != null) {
				saveLocationToClipboard();
			} else {
				Toast.makeText(this, Constant.UNKNOWN_GPS_LOCATION, Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.menu_settings:
			Intent settingsActivity = new Intent(this, MySetupActivity.class);
			// Intent settingsActivity = new Intent(this,
			// SettingsActivity.class);
			startActivity(settingsActivity);
			return true;
		case R.id.menu_stored_locations:
			Intent storedLocationsIntent = new Intent(this, SavedLocationsActivity.class);
			storedLocationsIntent.putExtra(Constant.ACTUAL_LOCATION, previousReceivedGPSLocation);
			startActivityForResult(storedLocationsIntent, Constant.RESULT_FOR_SAVED_LOCATION);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.RESULT_FOR_SAVED_LOCATION) {
			if (resultCode == RESULT_OK) {
				destinationLocation = data.getParcelableExtra(Constant.NEW_LOCATION_DATA);
				String locationDescription = data.getStringExtra(Constant.NEW_LOCATION_DESCRIPTION);
				Toast.makeText(this, locationDescription + " " + getResources().getString(R.string.pos_set), Toast.LENGTH_SHORT).show();
			}
		}
		if (requestCode == Constant.RESULT_EDIT_LOCATION) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				String omschrijving = extras.getString(Constant.EDIT_OMSCHRIJVING);
				double latitude = extras.getDouble(Constant.EDIT_LATITUDE);
				double longitude = extras.getDouble(Constant.EDIT_LONGITUDE);
				SavedLocation editedLocation = new SavedLocation(destinationLocation);
				editedLocation.setOmschrijving(omschrijving);
				editedLocation.setLatitude(latitude);
				editedLocation.setLongitude(longitude);
				quickSaveLocation(editedLocation);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void quickSaveLocation(SavedLocation savedLocation) {
		savedLocation.setTime(System.currentTimeMillis());
		GuideMeBackDbHelper dbHelper = new GuideMeBackDbHelper(this);
		dbHelper.insertLocation(savedLocation);
		dbHelper = null;
		Toast.makeText(this, savedLocation.getOmschrijving() + " " + getResources().getString(R.string.pos_saved), Toast.LENGTH_LONG).show();
	}

	private void quickSaveLocation(String omschrijving, Location location) {
		SavedLocation savedLocation = new SavedLocation(location);
		savedLocation.setOmschrijving(omschrijving);
		quickSaveLocation(savedLocation);
	}

	@Override
	public void onLocationChanged(Location location) {
		showActualSpeed(location.getSpeed());
		averagerGPSData.add(location);
		if (destinationLocation != null) {
			formatDistanceToDestination(location.distanceTo(destinationLocation));
			rotateImage(calculateDirection(averagerGPSData.getAverage(), previousReceivedGPSLocation, destinationLocation));
		}
		keepLastGPSLocation(averagerGPSData.getAverage());
	}

	private float calculateDirection(Location actualLocation, Location previousLocation, Location destinationLocation) {
		if (actualLocation == null || previousLocation == null || destinationLocation == null) {
			return 0f;
		}
		// TODO: uitzoeken waarom ik met -1 moet vermenigvuldigen...
		// Proefondervindelijk is het dan in orde :-)
		return (previousLocation.bearingTo(actualLocation) - actualLocation.bearingTo(destinationLocation)) * (-1f);
	}

	private float calculateCompassDirection(Location previousLocation, AverageCompassData averageCompassData, Location destinationLocation) {
		if (previousReceivedGPSLocation != null && destinationLocation != null) {
			return (averageCompassData.getAverage().getX() - previousReceivedGPSLocation.bearingTo(destinationLocation)) * (-1f);
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

	private void formatDistanceToDestination(float distanceTo) {
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
			editor.putString(Constant.SAVED_LOCATION_PROVIDER, String.valueOf(destinationLocation.getProvider()));
			editor.putString(Constant.SAVED_LOCATION_LATITUDE, String.valueOf(destinationLocation.getLatitude()));
			editor.putString(Constant.SAVED_LOCATION_LONGITUDE, String.valueOf(destinationLocation.getLongitude()));
			editor.commit();
		}
	}

	private Location restoreSavedlocation() {
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		String provider = sharedPreferences.getString(Constant.SAVED_LOCATION_PROVIDER, null);
		if (provider != null) {
			Location location = new Location(provider);
			location.setLatitude(Double.valueOf(sharedPreferences.getString(Constant.SAVED_LOCATION_LATITUDE, "0")));
			location.setLongitude(Double.valueOf(sharedPreferences.getString(Constant.SAVED_LOCATION_LONGITUDE, "0")));
			return location;
		}
		return null;
	}

	@Override
	public void onCompassSensorChanged(SensorEvent event) {
		CompassData compassData = new CompassData(event);
		averageCompassData.add(compassData);
		if (!gpsIsUpdating()) {

			if ((Math.abs(compassData.getY()) > Constant.DEVICE_ALLOWED_LEVEL) || (Math.abs(compassData.getZ()) > Constant.DEVICE_ALLOWED_LEVEL)) {
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
		return (System.currentTimeMillis() - lastReceivedGPSLocationTime) < Constant.GPS_EXPECTED_UPDATE_TIME;
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

	private void saveLocationToClipboard() {
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboardManager.setText(getResources().getString(R.string.clipboard_descr) + ";" + getResources().getString(R.string.clipboard_lat) + previousReceivedGPSLocation.getLatitude() + ";" + getResources().getString(R.string.clipboard_long)
				+ previousReceivedGPSLocation.getLongitude());
		Toast.makeText(this, Constant.LOCATION_SAVED_IN_CLIPBOARD, Toast.LENGTH_LONG).show();
	}

}
