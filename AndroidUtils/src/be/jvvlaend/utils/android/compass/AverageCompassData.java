package be.jvvlaend.utils.android.compass;

import java.util.ArrayList;

public class AverageCompassData {
	private static final int CAPACITY = 10;
	private ArrayList<CompassData> dataList = new ArrayList<CompassData>(CAPACITY);

	public void add(CompassData compassData) {
		dataList.add(compassData);
		if (dataList.size() > CAPACITY) {
			dataList.remove(0);
		}
	}

	public CompassData getAverage() {
		float x = 0, y = 0, z = 0;
		int numberOfElements = dataList.size();
		if (numberOfElements == 0) {
			return null;
		}
		CompassData avgData = new CompassData(dataList.get(0).getSensorEvent());
		for (CompassData data : dataList) {
			x += data.getX();
			y += data.getY();
			z += data.getZ();
		}
		avgData.setX(x / numberOfElements);
		avgData.setY(y / numberOfElements);
		avgData.setZ(z / numberOfElements);
		return avgData;
	}
}
