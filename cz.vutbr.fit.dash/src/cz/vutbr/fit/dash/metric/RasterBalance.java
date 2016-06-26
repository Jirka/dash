package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public class RasterBalance extends RasterMetric implements IMetric {
	
	public RasterBalance(Dashboard dashboard, int[][] matrix) {
		super(dashboard, matrix);
	}

	@Override
	public String getInicials() {
		return "BM'";
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "", "V", "H" };
	}
	
	public double getHorizontalBalance() {
		
		int width = matrix.length;
		int height = matrix[0].length;
		
		double upper = 0;
		int center = height/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < width; j++) {
				upper += diff*(255-matrix[j][i]);
			}
			diff--;
		}
		
		double bottom = 0;
		if(height % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < height; i++) {
			for(int j = 0; j < width; j++) {
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
	
	public double getVerticalBalance() {
		
		int width = matrix.length;
		int height = matrix[0].length;
		
		double left = 0;
		int center = width/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < height; j++) {
				left += diff*(255-matrix[i][j]);
			}
			diff--;
		}
		
		double right = 0;
		if(width % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < width; i++) {
			for(int j = 0; j < height; j++) {
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
	public Object measure() {
		double BM_V = getVerticalBalance();
		double BM_H = getHorizontalBalance();
		return new Object[] { 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0, BM_V, BM_H };
	}

}
