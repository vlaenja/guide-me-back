package be.jvvlaend.android.guidemeback;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import be.jvvlaend.utils.android.utils.MyActivity;

public class SavedLocationsActivity extends MyActivity {

	private ListView savedLocationsListView;
	private ArrayList<SavedLocation> savedLocations;
	private GuideMeBackDbHelper dbHelper = new GuideMeBackDbHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_locations);
		savedLocationsListView = getListView(R.id.savedLocationList);
		savedLocationsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View element, int position, long id) {
				Log.d("SavedLocationsActivity", "position clicked = " + position);
				Intent intent = new Intent();
				intent.putExtra("NEW_LOCATION", savedLocations.get(position).getGpsLocation());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		savedLocationsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView, View element, int position, long id) {
				dbHelper.deleteSavedLocation(savedLocations.get(position).getId());
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
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_savedLocation_deleteAll:
			dbHelper.deleteAllSavedLocations();
			Toast.makeText(this, "All locations deleted", Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void fillList() {
		savedLocations = dbHelper.getSavedLocations();
		SavedLocationsAdapter adapter = new SavedLocationsAdapter(this, R.layout.saved_location_detail, savedLocations);
		savedLocationsListView.setAdapter(adapter);
	}

}
