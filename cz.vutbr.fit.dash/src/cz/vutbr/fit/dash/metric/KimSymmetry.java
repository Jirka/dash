package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class KimSymmetry extends AbstractMetric implements IMetric {
	
	private boolean[][] matrix;

	public KimSymmetry(Dashboard dashboard, Type[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "SYM";
	}
	
	public String[] getSubNames() {
		return new String[] { "", "V", "H"/*, "R"*/ };
	};
	
	public void initMatrix(Type[] types) {
		if(matrix == null) {
			matrix = dashboard.getMattrix(types);
		}
	}
	
	public double getHorizontalSymmetry() {
		initMatrix(getTypes());
		long hit = 0, miss = 0;
		int center = dashboard.height/2;
		int up = center-1;
		int down;
		if(dashboard.height % 2 == 0) {
			down = center;
		} else {
			down = center+1;
			hit = dashboard.width;
		}
		
		while(up >= 0) {
			for (int i = 0; i < dashboard.width; i++) {
				if(matrix[i][up] == matrix[i][down]) {
					hit++;
				} else {
					miss++;
				}
			}
			up--;
			down++;
		}
		
		//return Math.min(left, right)/b;
		return hit/(double) (hit+miss);
	}
	
	public double getVerticalSymmetry() {
		initMatrix(getTypes());
		int center = dashboard.width/2;
		
		long hit = 0, miss = 0;
		int left = center-1;
		int right;
		if(dashboard.width % 2 == 0) {
			right = center;
		} else {
			right = center+1;
			hit = dashboard.height;
		}
		
		while(left >= 0) {
			for (int j = 0; j < dashboard.height; j++) {
				if(matrix[left][j] == matrix[right][j]) {
					hit++;
				} else {
					miss++;
				}
			}
			left--;
			right++;
		}
		
		//return Math.min(left, right)/b;
		return hit/(double) (hit+miss);
	}

	@Override
	public Object measure() {
		double SM_V = getVerticalSymmetry();
		double SM_H = getHorizontalSymmetry();
		return new Object[] { (Math.abs(SM_V)+Math.abs(SM_H))/2.0, SM_V, SM_H };
	}

}
