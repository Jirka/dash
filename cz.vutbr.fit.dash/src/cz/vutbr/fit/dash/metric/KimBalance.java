package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class KimBalance extends AbstractMetric implements IMetric {
	
	public KimBalance(Dashboard dashboard, Type[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "BM'";
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "", "V", "H" };
	}
	
	public double getHorizontalBalance(boolean[][] matrix) {
		long upper = 0;
		int center = dashboard.height/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < dashboard.width; j++) {
				if(matrix[j][i]) {
					upper += diff;
				}
			}
			diff--;
		}
		
		long bottom = 0;
		if(dashboard.height % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < dashboard.height; i++) {
			for(int j = 0; j < dashboard.width; j++) {
				if(matrix[j][i]) {
					bottom += diff;
				}
			}
			diff++;
		}
		
		System.out.println("upper1 = " + upper);
		System.out.println("bottom1 = " + bottom);
		
		double b = Math.max(upper, bottom);
		if(b == 0) {
			return 1;
		}
		//return Math.min(left, right)/b;
		return (upper-bottom)/b;
	}
	
	public double getVerticalBalance(boolean[][] matrix) {
		long left = 0;
		int center = dashboard.width/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < dashboard.height; j++) {
				if(matrix[i][j]) {
					left += diff;
				}
			}
			diff--;
		}
		
		long right = 0;
		if(dashboard.width % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < dashboard.width; i++) {
			for(int j = 0; j < dashboard.height; j++) {
				if(matrix[i][j]) {
					right += diff;
				}
			}
			diff++;
		}
		
		System.out.println("left1 = " + left);
		System.out.println("right1 = " + right);
		
		double b = Math.max(left, right);
		if(b == 0) {
			return 1;
		}
		//return Math.min(left, right)/b;
		return (left-right)/b;
	}

	@Override
	public Object measure() {
		boolean[][] matrix = dashboard.getMattrix(getTypes());
		double BM_V = getVerticalBalance(matrix);
		double BM_H = getHorizontalBalance(matrix);
		return new Object[] { 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0, BM_V, BM_H };
	}

}
