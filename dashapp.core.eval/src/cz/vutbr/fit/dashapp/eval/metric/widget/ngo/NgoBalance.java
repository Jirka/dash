package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoBalance extends AbstractWidgetMetric {
	
	public NgoBalance() {
		super();
	}
	
	public NgoBalance(GEType[] geTypes) {
		super(geTypes);
	}
	
	public double getBalance(Dashboard dashboard, GEType[] types, int dimension) {
		// vertical center of dashboard
		double center = dashboard.halfSize(dimension);
		// initialize weights
		double weight1 = 0.0;
		double weight2 = 0.0;
		// count weights
		for (GraphicalElement ge : dashboard.getChildren(types)) {
			double distanceCenter = ge.center(dimension) - center;
			if(distanceCenter < 0) {
				// left side
				weight1 += ge.area()*(-distanceCenter);
			} else if(distanceCenter > 0) {
				// ride side
				weight2 += ge.area()*(distanceCenter);
			}
		}
		// return balance for particular dimension
		double b = Math.max(Math.abs(weight1), Math.abs(weight2));
		if(b == 0) {
			// there are no objects to compare -> balanced
			return 0.0;
		}
		return (weight1-weight2)/b;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		double BM_V = getBalance(dashboard, getGeTypes(), Constants.X);
		double BM_H = getBalance(dashboard, getGeTypes(), Constants.Y);
		return new MetricResult[] {
				new MetricResult("Balance", "BM", 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0),
				new MetricResult("Vertical Balance", "BM_v", BM_V),
				new MetricResult("Horizontal Balance", "BM_h", BM_H)
		};
	}

}
