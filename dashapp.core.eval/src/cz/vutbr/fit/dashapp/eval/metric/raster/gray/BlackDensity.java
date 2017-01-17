package cz.vutbr.fit.dashapp.eval.metric.raster.gray;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

public class BlackDensity extends AbstractGrayRasterMetric {
	
	public double calulateBlackPixels(int matrix[][]) {
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		long sum = 0;
		for(int i = 0; i < mW; i++) {
			for(int j = 0; j < mH; j++) {
				if(matrix[i][j] == 0) {
					sum++;
				}
			}
		}
		
		return sum;
	}

	@Override
	public MetricResult[] measureGrayMatrix(int[][] matrix) {
		return new MetricResult[] {
				new MetricResult("Threshold Density", "THD'", ((double) calulateBlackPixels(matrix))/(matrix.length*matrix[0].length)*100)
		};
	}

}
