package cz.vutbr.fit.dashapp.eval.metric.raster.gray;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

public class GrayBalance extends AbstractGrayRasterMetric {
	
	public double getHorizontalBalance(int matrix[][]) {
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		double upper = 0;
		int center = mH/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < mW; j++) {
				upper += diff*(255-matrix[j][i]);
			}
			diff--;
		}
		
		double bottom = 0;
		if(mH % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < mH; i++) {
			for(int j = 0; j < mW; j++) {
				bottom += diff*(255-matrix[j][i]);
			}
			diff++;
		}
		
		//System.out.println("upper1 = " + upper);
		//System.out.println("bottom1 = " + bottom);
		
		double b = Math.max(upper, bottom);
		if(b == 0) {
			return 1;
		}
		//return Math.min(left, right)/b;
		return (upper-bottom)/b;
	}
	
	public double getVerticalBalance(int matrix[][]) {
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		double left = 0;
		int center = mW/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < mH; j++) {
				left += diff*(255-matrix[i][j]);
			}
			diff--;
		}
		
		double right = 0;
		if(mW % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < mW; i++) {
			for(int j = 0; j < mH; j++) {
				right += diff*(255-matrix[i][j]);
			}
			diff++;
		}
		
		//System.out.println("left1 = " + left);
		//System.out.println("right1 = " + right);
		
		double b = Math.max(left, right);
		if(b == 0) {
			return 1;
		}
		//return Math.min(left, right)/b;
		return (left-right)/b;
	}

	@Override
	public MetricResult[] measureGrayMatrix(int[][] matrix) {
		double BM_V = getVerticalBalance(matrix);
		double BM_H = getHorizontalBalance(matrix);
		return new MetricResult[] {
				new MetricResult("Balance", "BM'", 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0),
				new MetricResult("Vertical Balance", "BM'", BM_V),
				new MetricResult("Horizontal Balance", "BM'", BM_H)
		};
	}

}
