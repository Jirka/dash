package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public class Density extends AbstractMetric implements IMetric {

	public Density(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getInicials() {
		return "DM";
	}

	@Override
	public Object measure() {
		return 1.0-2*Math.abs(0.5-(((double) dashboard.getElementsArea())/dashboard.area()));
	}

}
