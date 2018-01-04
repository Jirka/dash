package cz.vutbr.fit.dashapp.eval.metric.raster.gray;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * It expects the input matrix to be in the black and white color space.
 * 
 * @author Jiri Hynek
 *
 */
public class BlackDensity extends AbstractGrayRasterMetric {
	
	public double calulateBlackPixels(int matrix[][]) {
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
				new MetricResult("Threshold Density", "THD'", ((double) calulateBlackPixels(matrix))/(MatrixUtils.area(matrix))*100)
		};
	}

}
