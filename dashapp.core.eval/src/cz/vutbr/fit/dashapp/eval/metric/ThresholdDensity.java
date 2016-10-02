package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;

public class ThresholdDensity extends RasterMetric implements IMetric {
	
	public ThresholdDensity(Dashboard dashboard, int[][] matrix) {
		super(dashboard, matrix);
	}

	@Override
	public String getInicials() {
		return "THD'";
	}
	
	public double calulateBlackPixels() {
		
		int width = matrix.length;
		int height = matrix[0].length;
		
		long sum = 0;
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(matrix[i][j] == 0) {
					sum++;
				}
			}
		}
		
		return sum;
	}

	@Override
	public Object measure() {
		return ((double) calulateBlackPixels())/(matrix.length*matrix[0].length)*100;
	}

}
