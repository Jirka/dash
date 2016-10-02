package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class Unity extends AbstractMetric implements IMetric {
	
	public Unity(Dashboard dashboard, GEType[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "UM";
	}

	@Override
	public Object measure() {
		int areas = dashboard.getElementsArea(getTypes());
		double UM_form = 1 - (((double)(dashboard.getNumberOfSizes(getTypes())-1))/dashboard.n(getTypes()));
		double UM_space = 1 - ((double) (dashboard.getLayoutArea(getTypes())-areas))/(dashboard.area()-areas);
		
		return (Math.abs(UM_form)+Math.abs(UM_space))/2;
	}

}
