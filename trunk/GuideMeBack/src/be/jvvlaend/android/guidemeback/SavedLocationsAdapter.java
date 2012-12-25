package be.jvvlaend.android.guidemeback;

import java.util.List;

import android.app.Activity;
import android.content.Context;
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

	public SavedLocationsAdapter(Context context, int layoutResourceId, List<SavedLocation> savedData) {
		super(context, layoutResourceId, savedData);
		this.context = context;
		this.savedData = savedData;
		this.layoutResourceId = layoutResourceId;
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
