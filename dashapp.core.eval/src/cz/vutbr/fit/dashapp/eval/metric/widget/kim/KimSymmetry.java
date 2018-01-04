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
public class KimSymmetry extends AbstractWidgetMetric {
	
	public KimSymmetry() {
		super();
	}
	
	public KimSymmetry(GEType[] geTypes) {
		super(geTypes);
	}

	public double getHorizontalSymmetry(boolean[][] matrix) {
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		long hit = 0, miss = 0;
		int center = mH/2;
		int up = center-1;
		int down;
		if(mH % 2 == 0) {
			down = center;
		} else {
			down = center+1;
			hit = mW;
		}
		
		while(up >= 0) {
			for (int i = 0; i < mW; i++) {
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
	
	public double getVerticalSymmetry(boolean[][] matrix) {
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int center = mW/2;
		
		long hit = 0, miss = 0;
		int left = center-1;
		int right;
		if(mW % 2 == 0) {
			right = center;
		} else {
			right = center+1;
			hit = mH;
		}
		
		while(left >= 0) {
			for (int j = 0; j < mH; j++) {
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
	public MetricResult[] measure(Dashboard dashboard) {
		return measure(dashboard.getBooleanMatrix(getGeTypes()));
	}
	
	public MetricResult[] measure(boolean[][] matrix) {
		double SM_V = getVerticalSymmetry(matrix);
		double SM_H = getHorizontalSymmetry(matrix);
		return new MetricResult[] {
				new MetricResult("Symmetry", "SYM", (Math.abs(SM_V)+Math.abs(SM_H))/2.0),
				new MetricResult("Symmetry", "SYM_v", SM_V),
				new MetricResult("Symmetry", "SYM_h", SM_H)
		};
	}

}
