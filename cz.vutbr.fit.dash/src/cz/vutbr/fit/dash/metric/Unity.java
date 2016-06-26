package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public class Unity extends AbstractMetric implements IMetric {
	
	public Unity(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getInicials() {
		return "UM";
	}

	@Override
	public Object measure() {
		int areas = dashboard.getElementsArea();
		double UM_form = 1 - (((double)(dashboard.getNumberOfSizes()-1))/dashboard.n());
		double UM_space = 1 - ((double) (dashboard.getLayoutArea()-areas))/(dashboard.area()-areas);
		
		return (Math.abs(UM_form)+Math.abs(UM_space))/2;
	}

}
