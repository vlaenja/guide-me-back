package be.jvvlaend.utils.android.utils;

import android.app.ListActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyListActivity extends ListActivity {
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

}
