package cz.vutbr.fit.dashapp.eval.metric.raster.gray;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class GraySymmetry extends AbstractGrayRasterMetric {
	
	public double getHorizontalSymmetry(int[][] matrix) {
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		double hits = 0, misses = 0;
		double hit, miss;
		int center = mH/2;
		int up = center-1;
		int down;
		if(mH % 2 == 0) {
			down = center;
		} else {
			down = center+1;
			hits = mW;
		}
		
		while(up >= 0) {
			for (int i = 0; i < mW; i++) {
				miss = ((double) Math.abs(matrix[i][up]-matrix[i][down]))/255;
				hit = 1-miss;
				misses += miss;
				hits += hit;
			}
			up--;
			down++;
		}
		
		//return Math.min(left, right)/b;
		return hits/(hits+misses);
	}
	
	public double getVerticalSymmetry(int[][] matrix) {
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		double hits = 0, misses = 0;
		double hit, miss;
		int center = mW/2;
		int left = center-1;
		int right;
		if(mW % 2 == 0) {
			right = center;
		} else {
			right = center+1;
			hits = mH;
		}
		
		while(left >= 0) {
			for (int j = 0; j < mH; j++) {
				miss = ((double) Math.abs(matrix[left][j]-matrix[right][j]))/255;
				hit = 1-miss;
				misses += miss;
				hits += hit;
			}
			left--;
			right++;
		}
		
		//return Math.min(left, right)/b;
		return hits/(hits+misses);
	}

	@Override
	public MetricResult[] measureGrayMatrix(int[][] matrix) {
		double SM_V = getVerticalSymmetry(matrix);
		double SM_H = getHorizontalSymmetry(matrix);
		return new MetricResult[] {
				new MetricResult("Symmetry", "SYM", (Math.abs(SM_V)+Math.abs(SM_H))/2.0),
				new MetricResult("Vertical Symmetry", "SYM_v", SM_V),
				new MetricResult("Horizontal Symmetry", "SYM_h", SM_H)
		};
	}

}
