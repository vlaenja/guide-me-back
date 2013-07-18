package be.jvvlaend.android.guidemeback;

import android.provider.BaseColumns;

public class ParameterDbEntry implements BaseColumns {
	public static final String TABLE_NAME = "Parameters";

	public static final String COLUMN_PARAMETER = "Parameter";
	public static final String COLUMN_VALUE = "Value";
	public static final String[] ALL_COLUMNS = { _ID, COLUMN_PARAMETER, COLUMN_VALUE };

}
