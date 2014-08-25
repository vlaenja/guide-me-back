package be.jvvlaend.android.guidemeback;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.ClipboardManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import be.jvvlaend.utils.android.utils.MyListActivity;

public class SavedLocationsActivity extends MyListActivity {

	private ListView savedLocationsListView;
	private ArrayList<SavedLocation> savedLocations;
	private GuideMeBackDbHelper dbHelper = new GuideMeBackDbHelper(this);
	private SavedLocationsAdapter savedLocationsadapter;
	private Location actualLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actualLocation = getIntent().getExtras().getParcelable(Constant.ACTUAL_LOCATION);
		setContentView(R.layout.activity_saved_locations);
		savedLocationsListView = getListView();
		savedLocationsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View clickedView, int position, long id) {
				savedLocationsadapter.setSelectedElement(position);
			}
		});
		savedLocationsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView, View clickedView, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra(Constant.NEW_LOCATION_DATA, savedLocations.get(position).getGpsLocation());
				intent.putExtra(Constant.NEW_LOCATION_DESCRIPTION, savedLocations.get(position).getOmschrijving());
				setResult(RESULT_OK, intent);
				finish();
				return true;
			}
		});
		fillList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_locations, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (savedLocationsadapter.isEmpty()) {
			menu.findItem(R.id.menu_savedLocation_delete).setEnabled(false);
			menu.findItem(R.id.menu_savedLocation_deleteAll).setEnabled(false);
			menu.findItem(R.id.menu_savedLocation_edit).setEnabled(false);
			menu.findItem(R.id.menu_savedLocation_set).setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		SavedLocation location;
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		switch (item.getItemId()) {
		case R.id.menu_savedLocation_set:
			Intent intent = new Intent();
			intent.putExtra(Constant.NEW_LOCATION_DATA, savedLocations.get(savedLocationsadapter.getSelectedElement()).getGpsLocation());
			intent.putExtra(Constant.NEW_LOCATION_DESCRIPTION, savedLocations.get(savedLocationsadapter.getSelectedElement()).getOmschrijving());
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.menu_savedLocation_delete:
			dbHelper.deleteSavedLocation(savedLocations.get(savedLocationsadapter.getSelectedElement()).getId());
			savedLocations.remove(savedLocationsadapter.getSelectedElement());
			savedLocationsadapter.notifyDataSetChanged();
			break;
		case R.id.menu_savedLocation_deleteAll:
			dbHelper.deleteAllSavedLocations();
			Toast.makeText(this, getResources().getString(R.string.all_locations_deleted), Toast.LENGTH_SHORT).show();
			finish();
			break;
		case R.id.menu_savedLocation_import_from_clipboard:
			saveLocationFromClipboard();
			// finish();
			break;
		case R.id.menu_savedLocation_edit:
			Intent editIntent = new Intent(this, EditSavedLocationActivity.class);
			Bundle extras = new Bundle();
			location = savedLocations.get(savedLocationsadapter.getSelectedElement());
			extras.putString(Constant.EDIT_OMSCHRIJVING, location.getOmschrijving());
			extras.putDouble(Constant.EDIT_LATITUDE, location.getLatitude());
			extras.putDouble(Constant.EDIT_LONGITUDE, location.getLongitude());
			editIntent.putExtras(extras);
			startActivityForResult(editIntent, Constant.RESULT_EDIT_LOCATION);
			break;
		case R.id.menu_savedLocation_export_location_to_clipboard:
			location = savedLocations.get(savedLocationsadapter.getSelectedElement());
			clipboardManager.setText(getLocationClipboardText(location));
			Toast.makeText(this, Constant.LOCATION_SAVED_IN_CLIPBOARD, Toast.LENGTH_LONG).show();

			break;
		case R.id.menu_savedLocation_export_all_locations_to_clipboard:
			StringBuffer buffer = new StringBuffer("");
			int counter = 0;
			for (SavedLocation loc : savedLocations) {
				counter++;
				buffer.append(getLocationClipboardText(loc));
				if (counter < savedLocations.size()) {
					buffer.append("|");
				}
			}
			clipboardManager.setText(buffer.toString());
			Toast.makeText(this, Constant.LOCATION_SAVED_IN_CLIPBOARD, Toast.LENGTH_LONG).show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private String getLocationClipboardText(SavedLocation location) {
		return location.getOmschrijving() + ";" + getResources().getString(R.string.clipboard_lat) + location.getLatitude() + ";" + getResources().getString(R.string.clipboard_long)
				+ location.getLongitude();
	}

	private void saveLocationFromClipboard() {
		Double latitude;
		Double longitude;
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if (clipboardManager.getText() == null) {
			Toast.makeText(this, getResources().getString(R.string.import_clipboard_empty), Toast.LENGTH_LONG).show();
			return;
		}
		String clipboardText = clipboardManager.getText().toString();
		StringTokenizer stClipboardText = new StringTokenizer(clipboardText, "|");
		while (stClipboardText.hasMoreElements()) {
			String locationText = stClipboardText.nextToken();
			StringTokenizer st = new StringTokenizer(locationText, ";");
			if (st.countTokens() != 3) {
				Toast.makeText(this, getResources().getString(R.string.import_clipboard_format), Toast.LENGTH_LONG).show();
				return;
			}
			String omschrijving = st.nextToken();
			String latText = st.nextToken();
			String longText = st.nextToken();
			if (!latText.startsWith("Lat=")) {
				Toast.makeText(this, getResources().getString(R.string.import_clipboard_format), Toast.LENGTH_LONG).show();
				return;
			}
			st = new StringTokenizer(latText, "=");
			if (st.countTokens() != 2) {
				Toast.makeText(this, getResources().getString(R.string.import_clipboard_format), Toast.LENGTH_LONG).show();
				return;
			}
			st.nextToken();
			try {
				latitude = Double.valueOf(st.nextToken());
			} catch (NumberFormatException e) {
				Toast.makeText(this, getResources().getString(R.string.import_clipboard_format), Toast.LENGTH_LONG).show();
				return;
			}
			if (!longText.startsWith("Long=")) {
				Toast.makeText(this, getResources().getString(R.string.import_clipboard_format), Toast.LENGTH_LONG).show();
				return;
			}
			st = new StringTokenizer(longText, "=");
			if (st.countTokens() != 2) {
				Toast.makeText(this, getResources().getString(R.string.import_clipboard_format), Toast.LENGTH_LONG).show();
				return;
			}
			st.nextToken();
			try {
				longitude = Double.valueOf(st.nextToken());
			} catch (NumberFormatException e) {
				Toast.makeText(this, getResources().getString(R.string.import_clipboard_format), Toast.LENGTH_LONG).show();
				return;
			}
			SavedLocation newLocation = new SavedLocation(new Location(""));
			newLocation.setOmschrijving(omschrijving);
			newLocation.setLatitude(latitude);
			newLocation.setLongitude(longitude);
			newLocation.setTime(System.currentTimeMillis());
			dbHelper.insertLocation(newLocation);
			fillList();
			savedLocationsadapter.notifyDataSetChanged();
		}
	}

	private void fillList() {
		savedLocations = dbHelper.getSavedLocations();
		savedLocationsadapter = new SavedLocationsAdapter(this, R.layout.saved_location_detail, savedLocations, actualLocation);
		savedLocationsadapter.setNotifyOnChange(true);
		savedLocationsListView.setAdapter(savedLocationsadapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.RESULT_EDIT_LOCATION) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				String nieuweOmschrijving = extras.getString(Constant.EDIT_OMSCHRIJVING);
				double latitude = extras.getDouble(Constant.EDIT_LATITUDE);
				double longitude = extras.getDouble(Constant.EDIT_LONGITUDE);
				SavedLocation location = savedLocations.get(savedLocationsadapter.getSelectedElement());
				location.setOmschrijving(nieuweOmschrijving);
				location.setLatitude(latitude);
				location.setLongitude(longitude);
				dbHelper.updateSavedLocation(location);
				savedLocationsadapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
