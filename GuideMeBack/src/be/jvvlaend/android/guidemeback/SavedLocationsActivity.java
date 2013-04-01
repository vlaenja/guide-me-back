package be.jvvlaend.android.guidemeback;

import java.util.ArrayList;

import android.content.Intent;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			Toast.makeText(this, "All locations deleted", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case R.id.menu_savedLocation_edit:
			Intent editIntent = new Intent(this, EditSavedLocationActivity.class);
			editIntent.putExtra(Constant.EDIT_OMSCHRIJVING, savedLocations.get(savedLocationsadapter.getSelectedElement()).getOmschrijving());
			startActivityForResult(editIntent, Constant.EDIT_OMSCHRIJVING_RESULT);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void fillList() {
		savedLocations = dbHelper.getSavedLocations();
		savedLocationsadapter = new SavedLocationsAdapter(this, R.layout.saved_location_detail, savedLocations);
		savedLocationsadapter.setNotifyOnChange(true);
		savedLocationsListView.setAdapter(savedLocationsadapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.EDIT_OMSCHRIJVING_RESULT) {
			if (resultCode == RESULT_OK) {
				String nieuweOmschrijving = data.getStringExtra(Constant.EDIT_NIEUWE_OMSCHRIJVING);
				savedLocations.get(savedLocationsadapter.getSelectedElement()).setOmschrijving(nieuweOmschrijving);
				dbHelper.updateSavedLocation(savedLocations.get(savedLocationsadapter.getSelectedElement()));
				savedLocationsadapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}