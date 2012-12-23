package be.jvvlaend.android.guidemeback;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.jvvlaend.utils.android.utils.Utils;

public class SavedLocationsAdapter extends ArrayAdapter<SavedLocation> {

	private Context context;
	private List<SavedLocation> savedData;
	private int layoutResourceId;

	public SavedLocationsAdapter(Context context, int layoutResourceId, List<SavedLocation> savedData) {
		super(context, layoutResourceId, savedData);
		this.context = context;
		this.savedData = savedData;
		this.layoutResourceId = layoutResourceId;
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {
		if (row == null) {
			LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
			row = layoutInflater.inflate(layoutResourceId, parent, false);
		}
		SavedLocation savedLocation = savedData.get(position);
		((TextView) row.findViewById(R.id.savedLocationOmschrijving)).setText(savedLocation.getOmschrijving());
		((TextView) row.findViewById(R.id.savedLocationTime)).setText(Utils.formatDate(savedLocation.getTime()));
		return row;
	}
}
