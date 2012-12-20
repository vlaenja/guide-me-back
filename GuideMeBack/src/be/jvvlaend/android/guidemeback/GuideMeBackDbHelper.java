package be.jvvlaend.android.guidemeback;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GuideMeBackDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "GuideMeBack.db";
	private static final int DATABASE_VERSION = 1;

	public GuideMeBackDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createStatement = "CREATE TABLE IF NOT EXISTS " + GuideMeBackDbEntry.TABLE_NAME + " ( " + GuideMeBackDbEntry._ID + " INTEGER PRIMARY KEY, " + GuideMeBackDbEntry.COLUMN_OMSCHRIJVING + " TEXT, " + GuideMeBackDbEntry.COLUMN_PROVIDER
				+ " TEXT, " + GuideMeBackDbEntry.COLUMN_TIMESTAMP + " DATE, " + GuideMeBackDbEntry.COLUMN_LONGITUDE + " REAL, " + GuideMeBackDbEntry.COLUMN_LATITUDE + " REAL )";
		db.execSQL(createStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public ArrayList<SavedLocation> getSavedLocations() {
		ArrayList<SavedLocation> savedIds = new ArrayList<SavedLocation>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(GuideMeBackDbEntry.TABLE_NAME, GuideMeBackDbEntry.ALL_COLUMNS, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SavedLocation location = new SavedLocation(cursor.getString(cursor.getColumnIndex(GuideMeBackDbEntry._ID)));
			location.setOmschrijving(cursor.getString(cursor.getColumnIndex(GuideMeBackDbEntry.COLUMN_OMSCHRIJVING)));
			location.setProvider(cursor.getString(cursor.getColumnIndex(GuideMeBackDbEntry.COLUMN_PROVIDER)));
			location.setTime(cursor.getLong(cursor.getColumnIndex(GuideMeBackDbEntry.COLUMN_TIMESTAMP)));
			location.setLatitude(cursor.getDouble(cursor.getColumnIndex(GuideMeBackDbEntry.COLUMN_LATITUDE)));
			location.setLongitude(cursor.getDouble(cursor.getColumnIndex(GuideMeBackDbEntry.COLUMN_LONGITUDE)));
			savedIds.add(location);
			cursor.moveToNext();
		}
		this.close();
		return savedIds;
	}

	public void deleteSavedLocation(String id) {
		SQLiteDatabase db = getWritableDatabase();
		String[] values = { id };
		db.delete(GuideMeBackDbEntry.TABLE_NAME, GuideMeBackDbEntry._ID, values);
		this.close();
	}

	public void insertLocation(SavedLocation savedLocation) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(GuideMeBackDbEntry.COLUMN_OMSCHRIJVING, savedLocation.getOmschrijving());
		values.put(GuideMeBackDbEntry.COLUMN_PROVIDER, savedLocation.getProvider());
		values.put(GuideMeBackDbEntry.COLUMN_TIMESTAMP, savedLocation.getTime());
		values.put(GuideMeBackDbEntry.COLUMN_LATITUDE, savedLocation.getLatitude());
		values.put(GuideMeBackDbEntry.COLUMN_LONGITUDE, savedLocation.getLongitudee());

		db.insertOrThrow(GuideMeBackDbEntry.TABLE_NAME, null, values);
		db.close();
	}
}
