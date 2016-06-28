package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Economy extends AbstractMetric implements IMetric {

	public Economy(Dashboard dashboard, Type[] types) {
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
