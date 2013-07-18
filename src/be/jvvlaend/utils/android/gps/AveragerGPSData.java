package be.jvvlaend.utils.android.gps;

import java.util.ArrayList;

import android.location.Location;

public class AveragerGPSData {
	private static final String AVERAGE = "AVERAGE";
	private static final int CAPACITY = 3;
	private ArrayList<Location> allData = new ArrayList<Location>(CAPACITY);

	public void add(Location location) {
		if (allData.size() == CAPACITY) {
			allData.remove(0);
		}
		allData.add(location);
	}

	public Location getAverage() {
		double altitude = 0;
		double longitude = 0;
		double latitude = 0;
		Location averageLocation = new Location(AVERAGE);
		int numberOfElements = allData.size();

		if (numberOfElements == 0) {
			return null;
		}
		for (Location loc : allData) {
			altitude += loc.getAltitude();
			longitude += loc.getLongitude();
			latitude += loc.getLatitude();
		}
		averageLocation.setAltitude(altitude / numberOfElements);
		averageLocation.setLatitude(latitude / numberOfElements);
		averageLocation.setLongitude(longitude / numberOfElements);
		return averageLocation;
	}
}
