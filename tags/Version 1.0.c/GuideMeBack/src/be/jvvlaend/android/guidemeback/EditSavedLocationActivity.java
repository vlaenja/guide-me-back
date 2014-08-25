package be.jvvlaend.android.guidemeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import be.jvvlaend.utils.android.utils.MyActivity;

public class EditSavedLocationActivity extends MyActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_saved_location);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Bundle extras = getIntent().getExtras();
		getTextView(R.id.editSavedLocationDescription).setText(extras.getString(Constant.EDIT_OMSCHRIJVING));
		getTextView(R.id.editLatitude).setText(String.valueOf(extras.getDouble(Constant.EDIT_LATITUDE)));
		getTextView(R.id.editLongitude).setText(String.valueOf(extras.getDouble(Constant.EDIT_LONGITUDE)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_saved_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.editSavedLocationButtonOk:
			Intent intent = new Intent();
			String nieuweOmschrijving = getTextView(R.id.editSavedLocationDescription).getText().toString();
			Double latitude = Double.valueOf(getTextView(R.id.editLatitude).getText().toString());
			Double longitude = Double.valueOf(getTextView(R.id.editLongitude).getText().toString());
			Bundle extras = new Bundle();
			extras.putString(Constant.EDIT_OMSCHRIJVING, nieuweOmschrijving);
			extras.putDouble(Constant.EDIT_LATITUDE, latitude);
			extras.putDouble(Constant.EDIT_LONGITUDE, longitude);
			intent.putExtras(extras);
			if (getParent() == null) {
				setResult(RESULT_OK, intent);
			} else {
				getParent().setResult(RESULT_OK, intent);
			}
			finish();
			break;
		case R.id.editSavedLocationButtonCancel:
			finish();
			break;
		}
	}

}
