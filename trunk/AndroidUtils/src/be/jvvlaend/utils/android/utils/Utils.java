package be.jvvlaend.utils.android.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static float afrondenTotCijfersNaKomma(float value, int cijfersNaKomma) {
		return Float.valueOf(value * 10f).intValue() / 10f;
	}

	public static double afrondenTotCijfersNaKomma(double value, int cijfersNaKomma) {
		return Double.valueOf(value * 10).intValue() / 10;
	}

	public static String formatDate(long timeStamp) {
		Date date = new Date();
		date.setTime(timeStamp);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dateFormat.format(date);
	}
}
