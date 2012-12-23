package be.jvvlaend.utils.android.utils;

import android.app.Activity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyActivity extends Activity {

	protected TextView getTextView(int id) {
		return (TextView) findViewById(id);
	}

	protected Button getButton(int id) {
		return (Button) findViewById(id);
	}

	protected ImageView getImageView(int id) {
		return (ImageView) findViewById(id);

	}

	public ListView getListView(int locationlist) {
		return (ListView) findViewById(locationlist);
	}

	public void setScreenOn(boolean on) {
		if (on) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}
}
