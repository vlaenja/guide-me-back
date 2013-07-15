package be.jvvlaend.android.guidemeback;

import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import be.jvvlaend.utils.android.utils.MyActivity;

public class MySetupActivity extends MyActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_setup);
		CheckBox checkbox = getCheckBox(R.id.setupCheckBoxKeepScreenOn);
		checkbox.setChecked(ParameterDbHelper.getParameterScreenOn(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_setup, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		saveSettings();
		super.onBackPressed();
	}

	private void saveSettings() {
		boolean checked = getCheckBox(R.id.setupCheckBoxKeepScreenOn).isChecked();
		ParameterDbHelper.setParameterScreenOn(this, checked);
	}
}
