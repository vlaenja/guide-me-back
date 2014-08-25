package be.jvvlaend.android.guidemeback;

import java.io.Serializable;

import android.location.Location;

public class SavedLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id = null;
	private String omschrijving = null;
	private Location gpsLocation = new Location("");

	public SavedLocation(String id) {
		this.id = id;
	}

	public SavedLocation(Location location) {
		this.id = null;
		this.omschrijving = null;
		this.gpsLocation = location;
	}

	public String getId() {
		return id;
	}

	public Location getGpsLocation() {
		return gpsLocation;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public double getLatitude() {
		return gpsLocation.getLatitude();
	}

	public void setLatitude(double latitude) {
		gpsLocation.setLatitude(latitude);
	}

	public double getLongitude() {
		return gpsLocation.getLongitude();
	}

	public void setLongitude(double longitude) {
		gpsLocation.setLongitude(longitude);
	}

	public String getProvider() {
		return gpsLocation.getProvider();
	}

	public void setProvider(String provider) {
		gpsLocation.setProvider(provider);
	}

	public long getTime() {
		return gpsLocation.getTime();
	}

	public void setTime(long time) {
		gpsLocation.setTime(time);
	}
}
