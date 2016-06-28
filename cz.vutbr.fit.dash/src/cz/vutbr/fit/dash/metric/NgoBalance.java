package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.Constants;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class NgoBalance extends AbstractMetric implements IMetric {

	public NgoBalance(Dashboard dashboard, Type[] types) {
		super(dashboard, types);
	}
	
	public NgoBalance(Dashboard dashboard, Type[] types, String name) {
		super(dashboard, types, name);
	}

	@Override
	public String getInicials() {
		return "BM";
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "", "V", "H" };
	}
	
	public double getBalance(Dashboard dashboard, int dimension) {
		// vertical center of dashboard
		double center = dashboard.ownCenter(dimension);
		// initialize weights
		double weight1 = 0.0;
		double weight2 = 0.0;
		// count weights
		for (GraphicalElement ge : dashboard.getGraphicalElements(getTypes())) {
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
	public Object measure() {
		double BM_V = getBalance(dashboard, Constants.X);
		double BM_H = getBalance(dashboard, Constants.Y);
		return new Object[] { 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0, BM_V, BM_H };
	}

}
