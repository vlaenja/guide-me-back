package be.jvvlaend.android.guidemeback;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}
}
