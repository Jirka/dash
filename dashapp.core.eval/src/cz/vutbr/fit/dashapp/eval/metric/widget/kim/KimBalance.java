package cz.vutbr.fit.dashapp.eval.metric.widget.kim;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class KimBalance extends AbstractWidgetMetric {
	
	public KimBalance() {
		super();
	}
	
	public KimBalance(GEType[] geTypes) {
		super(geTypes);
	}
	
	public double getHorizontalBalance(boolean[][] matrix) {
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		long upper = 0;
		int center = mH/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < mW; j++) {
				if(matrix[j][i]) {
					upper += diff;
				}
			}
			diff--;
		}
		
		long bottom = 0;
		if(mH % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < mH; i++) {
			for(int j = 0; j < mW; j++) {
				if(matrix[j][i]) {
					bottom += diff;
				}
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
	
	public double getVerticalBalance(boolean[][] matrix) {
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		long left = 0;
		int center = mW/2;
		int diff = center;
		for(int i = 0; i < center; i++) {
			for(int j = 0; j < mH; j++) {
				if(matrix[i][j]) {
					left += diff;
				}
			}
			diff--;
		}
		
		long right = 0;
		if(mW % 2 == 0) {
			center--;
		}
		diff = 1;
		for(int i = center+1; i < mW; i++) {
			for(int j = 0; j < mH; j++) {
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
	public MetricResult[] measure(Dashboard dashboard) {
		return measure(dashboard.getBooleanMatrix(getGeTypes()));
	}
	
	public MetricResult[] measure(boolean[][] matrix) {
		double BM_V = getVerticalBalance(matrix);
		double BM_H = getHorizontalBalance(matrix);
		return new MetricResult[] {
				new MetricResult("Balance", "BM'", 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0),
				new MetricResult("Vertical Balance", "BM_v'", BM_V),
				new MetricResult("Horizontal Balance", "BM_h'", BM_H)
		};
	}

}
