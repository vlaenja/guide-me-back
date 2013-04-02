package be.jvvlaend.android.guidemeback;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.jvvlaend.android.guidemeback.R.color;
import be.jvvlaend.utils.android.utils.Utils;

public class SavedLocationsAdapter extends ArrayAdapter<SavedLocation> {

	private Context context;
	private List<SavedLocation> savedData;
	private int layoutResourceId;
	private int selectedElement = 0;
	private Location actualLocation;

	public SavedLocationsAdapter(Context context, int layoutResourceId, List<SavedLocation> savedData, Location actualLocation) {
		super(context, layoutResourceId, savedData);
		this.context = context;
		this.savedData = savedData;
		this.layoutResourceId = layoutResourceId;
		this.actualLocation = actualLocation;
	}

	@Override
	public View getView(int position, View convertRow, ViewGroup parent) {
		View row = convertRow;
		if (row == null) {
			LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
			row = layoutInflater.inflate(layoutResourceId, parent, false);
		}
		if (getSelectedElement() == position) {
			row.setBackgroundColor(color.LightSalmon);
		} else {
			row.setBackgroundColor(color.LightGrey);
		}
		SavedLocation savedLocation = savedData.get(position);
		((TextView) row.findViewById(R.id.savedLocationOmschrijving)).setText(savedLocation.getOmschrijving());
		((TextView) row.findViewById(R.id.savedLocationTime)).setText(Utils.formatDate(savedLocation.getTime()));
		if (actualLocation == null) {
			((TextView) row.findViewById(R.id.savedLocationDistance)).setText("--");
		} else {
			((TextView) row.findViewById(R.id.savedLocationDistance)).setText(Utils.formatDistanceToDestinationWithUnits(actualLocation.distanceTo(savedLocation.getGpsLocation())));
		}
		((TextView) row.findViewById(R.id.savedLocationLat)).setText("Lat: " + savedLocation.getGpsLocation().getLatitude());
		((TextView) row.findViewById(R.id.savedLocationLong)).setText("Long: " + savedLocation.getGpsLocation().getLongitude());
		return row;
	}

	public void setSelectedElement(int position) {
		selectedElement = position;
		notifyDataSetChanged();

	}

	public int getSelectedElement() {
		if (isEmpty()) {
			return -1;
		} else {
			return (selectedElement >= savedData.size()) ? savedData.size() - 1 : selectedElement;
		}
	}

	public boolean isEmpty() {
		return savedData.isEmpty();
	}
}
