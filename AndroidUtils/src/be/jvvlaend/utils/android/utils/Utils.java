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

	public static String formatDistanceToDestinationWithUnits(float distanceTo) {
		int distance = Float.valueOf(distanceTo).intValue();
		if (distance < 10000) {
			return String.valueOf(distance) + " m";
		} else {
			return String.valueOf(Utils.afrondenTotCijfersNaKomma(distance / 1000f, 2)) + " Km";
		}
	}

}
