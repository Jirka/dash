package cz.vutbr.fit.dashapp.image;

public class MathUtils {
	
	private static final double LOG_e_2 = Math.log(2);
	private static final double LOG_10_2 = Math.log10(2);
	
	public static double entrophy(double p) {
		double pp = 1-p;
		return -(pp*log2_via_e(pp)+p*log2_via_e(p));
	}
	
	public static double log2_via_e(double x) {
		return Math.log(x)/LOG_e_2;
	}
	
	public static double log2_via_10(double x) {
		return Math.log10(x)/LOG_10_2;
	}

}
