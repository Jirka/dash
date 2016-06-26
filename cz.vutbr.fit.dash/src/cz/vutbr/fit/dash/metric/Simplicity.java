package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public class Simplicity extends AbstractMetric implements IMetric {

	public Simplicity(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getInicials() {
		return "SMM";
	}

	@Override
	public Object measure() {
		return 3.0/(dashboard.getHAP()+dashboard.getVAP()+dashboard.n());
	}

}
