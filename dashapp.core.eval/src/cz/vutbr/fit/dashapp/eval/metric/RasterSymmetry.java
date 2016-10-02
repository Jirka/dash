package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;

public class RasterSymmetry extends RasterMetric implements IMetric {

	public RasterSymmetry(Dashboard dashboard, int[][] matrix) {
		super(dashboard, matrix);
	}

	@Override
	public String getInicials() {
		return "SYM";
	}
	
	public String[] getSubNames() {
		return new String[] { "", "V", "H"/*, "R"*/ };
	};
	
	public double getHorizontalSymmetry() {
		
		int width = matrix.length;
		int height = matrix[0].length;
		
		double hits = 0, misses = 0;
		double hit, miss;
		int center = height/2;
		int up = center-1;
		int down;
		if(height % 2 == 0) {
			down = center;
		} else {
			down = center+1;
			hits = width;
		}
		
		while(up >= 0) {
			for (int i = 0; i < width; i++) {
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
	
	public double getVerticalSymmetry() {
		
		int width = matrix.length;
		int height = matrix[0].length;
		
		double hits = 0, misses = 0;
		double hit, miss;
		int center = width/2;
		int left = center-1;
		int right;
		if(width % 2 == 0) {
			right = center;
		} else {
			right = center+1;
			hits = height;
		}
		
		while(left >= 0) {
			for (int j = 0; j < height; j++) {
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
	public Object measure() {
		double SM_V = getVerticalSymmetry();
		double SM_H = getHorizontalSymmetry();
		return new Object[] { (Math.abs(SM_V)+Math.abs(SM_H))/2.0, SM_V, SM_H };
	}

}
