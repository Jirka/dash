package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.util.MatrixUtils;

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
