package cz.vutbr.fit.dash.eval.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;

public class Economy extends AbstractMetric implements IMetric {

	public Economy(Dashboard dashboard, GEType[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "ECM";
	}

	@Override
	public Object measure() {
		return 1.0/dashboard.getNumberOfSizes(getTypes());
	}

}
