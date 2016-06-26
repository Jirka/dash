package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public class Economy extends AbstractMetric implements IMetric {

	public Economy(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getInicials() {
		return "ECM";
	}

	@Override
	public Object measure() {
		return 1.0/dashboard.getNumberOfSizes();
	}

}
