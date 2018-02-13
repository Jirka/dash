package cz.vutbr.fit.dashapp.util;

public class MathUtils {

	public static final double LOG_e_2 = Math.log(2);
	public static final double LOG_10_2 = Math.log10(2);
	
	public static double log2_via_e(double x) {
		return Math.log(x)/LOG_e_2;
	}
	
	public static double log2_via_10(double x) {
		return Math.log10(x)/LOG_10_2;
	}
	
	public static double entrophy(double p) {
		double pp = 1-p;
		if(p == 0 || pp == 0) {
			return 0.0;
		}
		return -(pp*log2_via_e(pp)+p*log2_via_e(p));
	}
	
	public static double adaptNormalized(double act, double min, double max) {
		// a = (max-min)/(oldMax-oldMin)
		// (a * act) + (oldMax - a * oldMax)
		// oldMax = 1
		// oldMin = 0
		// (max-min)/1 * act + 1-(max-min)/1 * 1
		// (max-min) * act + 1 - (max-min)
		double a = max-min;
		return a*act+(1-a);
	}
	
	public static int roundInRange(int p, int min, int max) {
		return Math.min(max, Math.max(min, p));
	}
	
	public static boolean isInRange(int p, int min, int max) {
		return p >= min && p <= max;
	}

}
