package cz.vutbr.fit.dashapp.image;

public class MathUtils {
	
	private static final double LOG_e_2 = Math.log(2);
	private static final double LOG_10_2 = Math.log10(2);
	
	public static class MeanSatistics {
		public double mean;
		public double variance;
		public double stdev;
		public double min;
		public double max;
		
		@Override
		public String toString() {
			return mean + " " + variance + " " + stdev;
		}
		
		public String toStringRound() {
			return (int) mean + " " + (int) variance + " " + (int) stdev;
		}
	}
	
	public static double meanValue(int[][] matrix) {
		double mean = 0;
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		int size = mW*mH;
		
		if(size > 0) {
			for (int i = 0; i < mW; i++) {
				for (int j = 0; j < mH; j++) {
					mean += matrix[i][j];
				}
			}
			
			mean = mean/(size);
		}
		
		return mean;
	}
	
	public static double varianceValue(int[][] matrix, double mean) {
		double variance = 0.0;
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		int size = mW*mH;
		
		if(size > 0) {
			int act;
			for (int i = 0; i < mW; i++) {
				for (int j = 0; j < mH; j++) {
					act = matrix[i][j];
					variance += (mean-act)*(mean-act);
				}
			}
			variance = variance/(mW*mH);
		}
		
		return variance;
	}
	
	public static int minValue(int[][] matrix) {
		int min = Integer.MAX_VALUE;
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int act;
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				act = matrix[i][j];
				if(min > act) {
					min = act;
				}
			}
		}
		
		return min;
	}
	
	public static int maxValue(int[][] matrix) {
		int max = Integer.MIN_VALUE;
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int act;
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				act = matrix[i][j];
				if(max < act) {
					max = act;
				}
			}
		}
		
		return max;
	}
	
	public static double stdevValue(int[][] matrix, double variance) {
		return Math.sqrt(variance);
	}
	
	public static MeanSatistics meanStatistics(int[][] matrix) {
		MeanSatistics statistics = new MeanSatistics();
		statistics.mean = meanValue(matrix);
		statistics.variance = varianceValue(matrix, statistics.mean);
		statistics.stdev = stdevValue(matrix, statistics.variance);
		statistics.min = minValue(matrix);
		statistics.max = maxValue(matrix);
		return statistics;
	}
	
	public static double entrophy(double p) {
		double pp = 1-p;
		if(p == 0 || pp == 0) {
			return 0.0;
		}
		return -(pp*log2_via_e(pp)+p*log2_via_e(p));
	}
	
	public static double log2_via_e(double x) {
		return Math.log(x)/LOG_e_2;
	}
	
	public static double log2_via_10(double x) {
		return Math.log10(x)/LOG_10_2;
	}

}
