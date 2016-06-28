package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Simplicity extends AbstractMetric implements IMetric {

	public Simplicity(Dashboard dashboard, Type[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "SMM";
	}

	@Override
	public Object measure() {
		return 3.0/(dashboard.getHAP(getTypes())+dashboard.getVAP(getTypes())+dashboard.n(getTypes()));
	}

}
