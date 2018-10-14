package cz.vutbr.fit.dashapp.util.matrix;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class StatsUtils {
	
	public static class MeanStatistics {
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
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
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
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
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
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
	
	public static MeanStatistics meanStatistics(int[][] matrix) {
		MeanStatistics statistics = new MeanStatistics();
		statistics.mean = meanValue(matrix);
		statistics.variance = varianceValue(matrix, statistics.mean);
		statistics.stdev = stdevValue(matrix, statistics.variance);
		statistics.min = minValue(matrix);
		statistics.max = maxValue(matrix);
		return statistics;
	}

}
