package be.jvvlaend.android.guidemeback;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import be.jvvlaend.utils.android.utils.MyActivity;

public class LocationsActivity extends MyActivity {

	private ListView savedLocationsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations);
		savedLocationsListView = getListView(R.id.locationList);
		fillList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_locations, menu);
		return true;
	}

	private void fillList() {
		GuideMeBackDbHelper dbHelper = new GuideMeBackDbHelper(this);
		ArrayAdapter<SavedLocation> savedData = new ArrayAdapter<SavedLocation>(this, R.layout.saved_location_detail, dbHelper.getSavedLocations());
		savedLocationsListView.setAdapter(savedData);
	}

}
