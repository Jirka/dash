package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Unity extends AbstractMetric implements IMetric {
	
	public Unity(Dashboard dashboard, Type[] types) {
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
