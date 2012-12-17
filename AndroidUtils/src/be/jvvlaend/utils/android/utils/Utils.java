package be.jvvlaend.utils.android.utils;

public class Utils {

	public static float afrondenTotCijfersNaKomma(float value, int cijfersNaKomma) {
		return Float.valueOf(value * 10f).intValue() / 10f;
	}

	public static double afrondenTotCijfersNaKomma(double value, int cijfersNaKomma) {
		return Double.valueOf(value * 10).intValue() / 10;
	}
}
