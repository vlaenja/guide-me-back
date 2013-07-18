package be.jvvlaend.android.guidemeback;

import android.provider.BaseColumns;

public class GuideMeBackDbEntry implements BaseColumns {
	public static final String TABLE_NAME = "Positions";

	public static final String COLUMN_OMSCHRIJVING = "Omschrijving";
	public static final String COLUMN_TIMESTAMP = "Timestamp";
	public static final String COLUMN_LATITUDE = "Latitude";
	public static final String COLUMN_LONGITUDE = "Longitude";
	public static final String COLUMN_PROVIDER = "Provider";
	public static final String[] ALL_COLUMNS = { _ID, COLUMN_OMSCHRIJVING, COLUMN_TIMESTAMP, COLUMN_PROVIDER, COLUMN_LATITUDE, COLUMN_LONGITUDE };

}
