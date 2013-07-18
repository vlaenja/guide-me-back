package be.jvvlaend.android.guidemeback;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParameterDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Parameter.db";
	private static final int DATABASE_VERSION = 1;

	public ParameterDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createStatement = "CREATE TABLE IF NOT EXISTS " + ParameterDbEntry.TABLE_NAME + " ( " + ParameterDbEntry._ID + " INTEGER PRIMARY KEY, " + ParameterDbEntry.COLUMN_PARAMETER + " TEXT, "
				+ ParameterDbEntry.COLUMN_VALUE + " TEXT )";
		db.execSQL(createStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public boolean getParameter(String param, boolean defaultValue) {
		return Boolean.valueOf(getParameter(param, String.valueOf(defaultValue)));
	}

	public void setParameter(String param, boolean value) {
		setParameter(param, String.valueOf(value));
	}

	public void setParameter(String param, String value) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(ParameterDbEntry.COLUMN_PARAMETER, param);
			values.put(ParameterDbEntry.COLUMN_VALUE, value);
			if (db.update(ParameterDbEntry.TABLE_NAME, values, ParameterDbEntry.COLUMN_PARAMETER + "='" + param + "'", null) == 0) {
				db.insertOrThrow(ParameterDbEntry.TABLE_NAME, null, values);
			}
		} finally {
			db.close();
		}
	}

	public String getParameter(String param, String defaultValue) {
		SQLiteDatabase db = getReadableDatabase();
		try {
			Cursor cursor = db.query(ParameterDbEntry.TABLE_NAME, ParameterDbEntry.ALL_COLUMNS, ParameterDbEntry.COLUMN_PARAMETER + "=?", new String[] { param }, null, null, null);
			cursor.moveToFirst();
			if (cursor.isAfterLast()) {
				return defaultValue;
			}
			return cursor.getString(cursor.getColumnIndex(ParameterDbEntry.COLUMN_VALUE));
		} finally {
			db.close();
		}
	}

	public static void setParameterScreenOn(Context context, boolean value) {
		ParameterDbHelper db = null;
		try {
			db = new ParameterDbHelper(context);
			db.setParameter(Constant.SCREEN_ON, value);
		} finally {
			if (db != null) {
				db.close();
			}
			db = null;
		}
	}

	public static boolean getParameterScreenOn(Context context) {
		ParameterDbHelper db = null;
		try {
			db = new ParameterDbHelper(context);
			return db.getParameter(Constant.SCREEN_ON, true);
		} finally {
			if (db != null) {
				db.close();
			}
			db = null;
		}
	}

}
