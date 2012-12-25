package be.jvvlaend.android.guidemeback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
		getTextView(R.id.editSavedLocationDescription).setText((String) getIntent().getExtras().get(Constant.EDIT_OMSCHRIJVING));
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.editSavedLocationButtonOk:
			Intent intent = new Intent();
			String nieuweOmschrijving = getTextView(R.id.editSavedLocationDescription).getText().toString();
			intent.putExtra(Constant.EDIT_NIEUWE_OMSCHRIJVING, nieuweOmschrijving);
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
